package org.thingsboard.server.dao.hs;

/**
 * 常量类
 *
 * @author wwj
 * @since 2021.11.12
 */
public class HSConstants {
    public static final String NULL_STR = "null";
    public static final String CREATED_TIME = "createdTime";
    public static final String DESC = "desc";
    public static final String ASC = "asc";
    public static final String TS = "ts";
    public static final String UNGROUPED = "未分组";
    public static final String CODE_PREFIX_DICT_DATA = "SJZD";
    public static final String CODE_PREFIX_DICT_DEVICE = "SBZD";
    public static final String CODE_PREFIX_ORDER = "DD";
    public static final String CODE_PREFIX_DICT_DEVICE_COMPONENT = "SBBJ";
    /**
     * 设备是否在线状态属性key 1在线0离线
     */
    public static final String ATTR_ACTIVE = "active";
    /**
     * 设备是否在线状态属性switch 1开机0停机
     */
    public static final String ATTR_SWITCH = "switch";
    public static final String ATTR_DEVICE_ID = "attrDeviceId";
    public static final String FILE_STR = "file";
    public static final String TEMP_STR = "temp";

    /**
     * 一天的毫秒数
     */
    public static final Long DAY_TIME = 86400000L;

    public static final String VERSION = "20220109";
}
