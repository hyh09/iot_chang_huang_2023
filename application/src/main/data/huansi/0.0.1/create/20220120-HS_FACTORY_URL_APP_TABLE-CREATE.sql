CREATE TABLE "public"."hs_factory_url_app_table" (
  "id" uuid NOT NULL,
  "created_time" int8,
  "created_user" uuid,
  "tenant_id" uuid,
  "updated_time" int8,
  "updated_user" uuid,
  "app_url" varchar(255) COLLATE "pg_catalog"."default",
  "factory_id" varchar(255) COLLATE "pg_catalog"."default",
  "notes" varchar(255) COLLATE "pg_catalog"."default",
  CONSTRAINT "hs_factory_url_app_table_pkey" PRIMARY KEY ("id")
)
;