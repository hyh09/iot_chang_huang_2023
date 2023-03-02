

-- Table Definition
CREATE TABLE IF NOT EXISTS "public"."hs_file" (
                                                  "id" uuid NOT NULL,
                                                  "created_time" int8 NOT NULL,
                                                  "created_user" varchar(255),
    "updated_time" int8,
    "updated_user" varchar(255),
    "tenant_id" uuid,
    "file_name" varchar(255),
    "check_sum" varchar(5000),
    "content_type" varchar(255),
    "checksum_algorithm" varchar(32),
    "data_size" int8,
    "additional_info" varchar,
    "scope" varchar(255),
    "entity_id" uuid,
    "location" varchar(1000),
    PRIMARY KEY ("id")
    );

-- Column Comment
COMMENT ON COLUMN "public"."hs_file"."id" IS 'id';
COMMENT ON COLUMN "public"."hs_file"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_file"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_file"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_file"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_file"."tenant_id" IS '租户Id';
COMMENT ON COLUMN "public"."hs_file"."file_name" IS '文件名';
COMMENT ON COLUMN "public"."hs_file"."check_sum" IS '校验和';
COMMENT ON COLUMN "public"."hs_file"."content_type" IS '类型';
COMMENT ON COLUMN "public"."hs_file"."checksum_algorithm" IS '校验和算法';
COMMENT ON COLUMN "public"."hs_file"."data_size" IS '大小';
COMMENT ON COLUMN "public"."hs_file"."additional_info" IS '附加信息';
COMMENT ON COLUMN "public"."hs_file"."scope" IS '范围';
COMMENT ON COLUMN "public"."hs_file"."entity_id" IS '实体Id';
COMMENT ON COLUMN "public"."hs_file"."location" IS '存储位置';

-- Table Definition
CREATE TABLE IF NOT EXISTS  "public"."hs_dict_device_standard_property" (
                                                                            "id" uuid NOT NULL,
                                                                            "content" varchar(255),
    "name" varchar(255),
    "created_time" int8 NOT NULL,
    "created_user" varchar(255),
    "updated_time" int8,
    "updated_user" varchar(255),
    "dict_device_id" uuid,
    "title" varchar(255),
    "sort" int8,
    "dict_data_id" uuid,
    PRIMARY KEY ("id")
    );

-- Column Comment
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."content" IS '内容';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."title" IS '标题';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."sort" IS '排序字段';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."dict_data_id" IS '数据字典Id';

-- Table Comment
COMMENT ON TABLE "public"."hs_dict_device_standard_property" IS '设备字典-标准属性';

-- December 6, 2021 11:03:14 GMT+8
ALTER TABLE "public"."hs_dict_device" ADD COLUMN "is_default" bool NOT NULL DEFAULT 'FALSE';
COMMENT ON COLUMN "public"."hs_dict_device"."is_default" IS '是否默认';