package org.thingsboard.server.dao.hs.service;

import org.thingsboard.server.common.data.Device;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 抽离一些公共方法
 *
 * @author wwj
 * @since 2021.11.5
 */
public interface CommonService {

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
            return new ArrayList<>();
        }
        for (int i = 0; i < monthNum; i++) {
            temp.add(YearMonth.now().minusMonths(i).atDay(1).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
        return temp;
    }

    /**
     * 判断设备是否是未分配
     */
    default  <T extends Device> Boolean isDeviceUnAllocation(T t) {
        return t.getProductionLineId() == null;
    }

    /**
     * 获得可用的编码
     *
     * @param codes  编码列表
     * @param prefix 前缀
     */
    default String getAvailableCode(List<String> codes, String prefix) {
        if (codes.isEmpty()) {
            return prefix + "0001";
        } else {
            var ints = codes.stream().map(e -> Integer.valueOf(e.split(prefix)[1])).sorted().collect(Collectors.toList());
            int start = 0;
            while (true) {
                if (ints.size() - 1 == start) {
                    return prefix + String.format("%04d", start + 2);
                }
                if (!ints.get(start).equals(start + 1)) {
                    return prefix + String.format("%04d", start + 1);
                }
                start += 1;
            }
        }
    }
}
