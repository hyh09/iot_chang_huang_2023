-- February 28, 2022 10:57:23 GMT+8
ALTER TABLE "public"."hs_dict_device" ADD COLUMN "is_core" bool NOT NULL DEFAULT 'false';
COMMENT ON COLUMN "public"."hs_dict_device"."is_core" IS '是否核心';
ALTER TABLE "public"."hs_dict_device" ADD COLUMN "rated_capacity" varchar(255) DEFAULT '0';
COMMENT ON COLUMN "public"."hs_dict_device"."rated_capacity" IS '额定产能';
