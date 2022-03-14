CREATE TABLE "public"."hs_energy_hour" (
  "id" uuid NOT NULL,
  "created_time" int8,
  "created_user" uuid,
  "tenant_id" uuid,
  "updated_time" int8,
  "updated_user" uuid,
  "capacity_added_value" varchar(255) COLLATE "pg_catalog"."default",
  "capacity_first_time" int8,
  "capacity_first_value" varchar(255) COLLATE "pg_catalog"."default",
  "capacity_last_time" int8,
  "capacity_value" varchar(255) COLLATE "pg_catalog"."default",
  "date" date,
  "electric_added_value" varchar(255) COLLATE "pg_catalog"."default",
  "electric_first_time" int8,
  "electric_first_value" varchar(255) COLLATE "pg_catalog"."default",
  "electric_last_time" int8,
  "electric_value" varchar(255) COLLATE "pg_catalog"."default",
  "entity_id" uuid,
  "gas_added_value" varchar(255) COLLATE "pg_catalog"."default",
  "gas_first_time" int8,
  "gas_first_value" varchar(255) COLLATE "pg_catalog"."default",
  "gas_last_time" int8,
  "gas_value" varchar(255) COLLATE "pg_catalog"."default",
  "ts" int8,
  "water_added_value" varchar(255) COLLATE "pg_catalog"."default",
  "water_first_time" int8,
  "water_first_value" varchar(255) COLLATE "pg_catalog"."default",
  "water_last_time" int8,
  "water_value" varchar(255) COLLATE "pg_catalog"."default",
  CONSTRAINT "hs_energy_hour_pkey" PRIMARY KEY ("id")
)
;
