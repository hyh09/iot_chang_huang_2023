-- April 11, 2022 15:01:03 GMT+8
ALTER TABLE "public"."hs_order" ADD COLUMN "is_done" bool NOT NULL DEFAULT 'false';
COMMENT ON COLUMN "public"."hs_order"."is_done" IS '是否报工完成';


-- July 22, 2022 10:08:30 GMT+8
ALTER TABLE "public"."hs_production_line" ADD COLUMN "sort" int4 NOT NULL DEFAULT 0;
COMMENT ON COLUMN "public"."hs_production_line"."sort" IS '排序';

-- July 22, 2022 17:50:48 GMT+8
UPDATE "public"."hs_production_line" SET "sort" = '1' WHERE "name" = '前处理段';
UPDATE "public"."hs_production_line" SET "sort" = '2' WHERE "name" = '转移印染段';
UPDATE "public"."hs_production_line" SET "sort" = '3' WHERE "name" = '后处理段';