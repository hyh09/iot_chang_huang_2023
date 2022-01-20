package org.thingsboard.server.dao.hs.utils;

import com.google.common.collect.Maps;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.ota.ChecksumAlgorithm;
import org.thingsboard.server.dao.hs.HSConstants;
import org.thingsboard.server.dao.hs.entity.enums.EnumGetter;
import org.thingsboard.server.dao.hs.entity.vo.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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

    /**
     * 单个数据
     */
    public static PageRequest singleDataPage() {
        return PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdTime"));
    }

    /**
     * 获得excel 单元格时间数据
     *
     * @param cell 单元格
     */
    public static Long getCellDateVal(Cell cell) {
        try {
            return cell.getLocalDateTimeCellValue().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 获得excel 单元格decimal数据
     *
     * @param cell 单元格
     */
    public static BigDecimal getCellDecimalVal(Cell cell) {
        try {
            return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 获得excel 单元格字符串格式数据
     *
     * @param cell 单元格
     */
    public static String getCellStringVal(Cell cell) {
        if (cell == null)
            return StringUtils.EMPTY;
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                    return cell.getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                else
                    return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            case STRING:
                return cell.getStringCellValue().trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                return cell.getCellFormula().trim();
            case ERROR:
                return String.valueOf(cell.getErrorCellValue()).trim();
            default:
                return StringUtils.EMPTY;
        }
    }

    /**
     * 转换成UUID
     *
     * @param str uuid str
     */
    public static UUID toUUIDNullable(String str) {
        return Optional.ofNullable(str).map(String::trim).filter(e -> !"".equals(e)).map(UUID::fromString).orElse(null);
    }

    /**
     * 转换成UUID str
     *
     * @param uuid uuid
     */
    public static String toStrUUIDNullable(UUID uuid) {
        return Optional.ofNullable(uuid).map(UUID::toString).orElse(null);
    }

    /**
     * 转换成Null 如果是空字符串
     *
     * @param str str
     */
    public static String toNullStrIfIsBlank(String str) {
        if (StringUtils.isBlank(str))
            return null;
        return str;
    }

    /**
     * 生成校验和
     *
     * @param checksumAlgorithm 校验和算法
     * @param data              数据
     * @return 校验和
     */
    @SuppressWarnings("all")
    public static String generateChecksum(ChecksumAlgorithm checksumAlgorithm, ByteBuffer data) throws ThingsboardException {
        HashFunction hashFunction;
        switch (checksumAlgorithm) {
            case MD5:
                hashFunction = Hashing.md5();
                break;
            case SHA256:
                hashFunction = Hashing.sha256();
                break;
            case SHA384:
                hashFunction = Hashing.sha384();
                break;
            case SHA512:
                hashFunction = Hashing.sha512();
                break;
            case CRC32:
                hashFunction = Hashing.crc32();
                break;
            case MURMUR3_32:
                hashFunction = Hashing.murmur3_32();
                break;
            case MURMUR3_128:
                hashFunction = Hashing.murmur3_128();
                break;
            default:
                throw new ThingsboardException("不支持的校验和算法！", ThingsboardErrorCode.GENERAL);
        }
        return hashFunction.hashBytes(data.array()).toString();
    }

    /**
     * 转换为资源列表
     *
     * @param t 列表
     */
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
     * 获得当月零点的时间
     */
    public static Long getThisMonthStartTime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获得当年零点的时间
     */
    public static Long getThisYearStartTime() {
        return LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获得当天零点的时间
     */
    public static Long getTodayStartTime() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获得昨天零点的时间
     */
    public static Long getYesterdayStartTime() {
        return LocalDate.now().atStartOfDay().minusDays(1).toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获得当前的时间
     */
    public static Long getTodayCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获得当天的日期
     */
    public static String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
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
                for (DictDeviceComponentPropertyVO propertyVO : componentVO.getPropertyList()) {
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
