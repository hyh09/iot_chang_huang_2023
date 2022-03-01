/*
 Navicat Premium Data Transfer

 Source Server         : 测试
 Source Server Type    : PostgreSQL
 Source Server Version : 120009
 Source Host           : 121.40.253.159:30682
 Source Catalog        : hsiotdb
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120009
 File Encoding         : 65001

 Date: 25/01/2022 17:39:30
*/


-- ----------------------------
-- Table structure for hs_dict_device_graph
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_graph" (
                                                               "id" uuid NOT NULL,
                                                               "created_time" int8 NOT NULL,
                                                               "created_user" varchar(255) COLLATE "pg_catalog"."default",
    "updated_time" int8,
    "updated_user" varchar(255) COLLATE "pg_catalog"."default",
    "dict_device_id" uuid,
    "name" varchar(255) COLLATE "pg_catalog"."default",
    "enable" bool NOT NULL DEFAULT false
    )
;
ALTER TABLE "public"."hs_dict_device_graph" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_graph"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_device_graph"."enable" IS '是否显示';
COMMENT ON TABLE "public"."hs_dict_device_graph" IS '设备字典';

-- ----------------------------
-- Table structure for hs_dict_device_graph_item
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_dict_device_graph_item" (
                                                                    "id" uuid NOT NULL,
                                                                    "created_time" int8 NOT NULL,
                                                                    "created_user" varchar(255) COLLATE "pg_catalog"."default",
    "updated_time" int8,
    "updated_user" varchar(255) COLLATE "pg_catalog"."default",
    "dict_device_id" uuid,
    "property_id" uuid,
    "property_type" varchar(255) COLLATE "pg_catalog"."default",
    "graph_id" uuid,
    "sort" int4
    )
;
ALTER TABLE "public"."hs_dict_device_graph_item" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."property_id" IS '属性id';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."property_type" IS '属性类型';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."graph_id" IS '图表Id';
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."sort" IS '排序';
COMMENT ON TABLE "public"."hs_dict_device_graph_item" IS '设备字典';

-- ----------------------------
-- Primary Key structure for table hs_dict_device_graph
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_graph" ADD CONSTRAINT "hs_dict_device_graph_item_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_dict_device_graph_item
-- ----------------------------
CREATE INDEX "idx_graph_item" ON "public"."hs_dict_device_graph_item" USING btree (
  "property_id" "pg_catalog"."uuid_ops" ASC NULLS LAST,
  "property_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_graph_item_graph_id" ON "public"."hs_dict_device_graph_item" USING btree (
  "graph_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table hs_dict_device_graph_item
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_graph_item" ADD CONSTRAINT "uk_graph_item" UNIQUE ("property_id", "property_type");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_graph_item
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_graph_item" ADD CONSTRAINT "hs_dict_device_copy1_pkey" PRIMARY KEY ("id");
