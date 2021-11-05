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
                                                          "sort" int8
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
