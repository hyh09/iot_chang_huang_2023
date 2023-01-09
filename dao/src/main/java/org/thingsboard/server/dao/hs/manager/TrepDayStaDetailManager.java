package org.thingsboard.server.dao.hs.manager;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.thingsboard.server.dao.hs.dao.TrepDayStaDetailEntity;
import org.thingsboard.server.dao.hs.dao.TrepDayStaDetailRepository;
import org.thingsboard.server.dao.util.decimal.BigDecimalUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fwy
 * @date 2023/1/6 15:40
 */
@Component
public class TrepDayStaDetailManager {
    @Resource
    private TrepDayStaDetailRepository trepDayStaDetailRepository;

    /**
     * 设置设备的开机率
     *
     * @param r            被设置的数据
     * @param dateDay      查询的日期
     * @param entityIdFunc 设备id字段
     * @param rateConsumer 开机率字段
     * @param <K>
     */
    public <K> void setRate(K r,
                            Date dateDay,
                            final Function<K, UUID> entityIdFunc,
                            final BiConsumer<K, BigDecimal> rateConsumer) {
        if (r == null) {
            return;
        }
        this.setRateBatch(Arrays.asList(r), dateDay, entityIdFunc, rateConsumer);
    }

    /**
     * 设置设备的开机率
     *
     * @param data         被设置的数据
     * @param dateDay      查询的日期
     * @param entityIdFunc 设备id字段
     * @param rateConsumer 开机率字段
     * @param <K>
     */
    public <K> void setRateBatch(List<K> data,
                                 Date dateDay,
                                 final Function<K, UUID> entityIdFunc,
                                 final BiConsumer<K, BigDecimal> rateConsumer) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        //获得设备的开机时长
        List<UUID> entityIdList = new ArrayList<>();
        for (K record : data) {
            // 获取设备id
            UUID entityId = entityIdFunc.apply(record);
            entityIdList.add(entityId);
        }
        List<TrepDayStaDetailEntity> allByBdateEqualsAndEntityIdIn = trepDayStaDetailRepository.findAllByBdateEqualsAndEntityIdIn(dateDay, entityIdList);
        Map<UUID, Long> id2timeMap = allByBdateEqualsAndEntityIdIn.stream().collect(Collectors.toMap(TrepDayStaDetailEntity::getEntityId, e -> e.getTotalTime() + e.getStartTime()));
        //设置字段
        for (K record : data) {
            // 获取创建人
            UUID entityId = entityIdFunc.apply(record);
            Long timeLong = id2timeMap.get(entityId);
            rateConsumer.accept(record, BigDecimalUtil.INSTANCE.divide(timeLong, "86400000"));
        }
    }
}
