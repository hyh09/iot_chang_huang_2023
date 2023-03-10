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
COMMENT ON COLUMN "public"."hs_order"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."tenant_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_order"."order_no" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."total" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."production_line_id" IS '??????id';
COMMENT ON COLUMN "public"."hs_order"."workshop_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_order"."factory_id" IS '??????id';
COMMENT ON COLUMN "public"."hs_order"."contract_no" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."ref_order_no" IS '???????????????';
COMMENT ON COLUMN "public"."hs_order"."take_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."customer_order_no" IS '???????????????';
COMMENT ON COLUMN "public"."hs_order"."customer" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."type" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."biz_practice" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."currency" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."exchange_rate" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."tax_rate" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."taxes" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."total_amount" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."unit" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."unit_price_type" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."additional_amount" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."payment_method" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."emergency_degree" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."technological_requirements" IS '????????????';
COMMENT ON COLUMN "public"."hs_order"."num" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."season" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."merchandiser" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."salesman" IS '?????????';
COMMENT ON COLUMN "public"."hs_order"."short_shipment" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."over_shipment" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."comment" IS '??????';
COMMENT ON COLUMN "public"."hs_order"."intended_time" IS '??????????????????';
COMMENT ON COLUMN "public"."hs_order"."standard_available_time" IS '??????????????????';
COMMENT ON TABLE "public"."hs_order" IS '??????';

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
COMMENT ON COLUMN "public"."hs_order_plan"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_order_plan"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_order_plan"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_order_plan"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_order_plan"."tenant_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_order_plan"."device_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_order_plan"."order_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_order_plan"."intended_start_time" IS '??????????????????';
COMMENT ON COLUMN "public"."hs_order_plan"."intended_end_time" IS '??????????????????';
COMMENT ON COLUMN "public"."hs_order_plan"."enabled" IS '??????????????????';
COMMENT ON COLUMN "public"."hs_order_plan"."actual_start_time" IS '??????????????????';
COMMENT ON COLUMN "public"."hs_order_plan"."actual_end_time" IS '??????????????????';
COMMENT ON TABLE "public"."hs_order_plan" IS '??????-???????????????';

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
COMMENT ON COLUMN "public"."hs_order_plan"."sort" IS '??????';