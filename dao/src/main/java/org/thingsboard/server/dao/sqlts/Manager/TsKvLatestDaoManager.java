package org.thingsboard.server.dao.sqlts.Manager;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.dao.sqlts.TsKvLatestDao;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author fwy
 * @date 2023/1/11 10:12
 */
@Component
public class TsKvLatestDaoManager {
    @Resource
    private TsKvLatestDao tsKvLatestDao;

    /**
     * 已有在线离线状态的情况下设置状态
     * 离线>生产中/停机(switch)->在线
     */
    public <K> void setState(K r,
                             final Function<K, UUID> entityIdFunc,
                             final Function<K, Boolean> isOnLineFunc,
                             final BiConsumer<K, String> stateConsumer) {
        if (r == null) {
            return;
        }
        this.setState(Arrays.asList(r), entityIdFunc, isOnLineFunc, stateConsumer);
    }


    /**
     * 已有在线离线状态的情况下设置状态
     * 离线>生产中/停机(switch)->在线
     */
    public <K> void setState(List<K> data,
                             final Function<K, UUID> entityIdFunc,
                             final Function<K, Boolean> isOnLineFunc,
                             final BiConsumer<K, String> stateConsumer) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        List<UUID> entityIdList = new ArrayList<>();
        List<K> switchData = new ArrayList<>();
        for (K record : data) {
            //在线的设备查询生产还是停机状态
            Boolean isOnLine = isOnLineFunc.apply(record);
            if (BooleanUtils.isTrue(isOnLine)) {
                // 获取设备id
                UUID entityId = entityIdFunc.apply(record);
                entityIdList.add(entityId);
                switchData.add(record);
            } else {
                //离线
                stateConsumer.accept(record, "离线");
            }
        }
        Map<UUID, Long> aSwitch = tsKvLatestDao.getSwitch(entityIdList);
        for (K record : switchData) {
            // 获取设备id
            UUID entityId = entityIdFunc.apply(record);
            Long aLong = aSwitch.get(entityId);
            if (aLong == 1L) {
                stateConsumer.accept(record, "生产中");
            } else if (aLong == 0L) {
                stateConsumer.accept(record, "停机");
            } else {
                stateConsumer.accept(record, "在线");
            }
        }
    }

}
