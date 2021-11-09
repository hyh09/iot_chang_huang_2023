package org.thingsboard.server.dao.hs.utils;

import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.hs.entity.vo.DictDeviceComponentVO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
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
     *
     * @param t CompletableFuture
     */
    public static <T> T handleAsync(CompletableFuture<T> t) {
        return t.join();
    }

    /**
     * 通用处理异步返回
     *
     * @param t CompletableFutureList
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
            throw new ThingsboardException("code prefix error", ThingsboardErrorCode.GENERAL);
        }
        try {
            var intStr = code.split(prefix)[1];
            if (intStr.length() != 4)
                throw new ThingsboardException("code length is not 4", ThingsboardErrorCode.GENERAL);
            int intV = Integer.parseInt(intStr);
            if (intV < 1 || intV > 9999) {
                throw new ThingsboardException("code num not in [1, 9999] error", ThingsboardErrorCode.GENERAL);
            }
        } catch (Exception ignore) {
            throw new ThingsboardException("code error", ThingsboardErrorCode.GENERAL);
        }
    }

    /**
     * 【特定】递归校验设备字典部件编码
     * <p>
     * 唯一性及规范性
     *
     * @param componentList 部件列表
     * @param set           编码集合
     */
    public static void recursionCheckComponentCode(List<DictDeviceComponentVO> componentList, Set<String> set) throws ThingsboardException {
        for (DictDeviceComponentVO componentVO : componentList) {
            checkCode(componentVO.getCode(), "SBBJ");
            if (set.contains(componentVO.getCode()))
                throw new ThingsboardException("code duplicated", ThingsboardErrorCode.GENERAL);
            else
                set.add(componentVO.getCode());
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            recursionCheckComponentCode(componentVO.getComponentList(), set);
        }
    }
}
