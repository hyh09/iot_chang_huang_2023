/*
 Navicat Premium Data Transfer

 Source Server         : dev
 Source Server Type    : PostgreSQL
 Source Server Version : 140001
 Source Host           : 10.10.10.56:5432
 Source Catalog        : thingsboard
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 140001
 File Encoding         : 65001

 Date: 05/01/2022 17:20:09
*/


-- ----------------------------
-- Table structure for hs_order
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_order" (
                                                   "id" uuid NOT NULL,
                                                   "created_time" int8 NOT NULL,
                                                   "created_user" varchar(255) COLLATE "pg_catalog"."default",
    "updated_time" int8,
    "updated_user" varchar(255) COLLATE "pg_catalog"."default",
    "tenant_id" uuid,
    "order_no" varchar(255) COLLATE "pg_catalog"."default",
    "total" numeric(64,2),
    "production_line_id" uuid,
    "workshop_id" uuid,
    "factory_id" uuid,
    "contract_no" varchar(255) COLLATE "pg_catalog"."default",
    "ref_order_no" varchar(255) COLLATE "pg_catalog"."default",
    "take_time" int8,
    "customer_order_no" varchar(255) COLLATE "pg_catalog"."default",
    "customer" varchar(255) COLLATE "pg_catalog"."default",
    "type" varchar(255) COLLATE "pg_catalog"."default",
    "biz_practice" varchar(255) COLLATE "pg_catalog"."default",
    "currency" varchar(255) COLLATE "pg_catalog"."default",
    "exchange_rate" varchar(255) COLLATE "pg_catalog"."default",
    "tax_rate" varchar(255) COLLATE "pg_catalog"."default",
    "taxes" varchar(255) COLLATE "pg_catalog"."default",
    "total_amount" numeric(16,2),
    "unit" varchar(255) COLLATE "pg_catalog"."default",
    "unit_price_type" varchar(255) COLLATE "pg_catalog"."default",
    "additional_amount" numeric(16,2),
    "payment_method" varchar(255) COLLATE "pg_catalog"."default",
    "emergency_degree" varchar(255) COLLATE "pg_catalog"."default",
    "technological_requirements" varchar(255) COLLATE "pg_catalog"."default",
    "num" numeric(64,2),
    "season" varchar(255) COLLATE "pg_catalog"."default",
    "merchandiser" varchar(255) COLLATE "pg_catalog"."default",
    "salesman" varchar(255) COLLATE "pg_catalog"."default",
    "short_shipment" varchar(255) COLLATE "pg_catalog"."default",
    "over_shipment" varchar(255) COLLATE "pg_catalog"."default",
    "comment" varchar(255) COLLATE "pg_catalog"."default",
    "intended_time" int8,
    "standard_available_time" numeric(64,2)
    )
;
ALTER TABLE "public"."hs_order" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_order"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_order"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_order"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_order"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_order"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_order"."tenant_id" IS '租户Id';
COMMENT ON COLUMN "public"."hs_order"."order_no" IS '订单号';
COMMENT ON COLUMN "public"."hs_order"."total" IS '总数量';
COMMENT ON COLUMN "public"."hs_order"."production_line_id" IS '产线id';
COMMENT ON COLUMN "public"."hs_order"."workshop_id" IS '车间Id';
COMMENT ON COLUMN "public"."hs_order"."factory_id" IS '工厂id';
COMMENT ON COLUMN "public"."hs_order"."contract_no" IS '合同号';
COMMENT ON COLUMN "public"."hs_order"."ref_order_no" IS '参考合同号';
COMMENT ON COLUMN "public"."hs_order"."take_time" IS '接单日期';
COMMENT ON COLUMN "public"."hs_order"."customer_order_no" IS '客户订单号';
COMMENT ON COLUMN "public"."hs_order"."customer" IS '客户';
COMMENT ON COLUMN "public"."hs_order"."type" IS '订单类型';
COMMENT ON COLUMN "public"."hs_order"."biz_practice" IS '经营方式';
COMMENT ON COLUMN "public"."hs_order"."currency" IS '币种';
COMMENT ON COLUMN "public"."hs_order"."exchange_rate" IS '汇率';
COMMENT ON COLUMN "public"."hs_order"."tax_rate" IS '税率';
COMMENT ON COLUMN "public"."hs_order"."taxes" IS '税种';
COMMENT ON COLUMN "public"."hs_order"."total_amount" IS '总金额';
COMMENT ON COLUMN "public"."hs_order"."unit" IS '单位';
COMMENT ON COLUMN "public"."hs_order"."unit_price_type" IS '单价类型';
COMMENT ON COLUMN "public"."hs_order"."additional_amount" IS '附加金额';
COMMENT ON COLUMN "public"."hs_order"."payment_method" IS '付款方式';
COMMENT ON COLUMN "public"."hs_order"."emergency_degree" IS '紧急程度';
COMMENT ON COLUMN "public"."hs_order"."technological_requirements" IS '工艺要求';
COMMENT ON COLUMN "public"."hs_order"."num" IS '数量';
COMMENT ON COLUMN "public"."hs_order"."season" IS '季节';
COMMENT ON COLUMN "public"."hs_order"."merchandiser" IS '跟单员';
COMMENT ON COLUMN "public"."hs_order"."salesman" IS '销售员';
COMMENT ON COLUMN "public"."hs_order"."short_shipment" IS '短装';
COMMENT ON COLUMN "public"."hs_order"."over_shipment" IS '溢装';
COMMENT ON COLUMN "public"."hs_order"."comment" IS '备注';
COMMENT ON COLUMN "public"."hs_order"."intended_time" IS '计划完工时间';
COMMENT ON COLUMN "public"."hs_order"."standard_available_time" IS '标准可用时间';
COMMENT ON TABLE "public"."hs_order" IS '订单';

-- ----------------------------
-- Table structure for hs_order_plan
-- ----------------------------
CREATE TABLE IF NOT EXISTS "public"."hs_order_plan" (
                                                        "id" uuid NOT NULL,
                                                        "created_time" int8 NOT NULL,
                                                        "created_user" varchar(255) COLLATE "pg_catalog"."default",
    "updated_time" int8,
    "updated_user" varchar(255) COLLATE "pg_catalog"."default",
    "tenant_id" uuid NOT NULL,
    "device_id" uuid NOT NULL,
    "order_id" uuid NOT NULL,
    "intended_start_time" int8,
    "intended_end_time" int8,
    "enabled" bool NOT NULL DEFAULT false,
    "actual_start_time" int8,
    "actual_end_time" int8
    )
;
ALTER TABLE "public"."hs_order_plan" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_order_plan"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_order_plan"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_order_plan"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_order_plan"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_order_plan"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_order_plan"."tenant_id" IS '租户Id';
COMMENT ON COLUMN "public"."hs_order_plan"."device_id" IS '设备Id';
COMMENT ON COLUMN "public"."hs_order_plan"."order_id" IS '订单Id';
COMMENT ON COLUMN "public"."hs_order_plan"."intended_start_time" IS '计划开始时间';
COMMENT ON COLUMN "public"."hs_order_plan"."intended_end_time" IS '计划结束时间';
COMMENT ON COLUMN "public"."hs_order_plan"."enabled" IS '是否参与运算';
COMMENT ON COLUMN "public"."hs_order_plan"."actual_start_time" IS '实际开始时间';
COMMENT ON COLUMN "public"."hs_order_plan"."actual_end_time" IS '实际结束时间';
COMMENT ON TABLE "public"."hs_order_plan" IS '订单-设备关联表';

-- ----------------------------
-- Indexes structure for table hs_order
-- ----------------------------
CREATE INDEX "hs_order_tenant_id" ON "public"."hs_order" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table hs_order
-- ----------------------------
ALTER TABLE "public"."hs_order" ADD CONSTRAINT "uk_hs_order_no" UNIQUE ("tenant_id", "order_no");

-- ----------------------------
-- Primary Key structure for table hs_order
-- ----------------------------
ALTER TABLE "public"."hs_order" ADD CONSTRAINT "hs_dict_data_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_order_plan
-- ----------------------------
CREATE INDEX "hs_order_device_tenant_id" ON "public"."hs_order_plan" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table hs_order_plan
-- ----------------------------
ALTER TABLE "public"."hs_order_plan" ADD CONSTRAINT "hs_order_copy1_pkey" PRIMARY KEY ("id");


-- January 6, 2022 11:26:22 GMT+8
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "sort" int4 DEFAULT 0;
COMMENT ON COLUMN "public"."hs_order_plan"."sort" IS '排序';