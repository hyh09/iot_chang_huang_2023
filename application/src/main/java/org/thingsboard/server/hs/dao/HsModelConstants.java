package org.thingsboard.server.hs.dao;

/**
 * 常量类
 *
 * @author wwj
 * @since 2021.10.21
 */
public class HsModelConstants {

    /**
     * 通用
     */
    public static final String GENERAL_ID = "id";
    public static final String GENERAL_TENANT_ID = "tenant_id";
    public static final String GENERAL_CREATED_TIME = "created_time";
    public static final String GENERAL_CREATED_USER = "created_user";
    public static final String GENERAL_UPDATED_TIME = "updated_time";
    public static final String GENERAL_UPDATED_USER = "updated_user";


    /**
     * 数据字典
     */
    public static final String DICT_DATA_TABLE_NAME = "hs_dict_data";
    public static final String DICT_DATA_CODE = "code";
    public static final String DICT_DATA_NAME = "name";
    public static final String DICT_DATA_TYPE = "type";
    public static final String DICT_DATA_UNIT = "unit";
    public static final String DICT_DATA_COMMENT = "comment";
    public static final String DICT_DATA_ICON = "icon";
    public static final String DICT_DATA_PICTURE = "picture";

    /**
     * 设备字典
     */
    public static final String DICT_DEVICE_ID = "dict_device_id";

    public static final String DICT_DEVICE_TABLE_NAME = "hs_dict_device";
    public static final String DICT_DEVICE_CODE = "code";
    public static final String DICT_DEVICE_NAME = "name";
    public static final String DICT_DEVICE_TYPE = "type";
    public static final String DICT_DEVICE_SUPPLIER = "supplier";
    public static final String DICT_DEVICE_MODEL = "model";
    public static final String DICT_DEVICE_VERSION = "version";
    public static final String DICT_DEVICE_WARRANTY_PERIOD = "warranty_period";
    public static final String DICT_DEVICE_COMMENT = "comment";
    public static final String DICT_DEVICE_ICON = "icon";
    public static final String DICT_DEVICE_PICTURE = "picture";

    public static final String DICT_DEVICE_COMPONENT_TABLE_NAME = "hs_dict_device_component";
    public static final String DICT_DEVICE_COMPONENT_PARENT_ID= "parent_id";
    public static final String DICT_DEVICE_COMPONENT_CODE = "code";
    public static final String DICT_DEVICE_COMPONENT_NAME = "name";
    public static final String DICT_DEVICE_COMPONENT_TYPE = "type";
    public static final String DICT_DEVICE_COMPONENT_SUPPLIER = "supplier";
    public static final String DICT_DEVICE_COMPONENT_MODEL = "model";
    public static final String DICT_DEVICE_COMPONENT_VERSION = "version";
    public static final String DICT_DEVICE_COMPONENT_WARRANTY_PERIOD = "warranty_period";
    public static final String DICT_DEVICE_COMPONENT_COMMENT = "comment";
    public static final String DICT_DEVICE_COMPONENT_ICON = "icon";
    public static final String DICT_DEVICE_COMPONENT_PICTURE = "picture";

    public static final String DICT_DEVICE_PROPERTY_TABLE_NAME = "hs_dict_device_property";
    public static final String DICT_DEVICE_PROPERTY_NAME = "name";
    public static final String DICT_DEVICE_PROPERTY_CONTENT = "content";

    public static final String DICT_DEVICE_GROUP_TABLE_NAME = "hs_dict_device_group";
    public static final String DICT_DEVICE_GROUP_ID= "dict_group_id";
    public static final String DICT_DEVICE_GROUP_NAME = "name";

    public static final String DICT_DEVICE_GROUP_PROPERTY_TABLE_NAME = "hs_dict_device_group_property";
    public static final String DICT_DEVICE_GROUP_PROPERTY_NAME = "name";
    public static final String DICT_DEVICE_GROUP_PROPERTY_CONTENT = "content";


}
