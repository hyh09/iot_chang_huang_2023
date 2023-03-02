package org.thingsboard.server.dao.hs.dao;

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
    public static final String GENERAL_SORT = "sort";

    public static final String GENERAL_PRODUCTION_LINE_ID = "production_line_id";
    public static final String GENERAL_WORKSHOP_ID = "workshop_id";
    public static final String GENERAL_FACTORY_ID = "factory_id";

    public static final String DICT_DEVICE_ID = "dict_device_id";
    public static final String DEVICE_PROFILE_ID = "device_profile_id";
    public static final String DICT_DATA_ID = "dict_data_id";

    /**
     * 初始化
     */
    public static final String INIT_TABLE_NAME = "hs_init";
    public static final String INIT_DATA = "init_data";
    public static final String INIT_SCOPE = "scope";


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
     * 设备字典表
     */
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
    public static final String DICT_DEVICE_IS_DEFAULT = "is_default";
    public static final String DICT_DEVICE_IS_CORE = "is_core";
    public static final String DICT_DEVICE_RATED_CAPACITY = "rated_capacity";

    public static final String DICT_DEVICE_COMPONENT_TABLE_NAME = "hs_dict_device_component";
    public static final String DICT_DEVICE_COMPONENT_PARENT_ID = "parent_id";
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
    public static final String DICT_DEVICE_GROUP_ID = "dict_device_group_id";
    public static final String DICT_DEVICE_GROUP_NAME = "name";

    public static final String DICT_DEVICE_GROUP_PROPERTY_TABLE_NAME = "hs_dict_device_group_property";
    public static final String DICT_DEVICE_GROUP_PROPERTY_NAME = "name";
    public static final String DICT_DEVICE_GROUP_PROPERTY_CONTENT = "content";
    public static final String DICT_DEVICE_GROUP_PROPERTY_TITLE = "title";

    public static final String DICT_DEVICE_COMPONENT_PROPERTY_TABLE_NAME = "hs_dict_device_component_property";
    public static final String DICT_DEVICE_COMPONENT_PROPERTY_COMPONENT_ID = "component_id";
    public static final String DICT_DEVICE_COMPONENT_PROPERTY_NAME = "name";
    public static final String DICT_DEVICE_COMPONENT_PROPERTY_CONTENT = "content";
    public static final String DICT_DEVICE_COMPONENT_PROPERTY_TITLE = "title";


    public static final String DICT_DEVICE_STANDARD_PROPERTY_TABLE_NAME = "hs_dict_device_standard_property";
    public static final String DICT_DEVICE_STANDARD_PROPERTY_NAME = "name";
    public static final String DICT_DEVICE_STANDARD_PROPERTY_CONTENT = "content";
    public static final String DICT_DEVICE_STANDARD_PROPERTY_TITLE = "title";

    /**
     * 文件
     */
    public static final String FILE_TABLE_NAME = "hs_file";
    public static final String FILE_FILE_NAME = "file_name";
    public static final String FILE_CHECK_SUM = "check_sum";
    public static final String FILE_CONTENT_TYPE = "content_type";
    public static final String FILE_CHECKSUM_ALGORITHM = "checksum_algorithm";
    public static final String FILE_DATA_SIZE = "data_size";
    public static final String FILE_ADDITIONAL_INFO = "additional_info";
    public static final String FILE_SCOPE = "scope";
    public static final String FILE_ENTITY_ID = "entity_id";
    public static final String FILE_LOCATION = "location";

    /**
     * 订单
     */
    public static final String ORDER_TABLE_NAME = "hs_order";
    public static final String ORDER_NO = "order_no";
    public static final String ORDER_TOTAL = "total";
    public static final String ORDER_CONTRACT_NO = "contract_no";
    public static final String ORDER_REF_ORDER_NO = "ref_order_no";
    public static final String ORDER_TAKE_TIME = "take_time";
    public static final String ORDER_CUSTOMER_ORDER_NO = "customer_order_no";
    public static final String ORDER_CUSTOMER = "customer";
    public static final String ORDER_TYPE = "type";
    public static final String ORDER_BIZ_PRACTICE = "biz_practice";
    public static final String ORDER_CURRENCY = "currency";
    public static final String ORDER_EXCHANGE_RATE = "exchange_rate";
    public static final String ORDER_TAX_RATE = "tax_rate";
    public static final String ORDER_TAXES = "taxes";
    public static final String ORDER_TOTAL_AMOUNT = "total_amount";
    public static final String ORDER_UNIT = "unit";
    public static final String ORDER_UNIT_PRICE_TYPE = "unit_price_type";
    public static final String ORDER_ADDITIONAL_AMOUNT = "additional_amount";
    public static final String ORDER_PAYMENT_METHOD = "payment_method";
    public static final String ORDER_EMERGENCY_DEGREE = "emergency_degree";
    public static final String ORDER_TECHNOLOGICAL_REQUIREMENTS = "technological_requirements";
    public static final String ORDER_NUM = "num";
    public static final String ORDER_SEASON = "season";
    public static final String ORDER_MERCHANDISER = "merchandiser";
    public static final String ORDER_SALESMAN = "salesman";
    public static final String ORDER_SHORT_SHIPMENT = "short_shipment";
    public static final String ORDER_OVER_SHIPMENT = "over_shipment";
    public static final String ORDER_COMMENT = "comment";
    public static final String ORDER_INTENDED_TIME = "intended_time";
    public static final String ORDER_STANDARD_AVAILABLE_TIME = "standard_available_time";
    public static final String ORDER_IS_DONE = "is_done";


    /**
     * 订单设备
     */
    public static final String ORDER_PLAN_TABLE_NAME = "hs_order_plan";
    public static final String ORDER_PLAN_DEVICE_ID = "device_id";
    public static final String ORDER_PLAN_ORDER_ID = "order_id";
    public static final String ORDER_PLAN_INTENDED_START_TIME = "intended_start_time";
    public static final String ORDER_PLAN_INTENDED_END_TIME = "intended_end_time";
    public static final String ORDER_PLAN_ACTUAL_START_TIME = "actual_start_time";
    public static final String ORDER_PLAN_ACTUAL_END_TIME = "actual_end_time";
    public static final String ORDER_PLAN_ENABLED = "enabled";
    public static final String ORDER_PLAN_SORT = "sort";
    public static final String ORDER_PLAN_ACTUAL_CAPACITY = "actual_capacity";
    public static final String ORDER_PLAN_INTENDED_CAPACITY = "intended_capacity";
    public static final String ORDER_PLAN_MAINTAIN_START_TIME = "maintain_start_time";
    public static final String ORDER_PLAN_MAINTAIN_END_TIME = "maintain_end_time";
    public static final String ORDER_PLAN_FACTORY_ID = "factory_id";
    public static final String ORDER_PLAN_WORKSHOP_ID = "workshop_id";
    public static final String ORDER_PLAN_PRODUCTION_LINE_ID = "production_line_id";

    /**
     * 设备字典图表
     */
    public static final String DICT_DEVICE_GRAPH_TABLE_NAME = "hs_dict_device_graph";
    public static final String DICT_DEVICE_GRAPH_NAME = "name";


    /**
     * 设备字典图表分项
     */
    public static final String DICT_DEVICE_GRAPH_ITEM_TABLE_NAME = "hs_dict_device_graph_item";

    public static final String DICT_DEVICE_GRAPH_ITEM_PROPERTY_ID = "property_id";
    public static final String DICT_DEVICE_GRAPH_ITEM_PROPERTY_TYPE = "property_type";
    public static final String DICT_DEVICE_GRAPH_ITEM_GRAPH_ID = "graph_id";
    public static final String DICT_DEVICE_GRAPH_ITEM_SORT = "sort";
    public static final String DICT_DEVICE_GRAPH_ENABLE = "enable";
    public static final String DICT_DEVICE_GRAPH_SUFFIX = "suffix";


    /**
     * 设备字典属性开关
     */
    public static final String DICT_DEVICE_SWITCH_TABLE_NAME = "hs_dict_device_switch";

    public static final String DICT_DEVICE_SWITCH_PROPERTY_ID = "property_id";
    public static final String DICT_DEVICE_SWITCH_PROPERTY_TYPE = "property_type";
    public static final String DICT_DEVICE_SWITCH_SWITCH = "switch";

    /**
     * 开机时长记录表
     */
    public static final String TREP_DAY_STA_DETAIL_TABLE_NAME = "trep_day_sta_detail";
    public static final String TREP_HSTA_DETAILL_TABLE_NAME = "trep_hsta_detail";
    public static final String TREP_STA_ENTITY_ID = "entity_id";
    public static final String TREP_STA_TENANT_ID = "tenant_id";
    public static final String TREP_STA_TOTAL_TIME = "total_time";
    public static final String TREP_STA_START_TIME = "start_time";
    public static final String TREP_STA_END_TIME = "end_time";
}
