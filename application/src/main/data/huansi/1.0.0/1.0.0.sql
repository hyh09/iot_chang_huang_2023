/*
 Navicat Premium Data Transfer

 Source Server         : zsm
 Source Server Type    : PostgreSQL
 Source Server Version : 130004
 Source Host           : 10.10.11.175:5432
 Source Catalog        : thingsboard
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 130004
 File Encoding         : 65001

 Date: 23/11/2021 17:53:57
*/


-- ----------------------------
-- Table structure for hs_device_component
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_device_component" (
                                                "id" uuid NOT NULL,
                                                "created_time" int8,
                                                "code" varchar(255) COLLATE "pg_catalog"."default",
                                                "comment" varchar(255) COLLATE "pg_catalog"."default",
                                                "created_user" uuid,
                                                "device_id" uuid,
                                                "icon" varchar(255) COLLATE "pg_catalog"."default",
                                                "model" varchar(255) COLLATE "pg_catalog"."default",
                                                "name" varchar(255) COLLATE "pg_catalog"."default",
                                                "parent_id" uuid,
                                                "picture" varchar(255) COLLATE "pg_catalog"."default",
                                                "supplier" varchar(255) COLLATE "pg_catalog"."default",
                                                "type" varchar(255) COLLATE "pg_catalog"."default",
                                                "updated_time" int8,
                                                "updated_user" uuid,
                                                "version" varchar(255) COLLATE "pg_catalog"."default",
                                                "warranty_period" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for hs_dict_data
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_data" (
                                         "id" uuid NOT NULL,
                                         "code" varchar(255) COLLATE "pg_catalog"."default",
                                         "name" varchar(255) COLLATE "pg_catalog"."default",
                                         "type" varchar(255) COLLATE "pg_catalog"."default",
                                         "unit" varchar(32) COLLATE "pg_catalog"."default",
                                         "comment" varchar(255) COLLATE "pg_catalog"."default",
                                         "created_time" int8 NOT NULL,
                                         "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                         "updated_time" int8,
                                         "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                         "icon" varchar(255) COLLATE "pg_catalog"."default",
                                         "picture" varchar(1000000) COLLATE "pg_catalog"."default",
                                         "tenant_id" uuid
)
;
COMMENT ON COLUMN "public"."hs_dict_data"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_data"."code" IS '编码';
COMMENT ON COLUMN "public"."hs_dict_data"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_data"."type" IS '类型';
COMMENT ON COLUMN "public"."hs_dict_data"."unit" IS '单位';
COMMENT ON COLUMN "public"."hs_dict_data"."comment" IS '备注';
COMMENT ON COLUMN "public"."hs_dict_data"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_data"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_data"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_data"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_data"."icon" IS '图标';
COMMENT ON COLUMN "public"."hs_dict_data"."picture" IS '图片';
COMMENT ON COLUMN "public"."hs_dict_data"."tenant_id" IS '租户Id';
COMMENT ON TABLE "public"."hs_dict_data" IS '数据字典';

-- ----------------------------
-- Table structure for hs_dict_device
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device" (
                                           "id" uuid NOT NULL,
                                           "code" varchar(255) COLLATE "pg_catalog"."default",
                                           "name" varchar(255) COLLATE "pg_catalog"."default",
                                           "type" varchar(32) COLLATE "pg_catalog"."default",
                                           "supplier" varchar(255) COLLATE "pg_catalog"."default",
                                           "model" varchar(32) COLLATE "pg_catalog"."default",
                                           "version" varchar(32) COLLATE "pg_catalog"."default",
                                           "warranty_period" varchar(32) COLLATE "pg_catalog"."default",
                                           "picture" varchar(1000000) COLLATE "pg_catalog"."default",
                                           "icon" varchar(255) COLLATE "pg_catalog"."default",
                                           "created_time" int8 NOT NULL,
                                           "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                           "updated_time" int8,
                                           "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                           "tenant_id" uuid,
                                           "comment" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."hs_dict_device"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device"."code" IS '编码';
COMMENT ON COLUMN "public"."hs_dict_device"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_device"."type" IS '类型';
COMMENT ON COLUMN "public"."hs_dict_device"."supplier" IS '供应商';
COMMENT ON COLUMN "public"."hs_dict_device"."model" IS '型号';
COMMENT ON COLUMN "public"."hs_dict_device"."version" IS '版本号';
COMMENT ON COLUMN "public"."hs_dict_device"."warranty_period" IS '保修期(天)';
COMMENT ON COLUMN "public"."hs_dict_device"."picture" IS '图片';
COMMENT ON COLUMN "public"."hs_dict_device"."icon" IS '图标';
COMMENT ON COLUMN "public"."hs_dict_device"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device"."tenant_id" IS '租户Id';
COMMENT ON COLUMN "public"."hs_dict_device"."comment" IS '备注';
COMMENT ON TABLE "public"."hs_dict_device" IS '设备字典';

-- ----------------------------
-- Table structure for hs_dict_device_component
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_component" (
                                                     "id" uuid NOT NULL,
                                                     "code" varchar(255) COLLATE "pg_catalog"."default",
                                                     "name" varchar(255) COLLATE "pg_catalog"."default",
                                                     "type" varchar(32) COLLATE "pg_catalog"."default",
                                                     "supplier" varchar(255) COLLATE "pg_catalog"."default",
                                                     "model" varchar(32) COLLATE "pg_catalog"."default",
                                                     "version" varchar(32) COLLATE "pg_catalog"."default",
                                                     "warranty_period" varchar(32) COLLATE "pg_catalog"."default",
                                                     "picture" varchar(1000000) COLLATE "pg_catalog"."default",
                                                     "parent_id" uuid,
                                                     "dict_device_id" uuid,
                                                     "icon" varchar(255) COLLATE "pg_catalog"."default",
                                                     "created_time" int8 NOT NULL,
                                                     "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                     "updated_time" int8,
                                                     "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                     "comment" varchar(255) COLLATE "pg_catalog"."default",
                                                     "sort" int8
)
;
COMMENT ON COLUMN "public"."hs_dict_device_component"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."code" IS '编码';
COMMENT ON COLUMN "public"."hs_dict_device_component"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_device_component"."type" IS '类型';
COMMENT ON COLUMN "public"."hs_dict_device_component"."supplier" IS '供应商';
COMMENT ON COLUMN "public"."hs_dict_device_component"."model" IS '型号';
COMMENT ON COLUMN "public"."hs_dict_device_component"."version" IS '版本号';
COMMENT ON COLUMN "public"."hs_dict_device_component"."warranty_period" IS '保修期(天)';
COMMENT ON COLUMN "public"."hs_dict_device_component"."picture" IS '图片';
COMMENT ON COLUMN "public"."hs_dict_device_component"."parent_id" IS '父Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."icon" IS '图标';
COMMENT ON COLUMN "public"."hs_dict_device_component"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_component"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_component"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_component"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_component"."comment" IS '备注';
COMMENT ON COLUMN "public"."hs_dict_device_component"."sort" IS '排序字段';
COMMENT ON TABLE "public"."hs_dict_device_component" IS '设备字典-部件';

-- ----------------------------
-- Table structure for hs_dict_device_component_property
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_component_property" (
                                                              "id" uuid NOT NULL,
                                                              "component_id" uuid,
                                                              "content" varchar(255) COLLATE "pg_catalog"."default",
                                                              "name" varchar(255) COLLATE "pg_catalog"."default",
                                                              "created_time" int8 NOT NULL,
                                                              "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                              "updated_time" int8,
                                                              "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                              "dict_device_id" uuid,
                                                              "title" varchar(255) COLLATE "pg_catalog"."default",
                                                              "sort" int8,
                                                              "dict_data_id" uuid
)
;
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."component_id" IS '部件Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."content" IS '内容';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."title" IS '标题';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."sort" IS '排序字段';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."dict_data_id" IS '数据字典Id';
COMMENT ON TABLE "public"."hs_dict_device_component_property" IS '设备字典-部件属性';

-- ----------------------------
-- Table structure for hs_dict_device_group
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_group" (
                                                 "id" uuid NOT NULL,
                                                 "dict_device_id" uuid,
                                                 "name" varchar(255) COLLATE "pg_catalog"."default",
                                                 "created_time" int8 NOT NULL,
                                                 "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                 "updated_time" int8,
                                                 "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                 "sort" int8
)
;
COMMENT ON COLUMN "public"."hs_dict_device_group"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_group"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_group"."name" IS '分组名称';
COMMENT ON COLUMN "public"."hs_dict_device_group"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_group"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_group"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_group"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_group"."sort" IS '排序字段';
COMMENT ON TABLE "public"."hs_dict_device_group" IS '设备字典-分组';

-- ----------------------------
-- Table structure for hs_dict_device_group_property
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_group_property" (
                                                          "id" uuid NOT NULL,
                                                          "dict_device_group_id" uuid,
                                                          "content" varchar(255) COLLATE "pg_catalog"."default",
                                                          "name" varchar(255) COLLATE "pg_catalog"."default",
                                                          "created_time" int8 NOT NULL,
                                                          "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                          "updated_time" int8,
                                                          "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                          "dict_device_id" uuid,
                                                          "title" varchar(255) COLLATE "pg_catalog"."default",
                                                          "sort" int8,
                                                          "dict_data_id" uuid
)
;
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_device_group_id" IS '设备字典分组Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."content" IS '内容';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."title" IS '标题';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."sort" IS '排序字段';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_data_id" IS '数据字典Id';
COMMENT ON TABLE "public"."hs_dict_device_group_property" IS '设备字典-分组属性';

-- ----------------------------
-- Table structure for hs_dict_device_property
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_property" (
                                                    "id" uuid NOT NULL,
                                                    "dict_device_id" uuid,
                                                    "name" varchar(255) COLLATE "pg_catalog"."default",
                                                    "content" varchar(255) COLLATE "pg_catalog"."default",
                                                    "created_time" int8 NOT NULL,
                                                    "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                    "updated_time" int8,
                                                    "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                    "sort" int8
)
;
COMMENT ON COLUMN "public"."hs_dict_device_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_property"."content" IS '属性内容';
COMMENT ON COLUMN "public"."hs_dict_device_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_property"."sort" IS '排序字段';
COMMENT ON TABLE "public"."hs_dict_device_property" IS '设备字典-属性';

-- ----------------------------
-- Table structure for hs_factory
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_factory" (
                                       "id" uuid NOT NULL,
                                       "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                       "name" varchar(255) COLLATE "pg_catalog"."default",
                                       "logo_icon" varchar(255) COLLATE "pg_catalog"."default",
                                       "logo_images" varchar(1000000) COLLATE "pg_catalog"."default",
                                       "address" varchar(1000) COLLATE "pg_catalog"."default",
                                       "longitude" varchar(255) COLLATE "pg_catalog"."default",
                                       "latitude" varchar(255) COLLATE "pg_catalog"."default",
                                       "postal_code" varchar(255) COLLATE "pg_catalog"."default",
                                       "email" varchar(255) COLLATE "pg_catalog"."default",
                                       "admin_user_id" uuid,
                                       "admin_user_name" varchar(255) COLLATE "pg_catalog"."default",
                                       "remark" varchar(1000) COLLATE "pg_catalog"."default",
                                       "tenant_id" uuid NOT NULL,
                                       "created_time" int8 NOT NULL,
                                       "created_user" uuid,
                                       "updated_time" varchar(255) COLLATE "pg_catalog"."default",
                                       "updated_user" uuid,
                                       "del_flag" varchar(255) COLLATE "pg_catalog"."default",
                                       "mobile" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for hs_init
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_init" (
                                    "id" uuid NOT NULL,
                                    "init_data" jsonb,
                                    "scope" varchar(255) COLLATE "pg_catalog"."default",
                                    "created_time" int8,
                                    "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                    "updated_time" int8,
                                    "updated_user" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."hs_init"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_init"."init_data" IS '初始化数据';
COMMENT ON COLUMN "public"."hs_init"."scope" IS '范围';
COMMENT ON COLUMN "public"."hs_init"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_init"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_init"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_init"."updated_user" IS '更新人';
COMMENT ON TABLE "public"."hs_init" IS '初始化';

-- ----------------------------
-- Table structure for hs_production_line
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_production_line" (
                                               "id" uuid NOT NULL,
                                               "workshop_id" uuid NOT NULL,
                                               "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                               "name" varchar(255) COLLATE "pg_catalog"."default",
                                               "logo_icon" varchar(255) COLLATE "pg_catalog"."default",
                                               "logo_images" varchar(1000000) COLLATE "pg_catalog"."default",
                                               "remark" varchar(1000) COLLATE "pg_catalog"."default",
                                               "tenant_id" uuid NOT NULL,
                                               "created_time" int8 NOT NULL,
                                               "created_user" uuid,
                                               "updated_time" varchar(255) COLLATE "pg_catalog"."default",
                                               "updated_user" uuid,
                                               "del_flag" varchar(255) COLLATE "pg_catalog"."default",
                                               "mobile" varchar(255) COLLATE "pg_catalog"."default",
                                               "factory_id" uuid,
                                               "bg_images" varchar(100000) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for hs_workshop
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_workshop" (
                                        "id" uuid NOT NULL,
                                        "factory_id" uuid NOT NULL,
                                        "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                        "name" varchar(255) COLLATE "pg_catalog"."default",
                                        "logo_icon" varchar(255) COLLATE "pg_catalog"."default",
                                        "logo_images" varchar(1000000) COLLATE "pg_catalog"."default",
                                        "remark" varchar(1000) COLLATE "pg_catalog"."default",
                                        "tenant_id" uuid NOT NULL,
                                        "created_time" int8 NOT NULL,
                                        "created_user" uuid,
                                        "updated_time" varchar(255) COLLATE "pg_catalog"."default",
                                        "updated_user" uuid,
                                        "del_flag" varchar(255) COLLATE "pg_catalog"."default",
                                        "bg_images" varchar(1000000) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for tb_menu
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."tb_menu" (
                                    "id" uuid NOT NULL,
                                    "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                    "name" varchar(255) COLLATE "pg_catalog"."default",
                                    "level" int8 NOT NULL,
                                    "sort" int8 NOT NULL,
                                    "url" varchar(1000) COLLATE "pg_catalog"."default",
                                    "parent_id" uuid,
                                    "menu_icon" varchar(255) COLLATE "pg_catalog"."default",
                                    "menu_images" varchar(1000) COLLATE "pg_catalog"."default",
                                    "region" varchar(255) COLLATE "pg_catalog"."default",
                                    "created_time" int8 NOT NULL,
                                    "created_user" uuid,
                                    "updated_time" varchar(255) COLLATE "pg_catalog"."default",
                                    "updated_user" uuid,
                                    "menu_type" varchar(255) COLLATE "pg_catalog"."default",
                                    "path" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                    "is_button" bool DEFAULT false,
                                    "lang_key" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for tb_tenant_menu
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."tb_tenant_menu" (
                                           "id" uuid NOT NULL,
                                           "tenant_id" uuid NOT NULL,
                                           "sys_menu_id" uuid,
                                           "sys_menu_code" varchar(255) COLLATE "pg_catalog"."default",
                                           "sys_menu_name" varchar(255) COLLATE "pg_catalog"."default",
                                           "tenant_menu_name" varchar(255) COLLATE "pg_catalog"."default",
                                           "tenant_menu_code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
                                           "level" int8 NOT NULL,
                                           "sort" int8 NOT NULL,
                                           "url" varchar(1000) COLLATE "pg_catalog"."default",
                                           "parent_id" uuid,
                                           "tenant_menu_icon" varchar(255) COLLATE "pg_catalog"."default",
                                           "tenant_menu_images" varchar(1000) COLLATE "pg_catalog"."default",
                                           "region" varchar(255) COLLATE "pg_catalog"."default",
                                           "created_time" int8 NOT NULL,
                                           "created_user" uuid,
                                           "updated_time" varchar(255) COLLATE "pg_catalog"."default",
                                           "updated_user" uuid,
                                           "menu_type" varchar(255) COLLATE "pg_catalog"."default",
                                           "path" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
                                           "is_button" bool DEFAULT false,
                                           "lang_key" varchar(255) COLLATE "pg_catalog"."default",
                                           "has_children" bool DEFAULT false
)
;

-- ----------------------------
-- Table structure for tb_tenant_menu_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."tb_tenant_menu_role" (
                                                "id" uuid NOT NULL,
                                                "created_time" int8,
                                                "created_user" uuid,
                                                "tenant_id" uuid,
                                                "updated_time" int8,
                                                "updated_user" uuid,
                                                "remark" varchar(255) COLLATE "pg_catalog"."default",
                                                "tenant_menu_id" uuid,
                                                "tenant_sys_role_id" uuid,
                                                "flg" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for tb_tenant_sys_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."tb_tenant_sys_role" (
                                               "id" uuid NOT NULL,
                                               "created_time" int8,
                                               "created_user" uuid,
                                               "tenant_id" uuid,
                                               "updated_time" int8,
                                               "updated_user" uuid,
                                               "role_code" varchar(255) COLLATE "pg_catalog"."default",
                                               "role_desc" varchar(255) COLLATE "pg_catalog"."default",
                                               "role_name" varchar(255) COLLATE "pg_catalog"."default",
                                               "factory_id" uuid,
                                               "system_tab" varchar(255) COLLATE "pg_catalog"."default",
                                               "type" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Table structure for tb_user_menu_role
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."tb_user_menu_role" (
                                              "id" uuid NOT NULL,
                                              "created_time" int8,
                                              "created_user" uuid,
                                              "tenant_id" uuid,
                                              "updated_time" int8,
                                              "updated_user" uuid,
                                              "remark" varchar(255) COLLATE "pg_catalog"."default",
                                              "tenant_sys_role_id" uuid,
                                              "user_id" uuid
)
;

-- ----------------------------
-- Primary Key structure for table hs_device_component
-- ----------------------------
ALTER TABLE "public"."hs_device_component" ADD CONSTRAINT "hs_device_component_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table hs_dict_data
-- ----------------------------
ALTER TABLE "public"."hs_dict_data" ADD CONSTRAINT "uk_dict_data_code_and_tenant_id" UNIQUE ("tenant_id", "code");
ALTER TABLE "public"."hs_dict_data" ADD CONSTRAINT "uk_dict_data_name_and_tenant_id" UNIQUE ("tenant_id", "name");

-- ----------------------------
-- Primary Key structure for table hs_dict_data
-- ----------------------------
ALTER TABLE "public"."hs_dict_data" ADD CONSTRAINT "dict_data_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table hs_dict_device
-- ----------------------------
ALTER TABLE "public"."hs_dict_device" ADD CONSTRAINT "uk_dict_device_code_and_tenant_id" UNIQUE ("tenant_id", "code");

-- ----------------------------
-- Primary Key structure for table hs_dict_device
-- ----------------------------
ALTER TABLE "public"."hs_dict_device" ADD CONSTRAINT "dict_device_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_dict_device_component
-- ----------------------------
CREATE INDEX "idx_dict_device_id_2" ON "public"."hs_dict_device_component" USING btree (
  "dict_device_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table hs_dict_device_component
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_component" ADD CONSTRAINT "uk_component" UNIQUE ("dict_device_id", "code");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_component
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_component" ADD CONSTRAINT "dict_device_ component_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_component_property
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_component_property" ADD CONSTRAINT "hs_dict_device_component_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_dict_device_group
-- ----------------------------
CREATE INDEX "idx_dict_device_group_dict_device_id" ON "public"."hs_dict_device_group" USING btree (
  "dict_device_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table hs_dict_device_group
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_group" ADD CONSTRAINT "dict_device_group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table hs_dict_device_group_property
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_group_property" ADD CONSTRAINT "uk_dict_device_group_property_name_dict_device_id" UNIQUE ("dict_device_id", "name");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_group_property
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_group_property" ADD CONSTRAINT "dict_device_property_group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_dict_device_property
-- ----------------------------
CREATE INDEX "idx_dict_device_id" ON "public"."hs_dict_device_property" USING btree (
  "dict_device_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table hs_dict_device_property
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_property" ADD CONSTRAINT "dict_device_ property_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table hs_factory
-- ----------------------------
ALTER TABLE "public"."hs_factory" ADD CONSTRAINT "tb_factory_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table hs_init
-- ----------------------------
ALTER TABLE "public"."hs_init" ADD CONSTRAINT "uk_init_scope" UNIQUE ("scope");

-- ----------------------------
-- Primary Key structure for table hs_init
-- ----------------------------
ALTER TABLE "public"."hs_init" ADD CONSTRAINT "hs_dict_data_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table hs_production_line
-- ----------------------------
ALTER TABLE "public"."hs_production_line" ADD CONSTRAINT "tb_production_line_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table hs_workshop
-- ----------------------------
ALTER TABLE "public"."hs_workshop" ADD CONSTRAINT "tb_workshop_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_menu
-- ----------------------------
ALTER TABLE "public"."tb_menu" ADD CONSTRAINT "tb_menu_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_tenant_menu
-- ----------------------------
ALTER TABLE "public"."tb_tenant_menu" ADD CONSTRAINT "tb_tenant_menu_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_tenant_menu_role
-- ----------------------------
ALTER TABLE "public"."tb_tenant_menu_role" ADD CONSTRAINT "tb_tenant_menu_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_tenant_sys_role
-- ----------------------------
ALTER TABLE "public"."tb_tenant_sys_role" ADD CONSTRAINT "tb_tenant_sys_role_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_user_menu_role
-- ----------------------------
ALTER TABLE "public"."tb_user_menu_role" ADD CONSTRAINT "tb_user_menu_role_pkey" PRIMARY KEY ("id");


-- 用户表的修改 ALTER TABLE public.tb_user DROP COLUMN user_code;
ALTER TABLE public.tb_user
    ADD COLUMN user_code character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN user_creator character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN user_name character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN phone_number character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN active_status character varying(255) COLLATE pg_catalog."default";

--2021-11-22新增2个字段
ALTER TABLE public.tb_user
    ADD COLUMN type character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN factory_id uuid;

--设备表新增字段
ALTER TABLE public.device ADD COLUMN production_line_id uuid;
ALTER TABLE public.device ADD COLUMN workshop_id uuid;
ALTER TABLE public.device ADD COLUMN factory_id uuid;
ALTER TABLE public.device ADD COLUMN code character varying(1000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN picture character varying(1000000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN icon character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN dict_device_id uuid;
ALTER TABLE public.device ADD COLUMN created_time bigint ;
ALTER TABLE public.device ADD COLUMN created_user uuid;
ALTER TABLE public.device ADD COLUMN updated_time bigint;
ALTER TABLE public.device ADD COLUMN updated_user uuid;
ALTER TABLE public.device ADD COLUMN comment character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '备注';
ALTER TABLE public.device ADD COLUMN device_no character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '设备编号';


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
