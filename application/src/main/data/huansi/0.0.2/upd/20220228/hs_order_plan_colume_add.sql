ALTER TABLE "public"."hs_order_plan" ADD COLUMN "actual_capacity" varchar(255);
COMMENT ON COLUMN "public"."hs_order_plan"."actual_capacity" IS '实际产量';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "intended_capacity" varchar(255);
COMMENT ON COLUMN "public"."hs_order_plan"."intended_capacity" IS '计划产量';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "maintain_start_time" int8;
COMMENT ON COLUMN "public"."hs_order_plan"."maintain_start_time" IS '维护开始时间';
ALTER TABLE "public"."hs_order_plan" ADD COLUMN "maintain_end_time" int8;
COMMENT ON COLUMN "public"."hs_order_plan"."maintain_end_time" IS '维护结束时间';
