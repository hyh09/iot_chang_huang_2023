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
COMMENT ON COLUMN "public"."hs_dict_data"."code" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."name" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."type" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."unit" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."comment" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_data"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_data"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_data"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_data"."icon" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."picture" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_data"."tenant_id" IS '??????Id';
COMMENT ON TABLE "public"."hs_dict_data" IS '????????????';

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
COMMENT ON COLUMN "public"."hs_dict_device"."code" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device"."name" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device"."type" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device"."supplier" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device"."model" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device"."version" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device"."warranty_period" IS '?????????(???)';
COMMENT ON COLUMN "public"."hs_dict_device"."picture" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device"."icon" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device"."tenant_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_dict_device"."comment" IS '??????';
COMMENT ON TABLE "public"."hs_dict_device" IS '????????????';

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
COMMENT ON COLUMN "public"."hs_dict_device_component"."code" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."name" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."type" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."supplier" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."model" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."version" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."warranty_period" IS '?????????(???)';
COMMENT ON COLUMN "public"."hs_dict_device_component"."picture" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."parent_id" IS '???Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."dict_device_id" IS '????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."icon" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."comment" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component"."sort" IS '????????????';
COMMENT ON TABLE "public"."hs_dict_device_component" IS '????????????-??????';

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
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."component_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."content" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."name" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."dict_device_id" IS '????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."title" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."sort" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."dict_data_id" IS '????????????Id';
COMMENT ON TABLE "public"."hs_dict_device_component_property" IS '????????????-????????????';

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
COMMENT ON COLUMN "public"."hs_dict_device_group"."dict_device_id" IS '????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_group"."name" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_group"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_group"."sort" IS '????????????';
COMMENT ON TABLE "public"."hs_dict_device_group" IS '????????????-??????';

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
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_device_group_id" IS '??????????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."content" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."name" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_device_id" IS '????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."title" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."sort" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_data_id" IS '????????????Id';
COMMENT ON TABLE "public"."hs_dict_device_group_property" IS '????????????-????????????';

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
COMMENT ON COLUMN "public"."hs_dict_device_property"."dict_device_id" IS '????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_property"."name" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_property"."content" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_property"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_property"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_property"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_property"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_property"."sort" IS '????????????';
COMMENT ON TABLE "public"."hs_dict_device_property" IS '????????????-??????';

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
COMMENT ON COLUMN "public"."hs_init"."init_data" IS '???????????????';
COMMENT ON COLUMN "public"."hs_init"."scope" IS '??????';
COMMENT ON COLUMN "public"."hs_init"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_init"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_init"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_init"."updated_user" IS '?????????';
COMMENT ON TABLE "public"."hs_init" IS '?????????';

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


-- ?????????????????? ALTER TABLE public.tb_user DROP COLUMN user_code;
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

--2021-11-22??????2?????????
ALTER TABLE public.tb_user
    ADD COLUMN type character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN factory_id uuid;

--?????????????????????
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
COMMENT ON COLUMN public.device.comment IS '??????';
ALTER TABLE public.device ADD COLUMN device_no character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '????????????';

--2021/12/8??????????????????????????? additional_info
    ALTER TABLE IF EXISTS public.hs_factory
    ADD COLUMN additional_info character varying COLLATE pg_catalog."default";
	ALTER TABLE IF EXISTS public.hs_workshop
    ADD COLUMN additional_info character varying COLLATE pg_catalog."default";
	ALTER TABLE IF EXISTS public.hs_production_line
    ADD COLUMN additional_info character varying COLLATE pg_catalog."default";

--??????hs_device_component??????picture??????
alter table hs_device_component alter column picture type character varying(1000000);

--factory????????????

ALTER TABLE public.hs_factory
    ADD COLUMN country character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.country IS '??????';

ALTER TABLE public.hs_factory
    ADD COLUMN province character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.province IS '???';

ALTER TABLE public.hs_factory
    ADD COLUMN city character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.city IS '???';

ALTER TABLE public.hs_factory
    ADD COLUMN area character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.area IS '???';

--?????????
ALTER TABLE hs_factory DROP COLUMN admin_user_id;
ALTER TABLE hs_factory DROP COLUMN admin_user_name;

----?????????????????? v1.2??? ???????????????
ALTER TABLE tb_user add COLUMN user_level integer DEFAULT 0;

alter table tenant
    add county_level varchar(255);
alter table tenant
    add longitude varchar(255);
alter table tenant
    add latitude varchar(255);


alter table device
    add flg boolean default false;

----?????????????????? v1.2??? ???????????????
ALTER TABLE tb_user add COLUMN user_level integer DEFAULT 0;

alter table tenant
    add county_level varchar(255);
alter table tenant
    add longitude varchar(255);
alter table tenant
    add latitude varchar(255);

alter table device
    add flg boolean default false;



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
COMMENT ON COLUMN "public"."hs_file"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_file"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_file"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."tenant_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_file"."file_name" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."check_sum" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."content_type" IS '??????';
COMMENT ON COLUMN "public"."hs_file"."checksum_algorithm" IS '???????????????';
COMMENT ON COLUMN "public"."hs_file"."data_size" IS '??????';
COMMENT ON COLUMN "public"."hs_file"."additional_info" IS '????????????';
COMMENT ON COLUMN "public"."hs_file"."scope" IS '??????';
COMMENT ON COLUMN "public"."hs_file"."entity_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_file"."location" IS '????????????';

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
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."content" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."name" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."dict_device_id" IS '????????????Id';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."title" IS '??????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."sort" IS '????????????';
COMMENT ON COLUMN "public"."hs_dict_device_standard_property"."dict_data_id" IS '????????????Id';

-- Table Comment
COMMENT ON TABLE "public"."hs_dict_device_standard_property" IS '????????????-????????????';

-- December 6, 2021 11:03:14 GMT+8
ALTER TABLE "public"."hs_dict_device" ADD COLUMN "is_default" bool NOT NULL DEFAULT 'FALSE';
COMMENT ON COLUMN "public"."hs_dict_device"."is_default" IS '????????????';

--?????????????????????
CREATE TABLE IF NOT EXISTS public.hs_system_version
(
    id uuid NOT NULL,
    version character varying(225) COLLATE pg_catalog."default" NOT NULL DEFAULT '0.0.1'::character varying,
    publish_time bigint NOT NULL,
    comment character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_user uuid,
    created_time bigint NOT NULL,
    updated_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_system_version_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_system_version
    OWNER to postgres;

COMMENT ON TABLE public.hs_system_version
    IS '???????????????';

create table hs_energy_chart
(
    id                   uuid not null
        constraint hs_energy_chart_pkey
            primary key,
    created_time         bigint,
    created_user         uuid,
    tenant_id            uuid,
    updated_time         bigint,
    updated_user         uuid,
    capacity_added_value varchar(255),
    capacity_first_time  bigint,
    capacity_first_value varchar(255),
    capacity_last_time   bigint,
    capacity_value       varchar(255),
    date                 date,
    electric_added_value varchar(255),
    electric_first_time  bigint,
    electric_first_value varchar(255),
    electric_last_time   bigint,
    electric_value       varchar(255),
    entity_id            uuid,
    gas_added_value      varchar(255),
    gas_first_time       bigint,
    gas_first_value      varchar(255),
    gas_last_time        bigint,
    gas_value            varchar(255),
    ts                   bigint,
    water_added_value    varchar(255),
    water_first_time     bigint,
    water_first_value    varchar(255),
    water_last_time      bigint,
    water_value          varchar(255)
);

----??????????????????????????????????????????
CREATE TABLE IF NOT EXISTS public.hs_enery_time_gap
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    entity_id uuid,
    key_name character varying(255) COLLATE pg_catalog."default",
    time_gap bigint,
    ts bigint,
    value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_enery_time_gap_pkey PRIMARY KEY (id)
)

create table hs_statistical_data
(
    id                   uuid not null
        constraint hs_statistical_data_pkey
            primary key,
    created_time         bigint,
    created_user         uuid,
    tenant_id            uuid,
    updated_time         bigint,
    updated_user         uuid,
    capacity_added_value varchar(255),
    capacity_first_time  bigint,
    capacity_first_value varchar(255),
    capacity_last_time   bigint,
    capacity_value       varchar(255),
    date                 date,
    electric_added_value varchar(255),
    electric_first_time  bigint,
    electric_first_value varchar(255),
    electric_last_time   bigint,
    electric_value       varchar(255),
    entity_id            uuid,
    gas_added_value      varchar(255),
    gas_first_time       bigint,
    gas_first_value      varchar(255),
    gas_last_time        bigint,
    gas_value            varchar(255),
    ts                   bigint,
    water_added_value    varchar(255),
    water_first_time     bigint,
    water_first_value    varchar(255),
    water_last_time      bigint,
    water_value          varchar(255)
);