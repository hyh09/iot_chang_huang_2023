package org.thingsboard.server.dao.hs.utils;

import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 工具类
 *
 * @author wwj
 * @since 2021.10.21
 */
public class CommonUtil {

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
}
