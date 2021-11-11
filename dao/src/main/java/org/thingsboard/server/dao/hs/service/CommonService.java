package org.thingsboard.server.dao.hs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceGroupVO;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 抽离一些公共方法
 *
 * @author wwj
 * @since 2021.11.5
 */
public interface CommonService {

    /**
     * 转换数据
     *
     * @param fromValue   源数据
     * @param toValueType 目标类
     */
    default <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return new ObjectMapper().convertValue(fromValue, toValueType);
    }

    /**
     * 计算Map里value为ture的数量
     *
     * @param map Map<String, Boolean>
     */
    default int calculateValueInMap(Map<String, Boolean> map) {
        if (map == null || map.isEmpty()) return 0;
        return map.values().stream().reduce(0, (r, e) -> {
            if (e)
                return r + 1;
            return r;
        }, (a, b) -> null);
    }

    /**
     * 获得Map里的值
     *
     * @param map Map<String, Boolean>
     */
    default Boolean calculateValueInMap(Map<String, Boolean> map, String str) {
        if (map == null || map.isEmpty()) return Boolean.FALSE;
        return map.get(str);
    }

    /**
     * 判断是否数值型
     */
    default boolean isNumberData(Object o) {
        try {
            Double.valueOf(o.toString());
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    /**
     * 转换成UUID
     *
     * @param str uuid str
     */
    default UUID toUUID(String str) {
        return UUID.fromString(str);
    }

    /**
     * 获得近几个月的开始时间
     *
     * @param monthNum 月份数量
     */
    default List<Long> listLatestMonthsStartTime(int monthNum) {
        List<Long> temp = new ArrayList<>();
        if (monthNum < 1) {
            return temp;
        }
        for (int i = 0; i < monthNum; i++) {
            temp.add(YearMonth.now().minusMonths(i).atDay(1).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
        return temp;
    }

    /**
     * 判断设备是否是未分配
     */
    default <T extends Device> Boolean isDeviceUnAllocation(T t) {
        return t.getProductionLineId() == null;
    }

    /**
     * 获得可用的编码
     *
     * @param codes  编码列表
     * @param prefix 前缀
     */
    default String getAvailableCode(List<String> codes, String prefix) {
        var ints = codes.stream().map(e -> Integer.valueOf(e.split(prefix)[1])).sorted().collect(Collectors.toList());
        return IntStream.iterate(1, k -> k + 1).boxed().filter(e -> !ints.contains(e)).findFirst()
                .map(e -> prefix + String.format("%04d", e)).orElse(null);
    }
}
