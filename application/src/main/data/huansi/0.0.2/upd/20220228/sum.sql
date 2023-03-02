-- February 28, 2022 10:57:23 GMT+8
ALTER TABLE "public"."hs_dict_device" ADD COLUMN "is_core" bool NOT NULL DEFAULT 'false';
COMMENT ON COLUMN "public"."hs_dict_device"."is_core" IS '是否核心';
ALTER TABLE "public"."hs_dict_device" ADD COLUMN "rated_capacity" varchar(255) DEFAULT '0';
COMMENT ON COLUMN "public"."hs_dict_device"."rated_capacity" IS '额定产能';

ALTER TABLE "public"."hs_dict_device_graph_item" ADD COLUMN "suffix" varchar(255);
COMMENT ON COLUMN "public"."hs_dict_device_graph_item"."suffix" IS '后缀';

-- February 28, 2022 10:44:56 GMT+8
UPDATE "public"."hs_init" SET "init_data" = '[{"id": null, "name": "能耗", "groupPropertyList": [{"id": null, "name": "water", "unit": "T", "title": "耗水量", "content": "0", "createdTime": null}, {"id": null, "name": "electric", "unit": "KWH", "title": "耗电量", "content": "0", "createdTime": null}, {"id": null, "name": "gas", "unit": "T", "title": "耗气量", "content": "0", "createdTime": null}]}, {"id": null, "name": "产量", "groupPropertyList": [{"id": null, "name": "capacities", "unit": "", "title": "总产量", "content": "0", "createdTime": null}]}]' WHERE "id" = 'a6bcd176-7538-402c-9035-5b966888faa0';

ALTER TABLE "public"."hs_order_plan" ADD COLUMN "actual_capacity" varchar(255);
COMMENT ON COLUMN "public"."hs_order_plan"."actual_capacity" IS '实际产量';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "intended_capacity" varchar(255);
COMMENT ON COLUMN "public"."hs_order_plan"."intended_capacity" IS '计划产量';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "maintain_start_time" int8;
COMMENT ON COLUMN "public"."hs_order_plan"."maintain_start_time" IS '维护开始时间';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "maintain_end_time" int8;
COMMENT ON COLUMN "public"."hs_order_plan"."maintain_end_time" IS '维护结束时间';

-- March 18, 2022 18:38:23 GMT+8
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "factory_id" uuid;
COMMENT ON COLUMN "public"."hs_order_plan"."factory_id" IS '工厂Id';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "production_line_id" uuid;
COMMENT ON COLUMN "public"."hs_order_plan"."production_line_id" IS '产线Id';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "workshop_id" uuid;
COMMENT ON COLUMN "public"."hs_order_plan"."workshop_id" IS '车间Id';