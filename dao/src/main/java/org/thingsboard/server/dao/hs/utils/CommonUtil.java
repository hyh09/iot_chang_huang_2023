package org.thingsboard.server.dao.hs.utils;

import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 工具类
 *
 * @author wwj
 * @since 2021.10.21
 */
public class CommonUtil {

    /**
     * 判断设备是否是未分配
     */
    public static <T extends Device> Boolean isDeviceUnAllocation(T t) {
        return t.getProductionLineId() == null;
    }

    /**
     * 通用处理异步返回
     * <p>
     * TODO 增加处理
     */
    public static <T> T handleAsync(CompletableFuture<T> t) {
        return t.join();
    }

    /**
     * 通用处理异步返回
     * <p>
     * TODO 增加处理
     */
    public static <T> List<T> handleAsync(List<CompletableFuture<T>> t) {
        return t.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 获得近几个月的开始时间
     *
     * @param monthNum 月份数量
     */
    public static List<Long> listLatestMonthsStartTime(int monthNum) {
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
     * 获得当天零点的时间
     */
    public static Long getTodayStartTime() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获得当前的时间
     */
    public static Long getTodayCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 校验编码规则
     *
     * @param code   编码
     * @param prefix 前缀
     */
    public static void checkCode(String code, String prefix) throws ThingsboardException {
        if (code == null || !code.startsWith(prefix)) {
            throw new ThingsboardException("code error", ThingsboardErrorCode.GENERAL);
        }
        try {
            int intV = Integer.parseInt(code.split(prefix)[1]);
            if (intV < 1 || intV > 9999) {
                throw new ThingsboardException("code error", ThingsboardErrorCode.GENERAL);
            }
        } catch (Exception ignore) {
            throw new ThingsboardException("code error", ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * 获得可用的编码
     *
     * @param codes  编码列表
     * @param prefix 前缀
     */
    public static String getAvailableCode(List<String> codes, String prefix) {
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
