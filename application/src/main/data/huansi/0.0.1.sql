
-- ----------------------------
-- Table structure for hs_init
-- ----------------------------
-- ----------------------------
-- Table structure for hs_init
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_init";
CREATE TABLE "public"."hs_init" (
                                    "id" uuid NOT NULL,
                                    "init_data" jsonb,
                                    "scope" varchar(255) COLLATE "pg_catalog"."default",
                                    "created_time" int8,
                                    "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                    "updated_time" int8,
                                    "updated_user" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."hs_init" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_init"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_init"."init_data" IS '初始化数据';
COMMENT ON COLUMN "public"."hs_init"."scope" IS '范围';
COMMENT ON COLUMN "public"."hs_init"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_init"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_init"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_init"."updated_user" IS '更新人';
COMMENT ON TABLE "public"."hs_init" IS '初始化';

-- ----------------------------
-- Primary Key structure for table hs_init
-- ----------------------------
ALTER TABLE "public"."hs_init" ADD CONSTRAINT "hs_init_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Records of hs_init
-- ----------------------------
BEGIN;
INSERT INTO "public"."hs_init" VALUES ('a6bcd176-7538-402c-9035-5b966888faa0', '[{"id": null, "name": "能耗", "groupPropertyList": [{"id": null, "name": "水", "unit": null, "title": "水", "content": "0", "createdTime": null}, {"id": null, "name": "电", "unit": null, "title": "电", "content": "0", "createdTime": null}, {"id": null, "name": "气", "unit": null, "title": "气", "content": "0", "createdTime": null}]}, {"id": null, "name": "产能", "groupPropertyList": [{"id": null, "name": "总产能", "unit": null, "title": "总产能", "content": "0", "createdTime": null}]}]', 'DICT_DEVICE_GROUP', 1636522070426, '07b770d0-3bb3-11ec-ad5a-9bec5deb66b9', 1636522070426, '07b770d0-3bb3-11ec-ad5a-9bec5deb66b9');
COMMIT;

-- ----------------------------
-- Uniques structure for table hs_init
-- ----------------------------
ALTER TABLE "public"."hs_init" ADD CONSTRAINT "uk_init_scope" UNIQUE ("scope");

-- ----------------------------
-- Primary Key structure for table hs_init
-- ----------------------------
ALTER TABLE "public"."hs_init" ADD CONSTRAINT "hs_dict_data_copy1_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Table structure for hs_device_profile_dict_device
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_device_profile_dict_device";
CREATE TABLE "public"."hs_device_profile_dict_device" (
                                                          "created_time" int8 NOT NULL,
                                                          "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                          "updated_time" int8,
                                                          "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                          "device_profile_id" uuid,
                                                          "dict_device_id" uuid,
                                                          "id" uuid NOT NULL
)
;
ALTER TABLE "public"."hs_device_profile_dict_device" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."device_profile_id" IS '设备配置id';
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."dict_device_id" IS '设备字典id';
COMMENT ON COLUMN "public"."hs_device_profile_dict_device"."id" IS 'id';
COMMENT ON TABLE "public"."hs_device_profile_dict_device" IS '设备配置-设备字典关联表';

-- ----------------------------
-- Table structure for hs_dict_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_data";
CREATE TABLE "public"."hs_dict_data" (
                                         "id" uuid NOT NULL,
                                         "code" varchar(255) COLLATE "pg_catalog"."default",
                                         "name" varchar(255) COLLATE "pg_catalog"."default",
                                         "type" varchar(255) COLLATE "pg_catalog"."default",
                                         "unit" varchar(32) COLLATE "pg_catalog"."default",
                                         "comment" varchar(255) COLLATE "pg_catalog"."default",
                                         "created_time" int8 NOT NULL,
                                         "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                         "updated_time" int8,
                                         "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                         "icon" varchar(255) COLLATE "pg_catalog"."default",
                                         "picture" varchar(1000000) COLLATE "pg_catalog"."default",
                                         "tenant_id" uuid
)
;
ALTER TABLE "public"."hs_dict_data" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_data"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_data"."code" IS '编码';
COMMENT ON COLUMN "public"."hs_dict_data"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_data"."type" IS '类型';
COMMENT ON COLUMN "public"."hs_dict_data"."unit" IS '单位';
COMMENT ON COLUMN "public"."hs_dict_data"."comment" IS '备注';
COMMENT ON COLUMN "public"."hs_dict_data"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_data"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_data"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_data"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_data"."icon" IS '图标';
COMMENT ON COLUMN "public"."hs_dict_data"."picture" IS '图片';
COMMENT ON COLUMN "public"."hs_dict_data"."tenant_id" IS '租户Id';
COMMENT ON TABLE "public"."hs_dict_data" IS '数据字典';

-- ----------------------------
-- Table structure for hs_dict_device
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_device";
CREATE TABLE "public"."hs_dict_device" (
                                           "id" uuid NOT NULL,
                                           "code" varchar(255) COLLATE "pg_catalog"."default",
                                           "name" varchar(255) COLLATE "pg_catalog"."default",
                                           "type" varchar(32) COLLATE "pg_catalog"."default",
                                           "supplier" varchar(255) COLLATE "pg_catalog"."default",
                                           "model" varchar(32) COLLATE "pg_catalog"."default",
                                           "version" varchar(32) COLLATE "pg_catalog"."default",
                                           "warranty_period" varchar(32) COLLATE "pg_catalog"."default",
                                           "picture" varchar(1000000) COLLATE "pg_catalog"."default",
                                           "icon" varchar(255) COLLATE "pg_catalog"."default",
                                           "created_time" int8 NOT NULL,
                                           "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                           "updated_time" int8,
                                           "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                           "tenant_id" uuid,
                                           "comment" varchar(255) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."hs_dict_device" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device"."code" IS '编码';
COMMENT ON COLUMN "public"."hs_dict_device"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_device"."type" IS '类型';
COMMENT ON COLUMN "public"."hs_dict_device"."supplier" IS '供应商';
COMMENT ON COLUMN "public"."hs_dict_device"."model" IS '型号';
COMMENT ON COLUMN "public"."hs_dict_device"."version" IS '版本号';
COMMENT ON COLUMN "public"."hs_dict_device"."warranty_period" IS '保修期(天)';
COMMENT ON COLUMN "public"."hs_dict_device"."picture" IS '图片';
COMMENT ON COLUMN "public"."hs_dict_device"."icon" IS '图标';
COMMENT ON COLUMN "public"."hs_dict_device"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device"."tenant_id" IS '租户Id';
COMMENT ON COLUMN "public"."hs_dict_device"."comment" IS '备注';
COMMENT ON TABLE "public"."hs_dict_device" IS '设备字典';

-- ----------------------------
-- Table structure for hs_dict_device_component
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_device_component";
CREATE TABLE "public"."hs_dict_device_component" (
                                                     "id" uuid NOT NULL,
                                                     "code" varchar(255) COLLATE "pg_catalog"."default",
                                                     "name" varchar(255) COLLATE "pg_catalog"."default",
                                                     "type" varchar(32) COLLATE "pg_catalog"."default",
                                                     "supplier" varchar(255) COLLATE "pg_catalog"."default",
                                                     "model" varchar(32) COLLATE "pg_catalog"."default",
                                                     "version" varchar(32) COLLATE "pg_catalog"."default",
                                                     "warranty_period" varchar(32) COLLATE "pg_catalog"."default",
                                                     "picture" varchar(1000000) COLLATE "pg_catalog"."default",
                                                     "parent_id" uuid,
                                                     "dict_device_id" uuid,
                                                     "icon" varchar(255) COLLATE "pg_catalog"."default",
                                                     "created_time" int8 NOT NULL,
                                                     "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                     "updated_time" int8,
                                                     "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                     "comment" varchar(255) COLLATE "pg_catalog"."default",
                                                     "sort" int8
)
;
ALTER TABLE "public"."hs_dict_device_component" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_component"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."code" IS '编码';
COMMENT ON COLUMN "public"."hs_dict_device_component"."name" IS '名称';
COMMENT ON COLUMN "public"."hs_dict_device_component"."type" IS '类型';
COMMENT ON COLUMN "public"."hs_dict_device_component"."supplier" IS '供应商';
COMMENT ON COLUMN "public"."hs_dict_device_component"."model" IS '型号';
COMMENT ON COLUMN "public"."hs_dict_device_component"."version" IS '版本号';
COMMENT ON COLUMN "public"."hs_dict_device_component"."warranty_period" IS '保修期(天)';
COMMENT ON COLUMN "public"."hs_dict_device_component"."picture" IS '图片';
COMMENT ON COLUMN "public"."hs_dict_device_component"."parent_id" IS '父Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_component"."icon" IS '图标';
COMMENT ON COLUMN "public"."hs_dict_device_component"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_component"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_component"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_component"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_component"."comment" IS '备注';
COMMENT ON COLUMN "public"."hs_dict_device_component"."sort" IS '排序字段';
COMMENT ON TABLE "public"."hs_dict_device_component" IS '设备字典-部件';

-- ----------------------------
-- Table structure for hs_dict_device_component_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_device_component_property";
CREATE TABLE "public"."hs_dict_device_component_property" (
                                                              "id" uuid NOT NULL,
                                                              "component_id" uuid,
                                                              "content" varchar(255) COLLATE "pg_catalog"."default",
                                                              "name" varchar(255) COLLATE "pg_catalog"."default",
                                                              "created_time" int8 NOT NULL,
                                                              "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                              "updated_time" int8,
                                                              "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                              "dict_device_id" uuid,
                                                              "title" varchar(255) COLLATE "pg_catalog"."default",
                                                              "sort" int8,
                                                              "dict_data_id" uuid
)
;
ALTER TABLE "public"."hs_dict_device_component_property" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."component_id" IS '部件Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."content" IS '内容';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."title" IS '标题';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."sort" IS '排序字段';
COMMENT ON COLUMN "public"."hs_dict_device_component_property"."dict_data_id" IS '数据字典Id';
COMMENT ON TABLE "public"."hs_dict_device_component_property" IS '设备字典-部件属性';

-- ----------------------------
-- Table structure for hs_dict_device_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_device_group";
CREATE TABLE "public"."hs_dict_device_group" (
                                                 "id" uuid NOT NULL,
                                                 "dict_device_id" uuid,
                                                 "name" varchar(255) COLLATE "pg_catalog"."default",
                                                 "created_time" int8 NOT NULL,
                                                 "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                 "updated_time" int8,
                                                 "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                 "sort" int8
)
;
ALTER TABLE "public"."hs_dict_device_group" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_group"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_group"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_group"."name" IS '分组名称';
COMMENT ON COLUMN "public"."hs_dict_device_group"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_group"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_group"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_group"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_group"."sort" IS '排序字段';
COMMENT ON TABLE "public"."hs_dict_device_group" IS '设备字典-分组';

-- ----------------------------
-- Table structure for hs_dict_device_group_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_device_group_property";
CREATE TABLE "public"."hs_dict_device_group_property" (
                                                          "id" uuid NOT NULL,
                                                          "dict_device_group_id" uuid,
                                                          "content" varchar(255) COLLATE "pg_catalog"."default",
                                                          "name" varchar(255) COLLATE "pg_catalog"."default",
                                                          "created_time" int8 NOT NULL,
                                                          "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                          "updated_time" int8,
                                                          "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                          "dict_device_id" uuid,
                                                          "title" varchar(255) COLLATE "pg_catalog"."default",
                                                          "sort" int8,
                                                          "dict_data_id" uuid
)
;
ALTER TABLE "public"."hs_dict_device_group_property" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_device_group_id" IS '设备字典分组Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."content" IS '内容';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."title" IS '标题';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."sort" IS '排序字段';
COMMENT ON COLUMN "public"."hs_dict_device_group_property"."dict_data_id" IS '数据字典Id';
COMMENT ON TABLE "public"."hs_dict_device_group_property" IS '设备字典-分组属性';

-- ----------------------------
-- Table structure for hs_dict_device_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."hs_dict_device_property";
CREATE TABLE "public"."hs_dict_device_property" (
                                                    "id" uuid NOT NULL,
                                                    "dict_device_id" uuid,
                                                    "name" varchar(255) COLLATE "pg_catalog"."default",
                                                    "content" varchar(255) COLLATE "pg_catalog"."default",
                                                    "created_time" int8 NOT NULL,
                                                    "created_user" varchar(255) COLLATE "pg_catalog"."default",
                                                    "updated_time" int8,
                                                    "updated_user" varchar(255) COLLATE "pg_catalog"."default",
                                                    "sort" int8
)
;
ALTER TABLE "public"."hs_dict_device_property" OWNER TO "postgres";
COMMENT ON COLUMN "public"."hs_dict_device_property"."id" IS 'Id';
COMMENT ON COLUMN "public"."hs_dict_device_property"."dict_device_id" IS '设备字典Id';
COMMENT ON COLUMN "public"."hs_dict_device_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."hs_dict_device_property"."content" IS '属性内容';
COMMENT ON COLUMN "public"."hs_dict_device_property"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."hs_dict_device_property"."created_user" IS '创建人';
COMMENT ON COLUMN "public"."hs_dict_device_property"."updated_time" IS '更新时间';
COMMENT ON COLUMN "public"."hs_dict_device_property"."updated_user" IS '更新人';
COMMENT ON COLUMN "public"."hs_dict_device_property"."sort" IS '排序字段';
COMMENT ON TABLE "public"."hs_dict_device_property" IS '设备字典-属性';

-- ----------------------------
-- Uniques structure for table hs_device_profile_dict_device
-- ----------------------------
ALTER TABLE "public"."hs_device_profile_dict_device" ADD CONSTRAINT "uk_device_profile_id_dict_device_id" UNIQUE ("device_profile_id", "dict_device_id");

-- ----------------------------
-- Primary Key structure for table hs_device_profile_dict_device
-- ----------------------------
ALTER TABLE "public"."hs_device_profile_dict_device" ADD CONSTRAINT "hs_device_profile_dict_device_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table hs_dict_data
-- ----------------------------
ALTER TABLE "public"."hs_dict_data" ADD CONSTRAINT "uk_code_and_tenant_id" UNIQUE ("code", "tenant_id");

-- ----------------------------
-- Primary Key structure for table hs_dict_data
-- ----------------------------
ALTER TABLE "public"."hs_dict_data" ADD CONSTRAINT "dict_data_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table hs_dict_device
-- ----------------------------
ALTER TABLE "public"."hs_dict_device" ADD CONSTRAINT "uk_code_and_tenant_id_2" UNIQUE ("code", "tenant_id");

-- ----------------------------
-- Primary Key structure for table hs_dict_device
-- ----------------------------
ALTER TABLE "public"."hs_dict_device" ADD CONSTRAINT "dict_device_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_dict_device_component
-- ----------------------------
CREATE INDEX "idx_dict_device_id_2" ON "public"."hs_dict_device_component" USING btree (
  "dict_device_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table hs_dict_device_component
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_component" ADD CONSTRAINT "uk_component" UNIQUE ("dict_device_id", "code");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_component
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_component" ADD CONSTRAINT "dict_device_ component_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_group
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_group" ADD CONSTRAINT "dict_device_group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table hs_dict_device_group_property
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_group_property" ADD CONSTRAINT "dict_device_property_group_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table hs_dict_device_property
-- ----------------------------
CREATE INDEX "idx_dict_device_id" ON "public"."hs_dict_device_property" USING btree (
  "dict_device_id" "pg_catalog"."uuid_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table hs_dict_device_property
-- ----------------------------
ALTER TABLE "public"."hs_dict_device_property" ADD CONSTRAINT "dict_device_ property_pkey" PRIMARY KEY ("id");



--系统菜单表
CREATE TABLE IF NOT EXISTS public.tb_menu
(
    id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    level bigint NOT NULL,
    sort bigint NOT NULL,
    url character varying(1000) COLLATE pg_catalog."default",
    parent_id uuid,
    menu_icon character varying(255) COLLATE pg_catalog."default",
    menu_images character varying(1000) COLLATE pg_catalog."default",
    menu_type character varying(255) COLLATE pg_catalog."default",
    path character varying(255) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    is_button boolean DEFAULT false,
    lang_key character varying(255) COLLATE pg_catalog."default",
    region character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    CONSTRAINT tb_menu_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE public.tb_menu
    OWNER to postgres;


--租户菜单表
CREATE TABLE IF NOT EXISTS public.tb_tenant_menu
(
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    sys_menu_id uuid NOT NULL,
    sys_menu_code character varying(255) COLLATE pg_catalog."default",
    sys_menu_name character varying(255) COLLATE pg_catalog."default",
    tenant_menu_name character varying(255) COLLATE pg_catalog."default",
    tenant_menu_code character varying(255) COLLATE pg_catalog."default",
    level bigint NOT NULL,
    sort bigint NOT NULL,
    url character varying(1000) COLLATE pg_catalog."default",
    parent_id uuid,
    tenant_menu_icon character varying(255) COLLATE pg_catalog."default",
    tenant_menu_images character varying(1000) COLLATE pg_catalog."default",
    menu_type character varying(255) COLLATE pg_catalog."default",
    path character varying(255) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    is_button boolean DEFAULT false,
    lang_key character varying(255) COLLATE pg_catalog."default",
    region character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    CONSTRAINT tb_tenant_menu_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE public.tb_tenant_menu
    OWNER to postgres;


--工厂表
CREATE TABLE IF NOT EXISTS public.hs_factory
(
    id uuid NOT NULL,
    code character varying(255) NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000) COLLATE pg_catalog."default",
    address character varying(1000) COLLATE pg_catalog."default",
    longitude character varying(255) COLLATE pg_catalog."default",
    latitude character varying(255) COLLATE pg_catalog."default",
    postal_code character varying(255) COLLATE pg_catalog."default",
    mobile character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    admin_user_id uuid,
    admin_user_name character varying(255) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_factory_pkey PRIMARY KEY (id)
    );
--车间表
CREATE TABLE IF NOT EXISTS public.hs_workshop
(
    id uuid NOT NULL,
    factory_id uuid NOT NULL,
    code character varying(255) NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    bg_images character varying(100000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_workshop_pkey PRIMARY KEY (id)
    );
--产线表
CREATE TABLE IF NOT EXISTS public.hs_production_line
(
    id uuid NOT NULL,
    workshop_id uuid NOT NULL,
    factory_id uuid NOT NULL,
    code character varying(255) NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000) COLLATE pg_catalog."default",
    bg_images character varying(100000) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_production_line_pkey PRIMARY KEY (id)
    );

--设备表新增字段
ALTER TABLE public.device ADD COLUMN production_line_id uuid;
ALTER TABLE public.device ADD COLUMN workshop_id uuid;
ALTER TABLE public.device ADD COLUMN factory_id uuid;
ALTER TABLE public.device ADD COLUMN code character varying(1000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN images character varying(1000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN icon character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN dict_device_id uuid;
ALTER TABLE public.device ADD COLUMN created_time bigint ;
ALTER TABLE public.device ADD COLUMN created_user uuid;
ALTER TABLE public.device ADD COLUMN updated_time bigint;
ALTER TABLE public.device ADD COLUMN updated_user uuid;


-- 用户表的修改 ALTER TABLE public.tb_user DROP COLUMN user_code;
ALTER TABLE public.tb_user
    ADD COLUMN user_code character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN user_creator character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN user_name character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN phone_number character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN active_status character varying(255) COLLATE pg_catalog."default";

-- Table: public.tb_tenant_sys_role
-- 角色-菜单 关系数 表 ------
-- DROP TABLE public.tb_tenant_sys_role;

CREATE TABLE IF NOT EXISTS public.tb_tenant_sys_role
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    updated_time bigint,
    updated_user uuid,
    role_code character varying(255) COLLATE pg_catalog."default",
    role_desc character varying(255) COLLATE pg_catalog."default",
    role_name character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid,
    CONSTRAINT tb_tenant_sys_role_pkey PRIMARY KEY (id)
)
CREATE TABLE IF NOT EXISTS public.tb_tenant_menu_role
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    updated_time bigint,
    updated_user uuid,
    remark character varying(255) COLLATE pg_catalog."default",
    tenant_menu_id uuid,
    tenant_sys_role_id uuid,
    tenant_id uuid,
    flg character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_tenant_menu_role_pkey PRIMARY KEY (id)
)
CREATE TABLE IF NOT EXISTS public.tb_user_menu_role
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    updated_time bigint,
    updated_user uuid,
    remark character varying(255) COLLATE pg_catalog."default",
    tenant_sys_role_id uuid,
    user_id uuid,
    tenant_id uuid,
    CONSTRAINT tb_user_menu_role_pkey PRIMARY KEY (id)
)

--新增字段 是否树节点（true/false）
ALTER TABLE public.tb_tenant_menu ADD COLUMN has_children boolean DEFAULT false;
--修改tb_tenant_menu
ALTER TABLE public.tb_tenant_menu ALTER sys_menu_code DROP not null;
ALTER TABLE public.tb_tenant_menu ALTER sys_menu_id DROP not null;
--更改工厂表logo图片长度
ALTER TABLE public.hs_factory alter COLUMN logo_images type character varying(1000000);
--更改车间表logo图片长度
ALTER TABLE public.hs_workshop alter COLUMN logo_images type character varying(1000000);
--更改车间表bg图片长度
ALTER TABLE public.hs_workshop alter COLUMN bg_images type character varying(1000000);
--更改产线表logo图片长度
ALTER TABLE public.hs_production_line alter COLUMN logo_images type character varying(1000000);

--修改设备表字段名
ALTER TABLE public.device RENAME images  to picture ;
ALTER TABLE public.device alter COLUMN picture type character varying(1000000);
ALTER TABLE public.device ADD COLUMN comment character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '备注';
ALTER TABLE public.device ADD COLUMN device_no character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '设备编号';

-- 用户表的修改 ALTER TABLE public.tb_user DROP COLUMN user_code;
ALTER TABLE public.tb_user
    ADD COLUMN user_code character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN user_creator character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN user_name character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN phone_number character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN active_status character varying(255) COLLATE pg_catalog."default";

--2021-11-22新增2个字段
ALTER TABLE public.tb_user
    ADD COLUMN type character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN factory_id uuid;

--设备表新增字段
ALTER TABLE public.device ADD COLUMN production_line_id uuid;
ALTER TABLE public.device ADD COLUMN workshop_id uuid;
ALTER TABLE public.device ADD COLUMN factory_id uuid;
ALTER TABLE public.device ADD COLUMN code character varying(1000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN picture character varying(1000000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN icon character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN dict_device_id uuid;
ALTER TABLE public.device ADD COLUMN created_time bigint ;
ALTER TABLE public.device ADD COLUMN created_user uuid;
ALTER TABLE public.device ADD COLUMN updated_time bigint;
ALTER TABLE public.device ADD COLUMN updated_user uuid;
ALTER TABLE public.device ADD COLUMN comment character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '备注';
ALTER TABLE public.device ADD COLUMN device_no character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '设备编号';