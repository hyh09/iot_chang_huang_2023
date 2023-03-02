CREATE TABLE IF NOT EXISTS "public"."device" (
  "id" uuid NOT NULL,
  "created_time" int8 NOT NULL,
  "additional_info" varchar COLLATE "pg_catalog"."default",
  "customer_id" uuid,
  "device_profile_id" uuid NOT NULL,
  "device_data" jsonb,
  "type" varchar(255) COLLATE "pg_catalog"."default",
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "label" varchar(255) COLLATE "pg_catalog"."default",
  "search_text" varchar(255) COLLATE "pg_catalog"."default",
  "tenant_id" uuid,
  "firmware_id" uuid,
  "software_id" uuid,
  "production_line_id" uuid,
  "workshop_id" uuid,
  "factory_id" uuid,
  "code" varchar(1000) COLLATE "pg_catalog"."default",
  "picture" varchar(1000000) COLLATE "pg_catalog"."default",
  "icon" varchar(2000000) COLLATE "pg_catalog"."default",
  "dict_device_id" uuid,
  "created_user" uuid,
  "updated_time" int8,
  "updated_user" uuid,
  "comment" varchar(255) COLLATE "pg_catalog"."default",
  "device_no" varchar(255) COLLATE "pg_catalog"."default",
  "flg" bool DEFAULT false,
  "sort" int8,
  "rename" varchar(255) COLLATE "pg_catalog"."default",
    CONSTRAINT "device_pkey" PRIMARY KEY ("id")

)
;


CREATE INDEX "idx_device_customer_id" ON "public"."device" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST,
  "customer_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

CREATE INDEX "idx_device_customer_id_and_type" ON "public"."device" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST,
  "customer_id" "pg_catalog"."uuid_ops" ASC NULLS LAST,
  "type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

CREATE INDEX "idx_device_device_profile_id" ON "public"."device" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST,
  "device_profile_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

CREATE INDEX "idx_device_factory_id" ON "public"."device" USING btree (
  "factory_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

CREATE INDEX "idx_device_tenant_id" ON "public"."device" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

CREATE INDEX "idx_device_type" ON "public"."device" USING btree (
  "tenant_id" "pg_catalog"."uuid_ops" ASC NULLS LAST,
  "type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

CREATE INDEX "production_line_id_index" ON "public"."device" USING btree (
  "production_line_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

CREATE INDEX "workshop_id_index" ON "public"."device" USING btree (
  "workshop_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

CREATE TRIGGER "trigger_device" AFTER INSERT OR UPDATE OR DELETE ON "public"."device"
FOR EACH ROW
EXECUTE PROCEDURE "public"."notify_global_data_change_device"();

COMMENT ON COLUMN "public"."device"."comment" IS '设备编号';

COMMENT ON COLUMN "public"."device"."device_no" IS '设备编号';

COMMENT ON COLUMN "public"."device"."sort" IS '排序值';

COMMENT ON COLUMN "public"."device"."rename" IS '重命名';