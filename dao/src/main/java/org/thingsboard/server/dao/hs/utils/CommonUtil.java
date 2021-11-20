package org.thingsboard.server.dao.hs.utils;

import com.google.common.collect.Maps;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.enums.EnumGetter;
import org.thingsboard.server.dao.hs.entity.vo.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 工具类
 *
 * @author wwj
 * @since 2021.10.21
 */
public class CommonUtil {

    public static <T extends EnumGetter> List<Map<String, String>> toResourceList(Collection<T> t) {
        return t.stream().map(e -> {
            HashMap<String, String> map = Maps.newLinkedHashMap();
            map.put("name", e.getName());
            map.put("code", e.getCode());
            return map;
        }).collect(Collectors.toList());
    }

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
            throw new ThingsboardException("编码前缀错误", ThingsboardErrorCode.GENERAL);
        }
        try {
            var intStr = code.split(prefix)[1];
            if (intStr.length() != 4)
                throw new ThingsboardException("编码数字长度不等于4位", ThingsboardErrorCode.GENERAL);
            int intV = Integer.parseInt(intStr);
            if (intV < 1 || intV > 9999) {
                throw new ThingsboardException("编码数字不在1-9999区间内", ThingsboardErrorCode.GENERAL);
            }
        } catch (Exception ignore) {
            throw new ThingsboardException("编码错误", ThingsboardErrorCode.GENERAL);
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
            checkCode(componentVO.getCode(), HSConstants.CODE_PREFIX_DICT_DEVICE_COMPONENT);
            if (set.contains(componentVO.getCode()))
                throw new ThingsboardException("编码重复", ThingsboardErrorCode.GENERAL);
            else
                set.add(componentVO.getCode());
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            recursionCheckComponentCode(componentVO.getComponentList(), set);
        }
    }


    /**
     * 【特定】校验两个list是否包含相同的头数据
     */
    public static void checkDictDeviceGroupVOListHeadIsUnlike(List<DictDeviceGroupVO> sourceList, List<DictDeviceGroupVO> childList) throws ThingsboardException {
        // TODO 校验
//        var groupNum = childList.size();
//        childList.forEach(e -> {
//            var index = childList.indexOf(e);
//            var propertyNum = e.getGroupPropertyList().size();
//        });
    }

    /**
     * 【特定】校验dictDeviceVO是否有相同的key或者name
     *
     * @param dictDeviceVO DictDeviceVO
     * @param set          属性set
     */
    public static void checkDuplicateName(DictDeviceVO dictDeviceVO, Set<String> set) throws ThingsboardException {
        for (DictDeviceGroupVO groupVO : dictDeviceVO.getGroupList()) {
            for (DictDeviceGroupPropertyVO propertyVO : groupVO.getGroupPropertyList()) {
                if (set.contains(propertyVO.getName()))
                    throw new ThingsboardException(propertyVO.getName() + " 重复", ThingsboardErrorCode.GENERAL);
                else
                    set.add(propertyVO.getName());
            }
        }
        checkComponentDuplicateNameOrKey(dictDeviceVO.getComponentList(), set);
    }

    /**
     * 【特定】校验DictDeviceComponentVO是否有相同的key或者name
     *
     * @param componentList 部件列表
     * @param set           属性set
     */
    public static void checkComponentDuplicateNameOrKey(List<DictDeviceComponentVO> componentList, Set<String> set) throws ThingsboardException {
        for (DictDeviceComponentVO componentVO : componentList) {
            if (componentVO.getPropertyList() != null && !componentVO.getPropertyList().isEmpty()) {
                for(DictDeviceComponentPropertyVO propertyVO:componentVO.getPropertyList()) {
                    if (set.contains(propertyVO.getName()))
                        throw new ThingsboardException(propertyVO.getName() + " 重复", ThingsboardErrorCode.GENERAL);
                    else
                        set.add(propertyVO.getName());
                }
            }
            if (componentVO.getComponentList() == null || componentVO.getComponentList().isEmpty()) {
                continue;
            }
            checkComponentDuplicateNameOrKey(componentVO.getComponentList(), set);
        }
    }
}
