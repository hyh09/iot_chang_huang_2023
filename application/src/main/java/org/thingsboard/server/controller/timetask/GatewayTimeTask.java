package org.thingsboard.server.controller.timetask;

import com.amazonaws.util.CollectionUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.config.RedisMessagePublish;
import org.thingsboard.server.dao.device.DeviceCredentialsDao;
import org.thingsboard.server.dao.hs.dao.TrepDayStaDetailEntity;
import org.thingsboard.server.dao.hs.dao.TrepDayStaDetailRepository;
import org.thingsboard.server.dao.hs.dao.TrepHstaDetailEntity;
import org.thingsboard.server.dao.hs.dao.TrepHstaDetailRepository;
import org.thingsboard.server.dao.model.sql.AttributeKvEntity;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sqlts.dictionary.TsKvDictionary;
import org.thingsboard.server.dao.model.sqlts.latest.TsKvLatestEntity;
import org.thingsboard.server.dao.sql.attributes.AttributeKvRepository;
import org.thingsboard.server.dao.sql.attributes.JpaAttributeDao;
import org.thingsboard.server.dao.sql.device.DeviceRepository;
import org.thingsboard.server.dao.sqlts.dictionary.TsKvDictionaryRepository;
import org.thingsboard.server.dao.sqlts.latest.TsKvLatestRepository;
import org.thingsboard.server.utils.DateUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GatewayTimeTask {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${state.defaultGatewayTimeout}")
    @Getter
    private long defaultGatewayTimeout;

    @Autowired
    private DeviceCredentialsDao deviceCredentialsDao;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RedisMessagePublish pub;

    @Autowired
    private JpaAttributeDao jpaAttributeDao;


    @Scheduled(cron = "0/10 * * * * ?")
    public void updateGatewayActive() {
        //pub.setWithExpire("fAtHNfi4kCRPkEr7V6aw",String.valueOf(System.currentTimeMillis()),1l, TimeUnit.HOURS);
        log.info("定时任务-定时更新网关状态执行了！");
        //获取所有网关令牌
        List<DeviceCredentials> deviceCredentialsList = deviceCredentialsDao.findGatewayCredentialsList();
        if (!CollectionUtils.isNullOrEmpty(deviceCredentialsList)) {
            for (DeviceCredentials deviceCredentials : deviceCredentialsList) {
                long currentTime = System.currentTimeMillis();
                if (pub.hasKey(deviceCredentials.getCredentialsId())) {
                    //在线
                    //String result = (String)pub.get(deviceCredentials.getCredentialsId());
                    //long ts = Long.parseLong(result);
                    long ts = (long) pub.get(deviceCredentials.getCredentialsId());
                    String currentTimeStr = sdf.format(new Date(currentTime));

                    if (ts + (this.defaultGatewayTimeout * 1000) > currentTime) {
                        log.info("定时更新网关在线，网关id=" + deviceCredentials.getDeviceId().getId() + ";网关令牌=" + deviceCredentials.getCredentialsId() + "；更新时间=" + sdf.format(new Date(ts)));
                        jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", true), ts));
                    } else {
                        log.info("系统当前时间: " + currentTimeStr);
                        log.info("定时更新网关离线（超时离线），网关id=" + deviceCredentials.getDeviceId().getId() + ";网关令牌=" + deviceCredentials.getCredentialsId() + "；更新时间=" + sdf.format(new Date(ts)));
                        jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", false), System.currentTimeMillis()));
                    }
                } else {
                    //离线
                    log.info("定时更新网关离线(心跳不存在)，网关id=" + deviceCredentials.getDeviceId().getId() + ";网关令牌=" + deviceCredentials.getCredentialsId());
                    jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", false), currentTime));
                }
            }
        }

    }

    @Resource
    private TsKvLatestRepository tsKvLatestRepository;

    @Resource
    private TsKvDictionaryRepository tsKvDictionaryRepository;

    @Resource
    private AttributeKvRepository attributeKvRepository;

    @Resource
    private TrepDayStaDetailRepository trepDayStaDetailRepository;

    @Resource
    private TrepHstaDetailRepository trepHstaDetailRepository;


    /**
     * 记录状态时长
     * 1.查询所有开机的机台
     * 2.查出数据库开机的机台
     * 3.
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void recordDuration() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("findByKey");
        //查询设备对应的switch状态
        Optional<TsKvDictionary> aSwitch = tsKvDictionaryRepository.findByKey("switch");
        if (aSwitch.isEmpty()) {
            return;
        }
        stopWatch.stop();
        //获取所有的设备的在线状态
        stopWatch.start("findAllByIdAttributeKey");
        List<AttributeKvEntity> allByAttributeKeyEquals = attributeKvRepository.findAllByIdAttributeKey("active");
        stopWatch.stop();
        TsKvDictionary tsKvDictionary = aSwitch.get();
        int keyId = tsKvDictionary.getKeyId();
        stopWatch.start("findAllByKeyEquals");
        List<TsKvLatestEntity> allByKeyEquals = tsKvLatestRepository.findAllByKeyEquals(keyId);
        stopWatch.stop();
        stopWatch.start("findAll");
        Iterable<DeviceEntity> all = deviceRepository.getTenantIdAll();
        stopWatch.stop();
        Map<UUID, DeviceEntity> deviceEntityMap = Lists.newArrayList(all).stream().collect(Collectors.toMap(DeviceEntity::getId, Function.identity()));
        //最后一次采集的map
        Map<UUID, TsKvLatestEntity> latestEntityMap = allByKeyEquals.stream().collect(Collectors.toMap(TsKvLatestEntity::getEntityId, Function.identity()));
        //获取当前开机的机台
        List<TrepHstaDetailEntity> startUpList = getTrepHstaDetailEntities(allByAttributeKeyEquals, deviceEntityMap, latestEntityMap);
        //获取更新和新增的上机数据
        List<TrepHstaDetailEntity> allByUpdateTimeIsNull = getTrepHstaDetailEntities(latestEntityMap, startUpList);
        //获取天的上机数据
        List<TrepDayStaDetailEntity> nowList = getTrepDayStaDetailEntities(allByUpdateTimeIsNull);
        stopWatch.start("addOrUpdate");
        addOrUpdate(allByUpdateTimeIsNull, nowList);
        stopWatch.stop();
        log.info("请求耗时: {}", stopWatch.prettyPrint());
    }

    /**
     * 获取天的统计数据包含新增和更新
     *
     * @param allByUpdateTimeIsNull
     * @return
     */
    @NotNull
    private List<TrepDayStaDetailEntity> getTrepDayStaDetailEntities(List<TrepHstaDetailEntity> allByUpdateTimeIsNull) {
        //当日的按天累计的时间
        Date now = DateUtils.getNowDate();
        String nowStr = DateUtils.dateTime(now);
        List<TrepDayStaDetailEntity> nowList = trepDayStaDetailRepository.findAllByBdateEqualsOrStartTimeIsNotNull(now);
        Map<String, TrepDayStaDetailEntity> dayMap = nowList.stream().collect(Collectors.toMap(e -> e.getEntityId() + DateUtils.dateTime(e.getBdate()) + e.getTenantId(), Function.identity()));
        List<TrepDayStaDetailEntity> addList = new ArrayList<>();
        allByUpdateTimeIsNull.forEach(e -> {
            UUID entityId = e.getEntityId();
            UUID tenantId = e.getTenantId();
            Long startTime = e.getStartTime();
            Date startTimeDate = new Date(startTime);
            String startTimeStr = DateUtils.dateTime(startTimeDate);
            Long endTime = e.getEndTime();
            Date endTimeDate;
            String endTimeStr = null;
            if (endTime != null) {
                endTimeDate = new Date(endTime);
                endTimeStr = DateUtils.dateTime(endTimeDate);
            }
            TrepDayStaDetailEntity trepDayStaDetailEntity = dayMap.get(entityId + startTimeStr + tenantId);
            //新增的天统计数据
            TrepDayStaDetailEntity addEntity = new TrepDayStaDetailEntity();
            addEntity.setEntityId(entityId);
            addEntity.setBdate(now);
            addEntity.setStartTime(now.getTime() - startTime);
            addEntity.setTotalTime(0L);
            addEntity.setTenantId(tenantId);
            //新增
            if (e.getId() == null && trepDayStaDetailEntity == null) {
                addList.add(addEntity);
            }
            //更新
            if (trepDayStaDetailEntity != null) {
                Date bdate = trepDayStaDetailEntity.getBdate();
                if (!nowStr.equals(DateUtils.dateTime(bdate))) {
                    trepDayStaDetailEntity.setStartTime(0L);
                    if (endTime == null || !nowStr.equals(endTimeStr)) {
                        Date dayMax = DateUtils.getDayMax(startTimeDate);
                        trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + dayMax.getTime() - startTime);
                        addEntity.setStartTime(endTime - DateUtils.getDayMin(now).getTime());
                        addList.add(addEntity);
                    } else {
                        trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + e.getEndTime() - startTime);
                    }
                }
                if (endTime == null) {
                    trepDayStaDetailEntity.setStartTime(now.getTime() - startTime);
                } else {
                    trepDayStaDetailEntity.setStartTime(0L);
                    trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + endTime - startTime);
                }
            }
        });
        nowList.addAll(addList);
        return nowList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdate(List<TrepHstaDetailEntity> allByUpdateTimeIsNull, List<TrepDayStaDetailEntity> nowList) {
        trepDayStaDetailRepository.saveAll(nowList);
        trepHstaDetailRepository.saveAll(allByUpdateTimeIsNull);
    }

    @NotNull
    private List<TrepHstaDetailEntity> getTrepHstaDetailEntities(Map<UUID, TsKvLatestEntity> latestEntityMap, List<TrepHstaDetailEntity> startUpList) {
        //更新为下机
        List<UUID> startUpIdList = startUpList.stream().map(TrepHstaDetailEntity::getEntityId).collect(Collectors.toList());
        List<TrepHstaDetailEntity> allByUpdateTimeIsNull = trepHstaDetailRepository.findAllByEndTimeIsNull();
        List<TrepHstaDetailEntity> updateList = new ArrayList<>();
        List<TrepHstaDetailEntity> addList = new ArrayList<>();
        allByUpdateTimeIsNull.forEach(e -> {
            UUID entityId = e.getEntityId();
            if (!startUpIdList.contains(entityId)) {
                Long ts = latestEntityMap.get(entityId).getTs();
                e.setEndTime(ts);
                e.setTotalTime(e.getEndTime() - e.getStartTime());
                updateList.add(e);
            }
        });
        //新增开机的机台
        List<UUID> startUpDbList = allByUpdateTimeIsNull.stream().map(TrepHstaDetailEntity::getEntityId).collect(Collectors.toList());
        startUpList.forEach(e -> {
            UUID entityId = e.getEntityId();
            if (!startUpDbList.contains(entityId)) {
                addList.add(e);
            }
        });
        allByUpdateTimeIsNull.addAll(addList);
        return allByUpdateTimeIsNull;
    }

    /**
     * 获取当前开机的机台
     *
     * @param allByAttributeKeyEquals
     * @param deviceEntityMap
     * @param latestEntityMap
     * @return
     */
    @NotNull
    private List<TrepHstaDetailEntity> getTrepHstaDetailEntities(List<AttributeKvEntity> allByAttributeKeyEquals, Map<UUID, DeviceEntity> deviceEntityMap, Map<UUID, TsKvLatestEntity> latestEntityMap) {
        //在线且 switch=1的时候才能算是开机
        List<TrepHstaDetailEntity> startUpList = new ArrayList<>();
        allByAttributeKeyEquals.forEach(e -> {
            UUID entityId = e.getId().getEntityId();
            DeviceEntity deviceEntity = deviceEntityMap.get(entityId);
            if (BooleanUtils.isTrue(e.getBooleanValue()) && deviceEntity != null) {
                TsKvLatestEntity tsKvLatestEntity = latestEntityMap.get(entityId);
                if (tsKvLatestEntity != null && tsKvLatestEntity.getLongValue() == 1L) {
                    Long ts = tsKvLatestEntity.getTs();
                    TrepHstaDetailEntity trepHstaDetailEntity = new TrepHstaDetailEntity();
                    trepHstaDetailEntity.setEntityId(e.getId().getEntityId());
                    trepHstaDetailEntity.setStartTime(ts);
                    trepHstaDetailEntity.setTenantId(deviceEntity.getTenantId());
                    startUpList.add(trepHstaDetailEntity);
                }
            }
        });
        return startUpList;
    }


}
