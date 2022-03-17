package org.thingsboard.server.dao.deviceoeeeveryhour;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.deviceoeeeveryhour.DeviceOeeEveryHour;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.productioncalender.ProductionCalender;
import org.thingsboard.server.common.data.statisticoee.StatisticOee;
import org.thingsboard.server.common.data.vo.DeviceCapacityVo;
import org.thingsboard.server.dao.device.DeviceDao;
import org.thingsboard.server.dao.hs.service.DictDeviceService;
import org.thingsboard.server.dao.productioncalender.ProductionCalenderDao;
import org.thingsboard.server.dao.sql.role.service.BulletinBoardSvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 计算OEE
 */
@Service
@Slf4j
public class DeviceOeeEveryHourServiceImpl implements DeviceOeeEveryHourService {
    private final String KEY = "statistic_oee_by_timed_task_lock_";

    @Autowired
    private BulletinBoardSvc bulletinBoardSvc;

    @Autowired
    private DictDeviceService dictDeviceService;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private ProductionCalenderDao productionCalenderDao;

    @Autowired
    private DeviceOeeEveryHourDao deviceOeeEveryHourDao;
/*
    //@Resource(name ="redisLockCommon")
    private RedisLockCommon redisLock;

    DeviceOeeEveryHourServiceImpl(){
        redisLock = new RedisLockCommon();
    }*/


    /**
     * 定时任务
     * 定时每天晚上24点
     * 执行统计当天设备每个小时OEE
     */
    @Scheduled(cron = "59 59 23 * * ?")
    public void statisticOeeByTimedTask() {
        //竞争锁
        /*if(!redisLock.decrementProductStore(KEY)){
            log.info("当前服务器节点未竞争到锁，不执行statisticOeeByTimedTask");
            return;
        }*/
        log.info("[statisticOeeByTimedTask]统计当天时间00:00:00到23:59:59之间每个设备每个小时OEE的定时任务执行啦！！！");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long cu = System.currentTimeMillis();//当前时间毫秒数
        long startTime = cu / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        long endTime = startTime + 24 * 60 * 60 * 1000 - 1;//今天23点59分59秒的毫秒数
        log.info("当前任务开始时间：" + sdf.format(new Date(Long.parseLong(String.valueOf(cu)))));
        log.info("OEE开始时间：" + sdf.format(new Date(Long.parseLong(String.valueOf(startTime)))));
        log.info("OEE结束时间：" + sdf.format(new Date(Long.parseLong(String.valueOf(endTime)))));
        this.statisticOeeByTimedTask(startTime,endTime);
        log.info("[statisticOeeByTimedTask]统计当天时间00:00:00到23:59:59之间每个设备每个小时OEE的定时任务执行完成 !!!：" );
        log.info("当前任务结束时间：" + sdf.format(new Date(Long.parseLong(String.valueOf(System.currentTimeMillis())))));
    }

    /**
     * 统计所有设备
     * @param startTime
     * @param endTime
     */
    public void statisticOeeByTimedTask(Long startTime,Long endTime){
        Device device = new Device();
        device.setFilterGatewayFlag(true);
        List<Device> deviceListByCdn = deviceDao.findDeviceListByCdn(device);
        if (!CollectionUtils.isEmpty(deviceListByCdn)) {
            //按小时拆分时间区间
            List<Long> dateList = this.cutDate(startTime, endTime);
            for (Device deviceIter:deviceListByCdn) {
                List<StatisticOee> statisticOees = this.statisticOeeDeviceEveryHour(dateList, deviceIter.getId().getId());
                if(!CollectionUtils.isEmpty(statisticOees)){
                    for (StatisticOee o :statisticOees) {
                        try {
                            deviceOeeEveryHourDao.save(new DeviceOeeEveryHour(deviceIter.getId().getId(),o.getTimeHours(),o.getOeeValue(),deviceIter.getTenantId().getId()));
                        } catch (ThingsboardException e) {
                            log.error("保存OEE报错！",e);
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

    /**
     * 执行（指定时间区间）所有设备每小时OEE同步
     * @param statisticOee
     * @return
     * @throws ThingsboardException
     */
    @Override
    public void statisticOeeByAnyTime(StatisticOee statisticOee) throws ThingsboardException{
        this.statisticOeeByTimedTask(statisticOee.getStartTime(),statisticOee.getEndTime());
    }


    /**
     * 查询设备当天OEE
     * 设备当天OEE需班次时间结束后运算，当天班次未结束取前一天的值
     *
     * @param deviceId
     * @return
     */
    @Override
    public BigDecimal getStatisticOeeDeviceByCurrentDay(UUID deviceId) {
        BigDecimal oeeValue = new BigDecimal(0);
        //获取当天时间戳
        long cu = System.currentTimeMillis();//当前时间毫秒数
        long zero = cu / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        long twelve = zero + 24 * 60 * 60 * 1000 - 1;//今天23点59分59秒的毫秒数
        long yesterdayZero = System.currentTimeMillis() - 24 * 60 * 60 * 1000;//昨天0点的毫秒数
        long yesterdayTwelve = System.currentTimeMillis() - 24 * 60 * 60 * 1000;//昨天23:59:59点的毫秒数
        if (deviceId != null) {
            //1.判断设备当天班次是否结束
            //查询时间段生产日历
            List<ProductionCalender> deviceByCurrentDay = productionCalenderDao.getDeviceByTimenterval(deviceId, zero, twelve);
            if (!CollectionUtils.isEmpty(deviceByCurrentDay)) {
                if (cu >= deviceByCurrentDay.get(0).getEndTime()) {
                    oeeValue = oeeValue.add(this.getDeviceOeeValue(deviceId, zero, twelve));
                } else {
                    //取昨天
                    deviceByCurrentDay = productionCalenderDao.getDeviceByTimenterval(deviceId, yesterdayZero, yesterdayTwelve);
                    if (!CollectionUtils.isEmpty(deviceByCurrentDay)) {
                        oeeValue = oeeValue.add(this.getDeviceOeeValue(deviceId, yesterdayZero, yesterdayTwelve));
                    }
                }
            }
        }
        return oeeValue;

    }


    /**
     * 查询OEE计算历史，返回每小时的值
     * @param statisticOee
     * @return
     * @throws ThingsboardException
     */
    @Override
    public List<StatisticOee> getStatisticOeeEveryHourList(StatisticOee statisticOee) throws ThingsboardException {
        List<StatisticOee> statisticOeeList = new ArrayList<>();
        List<DeviceOeeEveryHour> deviceOeeEveryHours = deviceOeeEveryHourDao.findAllByCdn(new DeviceOeeEveryHour(statisticOee), "ts", "ASC");
        if(!CollectionUtils.isEmpty(deviceOeeEveryHours)){
            //如果是工厂和车间需要合并成每个小时的
            Map<Long,BigDecimal> map = new LinkedHashMap<>();
            for (DeviceOeeEveryHour iter:deviceOeeEveryHours ) {
                if (map != null && map.containsKey(iter.getTs())) {
                    map.put(iter.getTs(),map.get(iter.getTs()).add(iter.getOeeValue()));
                }else {
                    map.put(iter.getTs(),iter.getOeeValue());
                }
            }
            //把整理后
            for(Map.Entry<Long,BigDecimal> entry : map.entrySet()){
                statisticOeeList.add(new StatisticOee(entry.getKey(),entry.getValue()));
            }
        }
        return statisticOeeList;
    }


    /**
     * 查询OEE实时数据，返回每小时的值
     * @param statisticOee
     * @return
     * @throws ThingsboardException
     */
    @Override
    public List<StatisticOee> getStatisticOeeListByRealTime(StatisticOee statisticOee) throws ThingsboardException {
        List<StatisticOee> result = new ArrayList<>();
        //按小时拆分时间区间
        List<Long> dateList = this.cutDate(statisticOee.getStartTime(), statisticOee.getEndTime());

        if (statisticOee.getDeviceId() != null) {
            return this.statisticOeeDeviceEveryHour(dateList, statisticOee.getDeviceId());
        } else {
            //集团/工厂/车间OEE）
            result = this.statisticOeeEveryHour(statisticOee, dateList);
        }
        return result;
    }


    /**
     * 查设备
     *
     * @param statisticOee
     * @return
     */
    private List<Device> getDeviceList(StatisticOee statisticOee) {
        Device device = new Device();
        device.setTenantId(new TenantId(statisticOee.getTenantId()));
        device.setFactoryId(statisticOee.getFactoryId());
        device.setWorkshopId(statisticOee.getWorkshopId());
        device.setFilterGatewayFlag(true);
        List<Device> deviceListByCdn = deviceDao.findDeviceListByCdn(device);
        return deviceListByCdn;
    }

    /**
     * 计算集团/工厂/车间OEE
     * 集团/工厂/车间OEE 是其下面所有设备每个小时的总和除以设备总数
     *
     * @param statisticOeeQry
     * @param dateList
     * @return
     */
    private List<StatisticOee> statisticOeeEveryHour(StatisticOee statisticOeeQry, List<Long> dateList) {
        List<StatisticOee> result = new ArrayList<>();

        List<Device> deviceListByCdn = this.getDeviceList(statisticOeeQry);
        if (deviceListByCdn != null) {
            //计算每个小时的总和
            for (int i = 0; i < dateList.size(); i++) {
                //排除最后一个
                if (i + 1 == dateList.size()) {
                    break;
                }

                //每个小时设备OEE值总和
                BigDecimal oeeValue = new BigDecimal(0);

                for (Device deviceCdn : deviceListByCdn) {
                    oeeValue = oeeValue.add(this.getDeviceOeeValue(deviceCdn.getId().getId(), dateList.get(i), dateList.get(i + 1)));
                }

                StatisticOee statisticOee = new StatisticOee();

                statisticOee.setOeeValue(oeeValue.divide(new BigDecimal(deviceListByCdn.size()),2, BigDecimal.ROUND_HALF_UP));
                statisticOee.setTimeHours(dateList.get(i));
                result.add(statisticOee);
            }
        }
        return result;
    }


    /**
     * 计算设备OEE  （设备每小时OEE）
     * 公式：良品数/(计划工作时间*额定产能)
     * 良品数：订单计划里面的实际产量值总和
     * 计划工作时间： 选择日期范围内班次时间总和（班次时间：生产日历的  时间区间，计算小时）
     * 额定产能：设备的设备字典中的标准产能
     *
     * @param dateList
     * @param deviceId
     * @return
     */
    public List<StatisticOee> statisticOeeDeviceEveryHour(List<Long> dateList, UUID deviceId) {
        List<StatisticOee> result = new ArrayList<>();
        for (int i = 0; i < dateList.size(); i++) {
            //排除最后一个
            if (i + 1 == dateList.size()) {
                break;
            }
            StatisticOee statisticOee = new StatisticOee();
            statisticOee.setOeeValue(this.getDeviceOeeValue(deviceId, dateList.get(i), dateList.get(i + 1)));
            statisticOee.setTimeHours(dateList.get(i));
            result.add(statisticOee);
        }
        return result;
    }

    /**
     * 计算设备一个时间区间内的OEE
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public BigDecimal getDeviceOeeValue(UUID deviceId, Long startTime, Long endTime) {
        BigDecimal deviceOeeValue = new BigDecimal(0);

        //1.良品数：订单计划里面的实际产量值总和
        BigDecimal deviceOutputReality = this.getDeviceOutput(startTime, endTime, deviceId);
        //2.(设备标准产能 * 设备日历中的时间）
        BigDecimal deviceOutputPredict = this.getStandardCapacity(startTime, endTime, deviceId);
        //3.设备OEE
        if (deviceOutputPredict.compareTo(BigDecimal.ZERO) == 0) {
            return deviceOeeValue;
        } else {
            deviceOeeValue = deviceOutputReality.divide(deviceOutputPredict,2, BigDecimal.ROUND_HALF_UP);
        }
        return deviceOeeValue;
    }


    /**
     * 计算时间区间内设备的 设备标准产能 * 设备日历中的时间
     *
     * @param startTime
     * @param endTime
     * @param deviceId
     * @return
     */
    private BigDecimal getStandardCapacity(Long startTime, Long endTime, UUID deviceId) {
        //设备标准产能 * 设备日历中的时间
        BigDecimal deviceOutputPredict = new BigDecimal(0);

        //设备标准产能
        UUID dictDeviceId = deviceDao.getDeviceInfo(deviceId).getDictDeviceId();
        if (dictDeviceId != null) {
            BigDecimal ratedCapacity = dictDeviceService.findById(dictDeviceId).getRatedCapacity();
            if (ratedCapacity.compareTo(BigDecimal.ZERO) == 0) {
                return deviceOutputPredict;
            }
            //单个设备生产日历总时间
            BigDecimal time = new BigDecimal(0);

            //生产日历时间
            List<ProductionCalender> historyList = productionCalenderDao.getDeviceByTimenterval(deviceId, startTime, endTime);
            if (!CollectionUtils.isEmpty(historyList)) {
                for (ProductionCalender pc : historyList) {
                    Map<String, Long> mapTime = this.intersectionTime(pc.getStartTime(), pc.getEndTime(), startTime, endTime);
                    time = time.add(this.timeDifferenceForHours(mapTime.get("startTime"), mapTime.get("endTime")));
                }
            }
            //单个设备 预计产量
            deviceOutputPredict = deviceOutputPredict.add(ratedCapacity.multiply(time));
        }
        return deviceOutputPredict;
    }

    /**
     * 查询时间区间内设备产能
     *
     * @param startTime
     * @param endTime
     * @param deviceId
     * @return
     */
    private BigDecimal getDeviceOutput(Long startTime, Long endTime, UUID deviceId) {
        BigDecimal deviceOutputReality = new BigDecimal(0);
        List<DeviceCapacityVo> deviceCapacityVoList = new ArrayList<>();
        deviceCapacityVoList.add(new DeviceCapacityVo(deviceId, startTime, endTime));
        Map<UUID, String> map = bulletinBoardSvc.queryCapacityValueByDeviceIdAndTime(deviceCapacityVoList);
        deviceOutputReality = deviceOutputReality.add(new BigDecimal(map.get(deviceId)));
        return deviceOutputReality;
    }

    /**
     * 拆分时间段，并把开始结束时间不整点的改成区间
     *
     * @param start
     * @param end
     * @return
     */
    public List<Long> cutDate(Long start, Long end) {
        List<Long> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //第一个时间区间
            result.add(start);
            long startEnd = this.cleanHoursMinScn(this.getNextHours(start, 1)).getTime();
            log.info(sdf.format(new Date(Long.parseLong(String.valueOf(start)))));

            //中间区间
            Date dBegin = new Date(Long.parseLong(String.valueOf(startEnd)));
            Date dEnd = new Date(Long.parseLong(String.valueOf(end)));
            List<Date> dates = this.findDates("H", dBegin, dEnd);
            if (!CollectionUtils.isEmpty(dates)) {
                for (Date date : dates) {
                    log.info(sdf.format(date.getTime()));
                    result.add(date.getTime());
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 切割时间段
     * 支持每月/每天/每小时/每分钟交易金额(可分应用平台统计)
     *
     * @param dateType 日期类型 M(每月)/D(每天)/H(每小时)/N(每分钟)
     *                 M：日期段应为当年月份以内 且 日期必须是01 时分秒必须是 00:00:00  例如：2016-06-01 00:00:00 2016-10-01 00:00:00
     *                 D: 日期段应为一月内 且 日期应当是01或31  时分秒必须是 00:00:00   例如：2016-10-01 00:00:00 2016-10-31 00:00:00
     *                 H：日期段应为一天内  且 时分秒必须是 00:00:00   例如：2016-10-01 00:00:00 2016-10-02 00:00:00
     *                 T：日期段应为一小时内  日期应相同 且 分秒必须是 xx:00:00   例如：2016-10-02 22:00:00 2016-10-02 23:00:00
     * @param dateType 交易类型 M/D/H/T -->每月/每天/每小时/每分钟
     * @param dBegin   yyyy-MM-dd HH:mm:ss
     * @param dEnd     yyyy-MM-dd HH:mm:ss
     * @return
     */
    public List<Date> findDates(String dateType, Date dBegin, Date dEnd) throws Exception {
        List<Date> listDate = new ArrayList<>();
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        listDate.add(calBegin.getTime());
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (calEnd.after(calBegin)) {
            switch (dateType) {
                case "M":
                    calBegin.add(Calendar.MONTH, 1);
                    break;
                case "D":
                    calBegin.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                case "H":
                    calBegin.add(Calendar.HOUR, 1);
                    break;
                case "T":
                    calBegin.add(Calendar.MINUTE, 1);
                    break;
                default:
                    return null;
            }
            if (calEnd.after(calBegin))
                listDate.add(calBegin.getTime());
            else {
                listDate.add(calEnd.getTime());
                break;
            }
        }
        return listDate;
    }

    /**
     * 获取指定时间戳前后n小时的时间
     *
     * @param dateTime
     * @return
     */
    private Date getNextHours(Long dateTime, Integer num) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTimeStr = sdf.format(new Date(Long.parseLong(String.valueOf(dateTime))));
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dateTimeStr));
        c.add(Calendar.HOUR, num);
        Date time = c.getTime();
        //String format = sdf.format(c.getTime());
        return time;
    }

    /**
     * 把时间的分秒值置为零
     *
     * @param dateTime
     * @return
     */
    private Date cleanHoursMinScn(Date dateTime) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //System.out.printf("%1$tF %1$tT\n", calendar.getTime());
        Date time = calendar.getTime();
        return time;
    }

    /**
     * 计算时间交集
     *
     * @param actualStartTime 实际数据-开始时间
     * @param actualEndTime   实际数据-结束时间
     * @param startTimeQry    查询条件开始时间
     * @param endTimeQry      查询条件结束时间
     * @return
     */
    public Map<String, Long> intersectionTime(Long actualStartTime, Long actualEndTime, Long startTimeQry, Long endTimeQry) {
        Map<String, Long> map = new HashMap<>();
        map.put("startTime", new Long(0));
        map.put("endTime", new Long(0));

        Long startTime = new Long(0);
        Long endTime = new Long(0);

        //时间要取交叉时间
        if (startTimeQry < actualStartTime && actualEndTime < endTimeQry) {
            startTime = actualStartTime;
            endTime = actualEndTime;
        }
        if (startTimeQry < actualStartTime && actualStartTime < endTimeQry && endTimeQry < actualEndTime) {
            startTime = actualStartTime;
            endTime = endTimeQry;
        }
        if (actualStartTime < startTimeQry && startTimeQry < actualEndTime && actualEndTime < endTimeQry) {
            startTime = startTimeQry;
            endTime = actualEndTime;
        }
        if (actualStartTime < startTimeQry && endTimeQry < actualStartTime) {
            startTime = startTimeQry;
            endTime = endTimeQry;
        }
        map.put("startTime", startTime);
        map.put("endTime", endTime);

        return map;
    }

    /**
     * 计算时间戳相间隔的小时数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public BigDecimal timeDifferenceForHours(Long startTime, Long endTime) {
        Long timeDifference = endTime - startTime;
        Long day = timeDifference / (24 * 60 * 60 * 1000);
        Long hour = (timeDifference / (60 * 60 * 1000) - day * 24);
        return new BigDecimal(hour);
    }


}
