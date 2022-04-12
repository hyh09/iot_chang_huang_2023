-- April 11, 2022 15:01:03 GMT+8
ALTER TABLE "public"."hs_order" ADD COLUMN "is_done" bool NOT NULL DEFAULT 'false';
COMMENT ON COLUMN "public"."hs_order"."is_done" IS '是否报工完成';