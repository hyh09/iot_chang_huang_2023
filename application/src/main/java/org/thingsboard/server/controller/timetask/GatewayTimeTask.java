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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.config.RedisMessagePublish;
import org.thingsboard.server.dao.device.DeviceCredentialsDao;
import org.thingsboard.server.dao.hs.HSConstants;
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
//@Component
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
        log.info("????????????-????????????????????????????????????");
        //????????????????????????
        List<DeviceCredentials> deviceCredentialsList = deviceCredentialsDao.findGatewayCredentialsList();
        if (!CollectionUtils.isNullOrEmpty(deviceCredentialsList)) {
            for (DeviceCredentials deviceCredentials : deviceCredentialsList) {
                long currentTime = System.currentTimeMillis();
                if (pub.hasKey(deviceCredentials.getCredentialsId())) {
                    //??????
                    //String result = (String)pub.get(deviceCredentials.getCredentialsId());
                    //long ts = Long.parseLong(result);
                    long ts = (long) pub.get(deviceCredentials.getCredentialsId());
                    String currentTimeStr = sdf.format(new Date(currentTime));

                    if (ts + (this.defaultGatewayTimeout * 1000) > currentTime) {
                        log.info("?????????????????????????????????id=" + deviceCredentials.getDeviceId().getId() + ";????????????=" + deviceCredentials.getCredentialsId() + "???????????????=" + sdf.format(new Date(ts)));
                        jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", true), ts));
                    } else {
                        log.info("??????????????????: " + currentTimeStr);
                        log.info("???????????????????????????????????????????????????id=" + deviceCredentials.getDeviceId().getId() + ";????????????=" + deviceCredentials.getCredentialsId() + "???????????????=" + sdf.format(new Date(ts)));
                        jpaAttributeDao.save(null, deviceCredentials.getDeviceId(), DataConstants.SERVER_SCOPE, new BaseAttributeKvEntry(new BooleanDataEntry("active", false), System.currentTimeMillis()));
                    }
                } else {
                    //??????
                    log.info("????????????????????????(???????????????)?????????id=" + deviceCredentials.getDeviceId().getId() + ";????????????=" + deviceCredentials.getCredentialsId());
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
     * ??????????????????
     * 1.???????????????????????????
     * 2.??????????????????????????????
     * 3.
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void recordDuration() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("findByKey");
        //?????????????????????switch??????
        Optional<TsKvDictionary> aSwitch = tsKvDictionaryRepository.findByKey(HSConstants.ATTR_SWITCH);
        if (aSwitch.isEmpty()) {
            return;
        }
        stopWatch.stop();
        //????????????????????????????????????
        stopWatch.start("findAllByIdAttributeKey");
        List<AttributeKvEntity> allByAttributeKeyEquals = attributeKvRepository.findAllByIdAttributeKey(HSConstants.ATTR_ACTIVE);
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
        //?????????????????????map
        Map<UUID, TsKvLatestEntity> latestEntityMap = allByKeyEquals.stream().collect(Collectors.toMap(TsKvLatestEntity::getEntityId, Function.identity()));
        //???????????????????????????
        List<TrepHstaDetailEntity> startUpList = getTrepHstaDetailEntities(allByAttributeKeyEquals, deviceEntityMap, latestEntityMap);
        //????????????????????????????????????
        List<TrepHstaDetailEntity> allByUpdateTimeIsNull = getTrepHstaDetailEntities(latestEntityMap, startUpList);
        //????????????????????????
        List<TrepDayStaDetailEntity> nowList = getTrepDayStaDetailEntities(allByUpdateTimeIsNull);
        stopWatch.start("addOrUpdate");
        addOrUpdate(allByUpdateTimeIsNull, nowList);
        stopWatch.stop();
        log.info("????????????: {}", stopWatch.prettyPrint());
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param allByUpdateTimeIsNull
     * @return
     */
    @NotNull
    private List<TrepDayStaDetailEntity> getTrepDayStaDetailEntities(List<TrepHstaDetailEntity> allByUpdateTimeIsNull) {
        //??????????????????????????????
        Date now = DateUtils.getNowDate();
        String nowStr = DateUtils.dateTime(now);
        Date nowMin = DateUtils.getDayMin(now);
        //??????????????????????????????????????????startTime???null
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
            Date endTimeDate = null;
            Date endMin;
            String endTimeStr = null;
            if (endTime != null) {
                endTimeDate = new Date(endTime);
                endTimeStr = DateUtils.dateTime(endTimeDate);
            }
            TrepDayStaDetailEntity trepDayStaDetailEntity = dayMap.get(entityId + nowStr + tenantId);
            if (trepDayStaDetailEntity == null) {
                Calendar instance = Calendar.getInstance();
                instance.setTime(now);
                instance.add(Calendar.DATE, -1);
                trepDayStaDetailEntity = dayMap.get(entityId + DateUtils.dateTime(instance.getTime()) + tenantId);
            }
            //????????????????????????
            TrepDayStaDetailEntity addEntity = new TrepDayStaDetailEntity();
            addEntity.setEntityId(entityId);
            addEntity.setBdate(now);
            addEntity.setStartTime(now.getTime() - startTime);
            if (!startTimeStr.equals(nowStr)) {
                addEntity.setStartTime(now.getTime() - nowMin.getTime());
            }
            addEntity.setTotalTime(0L);
            addEntity.setTenantId(tenantId);
            if (endTime != null) {
                addEntity.setStartTime(null);
                addEntity.setTotalTime(endTime - startTime);
                if (!startTimeStr.equals(nowStr)) {
                    addEntity.setTotalTime(endTime - nowMin.getTime());
                }
            }
            //??????
            if (trepDayStaDetailEntity == null) {
                addList.add(addEntity);
            }
            //??????
            if (trepDayStaDetailEntity != null) {
                Date bdate = trepDayStaDetailEntity.getBdate();
                String bdateStr = DateUtils.dateTime(bdate);
                if (!nowStr.equals(bdateStr)) {
                    //???????????????????????????
                    trepDayStaDetailEntity.setStartTime(null);
                    if (endTime == null || nowStr.equals(endTimeStr)) {
                        if (!startTimeStr.equals(bdateStr)) {
                            trepDayStaDetailEntity.setTotalTime(HSConstants.DAY_TIME);
                        } else {
                            trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + nowMin.getTime() - startTime);
                        }
                        addList.add(addEntity);
                    } else {
                        //???????????????????????????????????????????????????
                        endMin = DateUtils.getDayMin(endTimeDate);
                        if (!startTimeStr.equals(bdateStr)) {
                            trepDayStaDetailEntity.setTotalTime(endTime - endMin.getTime());
                        } else {
                            trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + endTime - startTime);
                        }
                    }
                } else if (endTime == null) {
                    trepDayStaDetailEntity.setStartTime(now.getTime() - startTime);
                    if (!startTimeStr.equals(nowStr)) {
                        trepDayStaDetailEntity.setStartTime(now.getTime() - nowMin.getTime());
                    }
                } else {
                    trepDayStaDetailEntity.setStartTime(null);
                    if (!startTimeStr.equals(endTimeStr)) {
                        trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + endTime - nowMin.getTime());
                    } else {
                        trepDayStaDetailEntity.setTotalTime(trepDayStaDetailEntity.getTotalTime() + endTime - startTime);
                    }
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
        //???????????????
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
        //?????????????????????
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
     * ???????????????????????????
     *
     * @param allByAttributeKeyEquals
     * @param deviceEntityMap
     * @param latestEntityMap
     * @return
     */
    @NotNull
    private List<TrepHstaDetailEntity> getTrepHstaDetailEntities(List<AttributeKvEntity> allByAttributeKeyEquals, Map<UUID, DeviceEntity> deviceEntityMap, Map<UUID, TsKvLatestEntity> latestEntityMap) {
        //????????? switch=1???????????????????????????
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
