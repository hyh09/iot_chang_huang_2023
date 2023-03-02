
CREATE TABLE "public"."tb_menu" (
  "id" uuid NOT NULL,
  "code" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "level" int8 NOT NULL,
  "sort" int8 NOT NULL,
  "url" varchar(1000) COLLATE "pg_catalog"."default",
  "parent_id" uuid,
  "menu_icon" varchar(255) COLLATE "pg_catalog"."default",
  "menu_images" varchar(1000) COLLATE "pg_catalog"."default",
  "region" varchar(255) COLLATE "pg_catalog"."default",
  "created_time" int8 NOT NULL,
  "created_user" uuid,
  "updated_time" varchar(255) COLLATE "pg_catalog"."default",
  "updated_user" uuid,
  "menu_type" varchar(255) COLLATE "pg_catalog"."default",
  "path" varchar(255) COLLATE "pg_catalog"."default" DEFAULT NULL::character varying,
  "is_button" bool DEFAULT false,
  "lang_key" varchar(255) COLLATE "pg_catalog"."default",
    CONSTRAINT "tb_menu_pkey" PRIMARY KEY ("id")

)
;

