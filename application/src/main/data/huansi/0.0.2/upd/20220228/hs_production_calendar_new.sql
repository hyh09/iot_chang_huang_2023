
-- Table Definition
CREATE TABLE "public"."hs_production_calendar" (
                                                   "id" uuid NOT NULL,
                                                   "created_time" int8 NOT NULL,
                                                   "created_user" varchar(255),
                                                   "updated_time" int8,
                                                   "updated_user" varchar(255),
                                                   "tenant_id" uuid,
                                                   "device_id" uuid NOT NULL,
                                                   "start_time" int8,
                                                   "end_time" int8,
                                                   PRIMARY KEY ("id")
);

-- Column Comment
COMMENT ON COLUMN "public"."hs_production_calendar"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_production_calendar"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_production_calendar"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_production_calendar"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_production_calendar"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_production_calendar"."tenant_id" IS '租户Id';
COMMENT ON COLUMN "public"."hs_production_calendar"."device_id" IS '设备Id';
COMMENT ON COLUMN "public"."hs_production_calendar"."start_time" IS '开始时间';
COMMENT ON COLUMN "public"."hs_production_calendar"."end_time" IS '结束时间';


-- Table Comment
COMMENT ON TABLE "public"."hs_production_calendar" IS '生产日历';