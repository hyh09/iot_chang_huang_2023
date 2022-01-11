package org.thingsboard.server.dao.hs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.kv.DataType;
import org.thingsboard.server.common.data.kv.KvEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
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
     * 格式化产量
     */
    default BigDecimal formatCapacity(BigDecimal val1) {
        return val1.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    /**
     * 计算完成度
     */
    default BigDecimal calculateCompleteness(BigDecimal val1, BigDecimal val2) {
        if (val2.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return val1.divide(val2, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L)).stripTrailingZeros();
    }

    /**
     * 计算百分比
     */
    default BigDecimal calculatePercentage(BigDecimal val1, BigDecimal val2) {
        if (val2.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        return val1.divide(val2, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L)).stripTrailingZeros();
    }

    /**
     * 转换遥测数据为保留4位的
     */
    default <T extends KvEntry> String formatKvEntryValue(T t) {
        if (t == null)
            return null;
        String result = t.getValueAsString();
        if (DataType.STRING.equals(t.getDataType()) || DataType.DOUBLE.equals(t.getDataType())) {
            try {
                BigDecimal bigDecimal = new BigDecimal(t.getValueAsString());
                if (bigDecimal.scale() > 2) {
                    return bigDecimal.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                }
            } catch (Exception ignore) {
            }
        }
        return result;
    }

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
     * 转换成String
     *
     * @param uuid UUID
     */
    default String UUIDToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    /**
     * 转换成String
     *
     * @param uuid UUID
     */
    default String UUIDToStringOrElseNullStr(UUID uuid) {
        return uuid == null ? "null" : uuid.toString();
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
     * urlEncode data
     */
    default String urlEncode(String str) {
        return Optional.ofNullable(str).map(v -> URLEncoder.encode(str.trim(), StandardCharsets.UTF_8)).orElse("");
    }

    /**
     * 转换成政治正确的国家
     */
    default String toTrueCountry(String country) {
        return Optional.ofNullable(country).map(v -> {
            var temp = v.toLowerCase().trim();
            if (temp.contains("tai") && temp.contains("wan")) {
                return "China";
            } else if (v.contains("台") && v.contains("湾")) {
                return "中国";
            } else if (v.contains("臺") && v.contains("灣")) {
                return "中国";
            }
            return v;
        }).orElse(null);
    }

    /**
     * 转换成政治正确的地区显示名称
     */
    default String toTrueDisplayName(String displayName) {
        return Optional.ofNullable(displayName).map(v -> {
            var temp = v.toLowerCase().trim();
            if (temp.contains("tai") && temp.contains("wan")) {
                return v + ", China";
            } else if (v.contains("台") && v.contains("湾")) {
                return v + ", 中国";
            } else if (v.contains("臺") && v.contains("灣")) {
                return v + ", 中国";
            }
            return v;
        }).orElse(null);
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
