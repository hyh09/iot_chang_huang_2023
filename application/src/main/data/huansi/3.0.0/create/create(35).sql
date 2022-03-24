-- Table: public.component_descriptor

-- DROP TABLE IF EXISTS public.component_descriptor;

CREATE TABLE IF NOT EXISTS public.component_descriptor
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    actions character varying(255) COLLATE pg_catalog."default",
    clazz character varying COLLATE pg_catalog."default",
    configuration_descriptor character varying COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    scope character varying(255) COLLATE pg_catalog."default",
    search_text character varying(255) COLLATE pg_catalog."default",
    type character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT component_descriptor_pkey PRIMARY KEY (id),
    CONSTRAINT component_descriptor_clazz_key UNIQUE (clazz)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.component_descriptor
    OWNER to postgres;

-- Trigger: trigger_component_descriptor

-- DROP TRIGGER IF EXISTS trigger_component_descriptor ON public.component_descriptor;

CREATE TRIGGER trigger_component_descriptor
    AFTER INSERT OR DELETE OR UPDATE
    ON public.component_descriptor
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_all();



-- Table: public.hs_device_component

-- DROP TABLE IF EXISTS public.hs_device_component;

CREATE TABLE IF NOT EXISTS public.hs_device_component
(
    id uuid NOT NULL,
    created_time bigint,
    code character varying(255) COLLATE pg_catalog."default",
    comment character varying(255) COLLATE pg_catalog."default",
    created_user uuid,
    device_id uuid,
    icon character varying(255) COLLATE pg_catalog."default",
    model character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    parent_id uuid,
    picture character varying(1000000) COLLATE pg_catalog."default",
    supplier character varying(255) COLLATE pg_catalog."default",
    type character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user uuid,
    version character varying(255) COLLATE pg_catalog."default",
    warranty_period character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_device_component_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_device_component
    OWNER to postgres;

-- Trigger: trigger_hs_device_component

-- DROP TRIGGER IF EXISTS trigger_hs_device_component ON public.hs_device_component;

CREATE TRIGGER trigger_hs_device_component
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_device_component
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_device_component();



-- Table: public.hs_device_oee_every_hour

-- DROP TABLE IF EXISTS public.hs_device_oee_every_hour;

CREATE TABLE IF NOT EXISTS public.hs_device_oee_every_hour
(
    id uuid NOT NULL,
    device_id uuid NOT NULL,
    ts bigint NOT NULL,
    oee_value numeric NOT NULL,
    created_time bigint NOT NULL,
    tenant_id uuid NOT NULL,
    factory_id uuid,
    workshop_id uuid,
    production_line_id uuid,
    CONSTRAINT hs_device_oee_every_hour_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_device_oee_every_hour
    OWNER to postgres;
-- Index: hs_device_oee_every_hour_device_id_ts

-- DROP INDEX IF EXISTS public.hs_device_oee_every_hour_device_id_ts;

CREATE INDEX IF NOT EXISTS hs_device_oee_every_hour_device_id_ts
    ON public.hs_device_oee_every_hour USING btree
    (device_id ASC NULLS LAST, ts ASC NULLS LAST)
    TABLESPACE pg_default;




-- Table: public.hs_dict_data

-- DROP TABLE IF EXISTS public.hs_dict_data;

CREATE TABLE IF NOT EXISTS public.hs_dict_data
(
    id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    type character varying(255) COLLATE pg_catalog."default",
    unit character varying(32) COLLATE pg_catalog."default",
    comment character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    icon character varying(255) COLLATE pg_catalog."default",
    picture character varying(1000000) COLLATE pg_catalog."default",
    tenant_id uuid,
    CONSTRAINT dict_data_pkey PRIMARY KEY (id),
    CONSTRAINT uk_code_and_tenant_id UNIQUE (code, tenant_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_data
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_data
    IS '数据字典';

COMMENT ON COLUMN public.hs_dict_data.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_data.code
    IS '编码';

COMMENT ON COLUMN public.hs_dict_data.name
    IS '名称';

COMMENT ON COLUMN public.hs_dict_data.type
    IS '类型';

COMMENT ON COLUMN public.hs_dict_data.unit
    IS '单位';

COMMENT ON COLUMN public.hs_dict_data.comment
    IS '备注';

COMMENT ON COLUMN public.hs_dict_data.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_data.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_data.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_data.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_data.icon
    IS '图标';

COMMENT ON COLUMN public.hs_dict_data.picture
    IS '图片';

COMMENT ON COLUMN public.hs_dict_data.tenant_id
    IS '租户Id';

-- Trigger: trigger_hs_dict_data

-- DROP TRIGGER IF EXISTS trigger_hs_dict_data ON public.hs_dict_data;

CREATE TRIGGER trigger_hs_dict_data
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_data
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();



-- Table: public.hs_dict_device

-- DROP TABLE IF EXISTS public.hs_dict_device;

CREATE TABLE IF NOT EXISTS public.hs_dict_device
(
    id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    type character varying(32) COLLATE pg_catalog."default",
    supplier character varying(255) COLLATE pg_catalog."default",
    model character varying(32) COLLATE pg_catalog."default",
    version character varying(32) COLLATE pg_catalog."default",
    warranty_period character varying(32) COLLATE pg_catalog."default",
    picture character varying(1000000) COLLATE pg_catalog."default",
    icon character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid,
    comment character varying(255) COLLATE pg_catalog."default",
    is_default boolean NOT NULL DEFAULT false,
    is_core boolean NOT NULL DEFAULT false,
    rated_capacity character varying(255) COLLATE pg_catalog."default" DEFAULT '0'::character varying,
    CONSTRAINT dict_device_pkey PRIMARY KEY (id),
    CONSTRAINT uk_code_and_tenant_id_2 UNIQUE (code, tenant_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device
    IS '设备字典';

COMMENT ON COLUMN public.hs_dict_device.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device.code
    IS '编码';

COMMENT ON COLUMN public.hs_dict_device.name
    IS '名称';

COMMENT ON COLUMN public.hs_dict_device.type
    IS '类型';

COMMENT ON COLUMN public.hs_dict_device.supplier
    IS '供应商';

COMMENT ON COLUMN public.hs_dict_device.model
    IS '型号';

COMMENT ON COLUMN public.hs_dict_device.version
    IS '版本号';

COMMENT ON COLUMN public.hs_dict_device.warranty_period
    IS '保修期(天)';

COMMENT ON COLUMN public.hs_dict_device.picture
    IS '图片';

COMMENT ON COLUMN public.hs_dict_device.icon
    IS '图标';

COMMENT ON COLUMN public.hs_dict_device.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device.tenant_id
    IS '租户Id';

COMMENT ON COLUMN public.hs_dict_device.comment
    IS '备注';

COMMENT ON COLUMN public.hs_dict_device.is_default
    IS '是否默认';

COMMENT ON COLUMN public.hs_dict_device.is_core
    IS '是否核心';

COMMENT ON COLUMN public.hs_dict_device.rated_capacity
    IS '额定产能';

-- Trigger: trigger_hs_dict_device

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device ON public.hs_dict_device;

CREATE TRIGGER trigger_hs_dict_device
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();



-- Table: public.hs_dict_device_component

-- DROP TABLE IF EXISTS public.hs_dict_device_component;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_component
(
    id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    type character varying(32) COLLATE pg_catalog."default",
    supplier character varying(255) COLLATE pg_catalog."default",
    model character varying(32) COLLATE pg_catalog."default",
    version character varying(32) COLLATE pg_catalog."default",
    warranty_period character varying(32) COLLATE pg_catalog."default",
    picture character varying(1000000) COLLATE pg_catalog."default",
    parent_id uuid,
    dict_device_id uuid,
    icon character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    comment character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    key character varying(255) COLLATE pg_catalog."default",
    dict_data_id uuid,
    content character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT "dict_device_ component_pkey" PRIMARY KEY (id),
    CONSTRAINT uk_component UNIQUE (dict_device_id, code)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_component
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_component
    IS '设备字典-部件';

COMMENT ON COLUMN public.hs_dict_device_component.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_component.code
    IS '编码';

COMMENT ON COLUMN public.hs_dict_device_component.name
    IS '名称';

COMMENT ON COLUMN public.hs_dict_device_component.type
    IS '类型';

COMMENT ON COLUMN public.hs_dict_device_component.supplier
    IS '供应商';

COMMENT ON COLUMN public.hs_dict_device_component.model
    IS '型号';

COMMENT ON COLUMN public.hs_dict_device_component.version
    IS '版本号';

COMMENT ON COLUMN public.hs_dict_device_component.warranty_period
    IS '保修期(天)';

COMMENT ON COLUMN public.hs_dict_device_component.picture
    IS '图片';

COMMENT ON COLUMN public.hs_dict_device_component.parent_id
    IS '父Id';

COMMENT ON COLUMN public.hs_dict_device_component.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_component.icon
    IS '图标';

COMMENT ON COLUMN public.hs_dict_device_component.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_component.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_component.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_component.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_component.comment
    IS '备注';

COMMENT ON COLUMN public.hs_dict_device_component.sort
    IS '排序字段';

COMMENT ON COLUMN public.hs_dict_device_component.key
    IS '属性';

COMMENT ON COLUMN public.hs_dict_device_component.dict_data_id
    IS '数据字典Id';

COMMENT ON COLUMN public.hs_dict_device_component.content
    IS '内容';
-- Index: idx_dict_device_id_2

-- DROP INDEX IF EXISTS public.idx_dict_device_id_2;

CREATE INDEX IF NOT EXISTS idx_dict_device_id_2
    ON public.hs_dict_device_component USING btree
    (dict_device_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Trigger: trigger_hs_dict_device_component

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_component ON public.hs_dict_device_component;

CREATE TRIGGER trigger_hs_dict_device_component
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_component
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_component();




-- Table: public.hs_dict_device_component_property

-- DROP TABLE IF EXISTS public.hs_dict_device_component_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_component_property
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    component_id uuid,
    content character varying(255) COLLATE pg_catalog."default",
    dict_data_id uuid,
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    title character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_dict_device_component_property_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_component_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_component_property
    IS '设备字典-部件属性';

COMMENT ON COLUMN public.hs_dict_device_component_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_component_property.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_component_property.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_component_property.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_component_property.component_id
    IS '部件Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.content
    IS '内容';

COMMENT ON COLUMN public.hs_dict_device_component_property.dict_data_id
    IS '数据字典Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.name
    IS '属性名称';

COMMENT ON COLUMN public.hs_dict_device_component_property.sort
    IS '排序字段';

COMMENT ON COLUMN public.hs_dict_device_component_property.title
    IS '标题';

-- Trigger: trigger_hs_dict_device_component_property

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_component_property ON public.hs_dict_device_component_property;

CREATE TRIGGER trigger_hs_dict_device_component_property
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_component_property
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_component_property();



-- Table: public.hs_dict_device_graph

-- DROP TABLE IF EXISTS public.hs_dict_device_graph;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_graph
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    enable boolean NOT NULL DEFAULT false,
    CONSTRAINT hs_dict_device_graph_item_copy1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_graph
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_graph
    IS '设备字典';

COMMENT ON COLUMN public.hs_dict_device_graph.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_graph.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_graph.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_graph.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_graph.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_graph.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_graph.name
    IS '名称';

COMMENT ON COLUMN public.hs_dict_device_graph.enable
    IS '是否显示';

-- Trigger: trigger_hs_dict_device_graph

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_graph ON public.hs_dict_device_graph;

CREATE TRIGGER trigger_hs_dict_device_graph
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_graph
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_graph();


-- Table: public.hs_dict_device_graph_item

-- DROP TABLE IF EXISTS public.hs_dict_device_graph_item;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_graph_item
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    property_id uuid,
    property_type character varying(255) COLLATE pg_catalog."default",
    graph_id uuid,
    sort integer,
    suffix character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_dict_device_copy1_pkey PRIMARY KEY (id),
    CONSTRAINT uk_graph_item UNIQUE (dict_device_id, property_id, property_type)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_graph_item
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_graph_item
    IS '设备字典';

COMMENT ON COLUMN public.hs_dict_device_graph_item.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_graph_item.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_graph_item.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_graph_item.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_graph_item.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_graph_item.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_graph_item.property_id
    IS '属性id';

COMMENT ON COLUMN public.hs_dict_device_graph_item.property_type
    IS '属性类型';

COMMENT ON COLUMN public.hs_dict_device_graph_item.graph_id
    IS '图表Id';

COMMENT ON COLUMN public.hs_dict_device_graph_item.sort
    IS '排序';

COMMENT ON COLUMN public.hs_dict_device_graph_item.suffix
    IS '后缀';
-- Index: idx_graph_item

-- DROP INDEX IF EXISTS public.idx_graph_item;

CREATE INDEX IF NOT EXISTS idx_graph_item
    ON public.hs_dict_device_graph_item USING btree
    (dict_device_id ASC NULLS LAST, property_id ASC NULLS LAST, property_type COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: idx_graph_item_graph_id

-- DROP INDEX IF EXISTS public.idx_graph_item_graph_id;

CREATE INDEX IF NOT EXISTS idx_graph_item_graph_id
    ON public.hs_dict_device_graph_item USING btree
    (graph_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Trigger: trigger_hs_dict_device_graph_item

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_graph_item ON public.hs_dict_device_graph_item;

CREATE TRIGGER trigger_hs_dict_device_graph_item
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_graph_item
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_graph_item();



-- Table: public.hs_dict_device_group

-- DROP TABLE IF EXISTS public.hs_dict_device_group;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_group
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    CONSTRAINT dict_device_group_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_group
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_group
    IS '设备字典-分组';

COMMENT ON COLUMN public.hs_dict_device_group.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_group.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_group.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_group.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_group.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_group.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_group.name
    IS '分组名称';

COMMENT ON COLUMN public.hs_dict_device_group.sort
    IS '排序字段';

-- Trigger: trigger_hs_dict_device_group

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_group ON public.hs_dict_device_group;

CREATE TRIGGER trigger_hs_dict_device_group
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_group
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_group();




-- Table: public.hs_dict_device_group_property

-- DROP TABLE IF EXISTS public.hs_dict_device_group_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_group_property
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    content character varying(255) COLLATE pg_catalog."default",
    dict_data_id uuid,
    dict_device_group_id uuid,
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    title character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_dict_device_group_property_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_group_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_group_property
    IS '设备字典-分组属性';

COMMENT ON COLUMN public.hs_dict_device_group_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_group_property.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_group_property.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_group_property.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_group_property.content
    IS '内容';

COMMENT ON COLUMN public.hs_dict_device_group_property.dict_data_id
    IS '数据字典Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.dict_device_group_id
    IS '设备字典分组Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.name
    IS '属性名称';

COMMENT ON COLUMN public.hs_dict_device_group_property.sort
    IS '排序字段';

COMMENT ON COLUMN public.hs_dict_device_group_property.title
    IS '标题';

-- Trigger: trigger_hs_dict_device_group_property

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_group_property ON public.hs_dict_device_group_property;

CREATE TRIGGER trigger_hs_dict_device_group_property
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_group_property
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_group_property();



-- Table: public.hs_dict_device_property

-- DROP TABLE IF EXISTS public.hs_dict_device_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_property
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    content character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    CONSTRAINT "dict_device_ property_pkey" PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_property
    IS '设备字典-属性';

COMMENT ON COLUMN public.hs_dict_device_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_property.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_property.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_property.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_property.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_property.content
    IS '属性内容';

COMMENT ON COLUMN public.hs_dict_device_property.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_property.name
    IS '属性名称';

COMMENT ON COLUMN public.hs_dict_device_property.sort
    IS '排序字段';
-- Index: idx_dict_device_id

-- DROP INDEX IF EXISTS public.idx_dict_device_id;

CREATE INDEX IF NOT EXISTS idx_dict_device_id
    ON public.hs_dict_device_property USING btree
    (dict_device_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Trigger: trigger_hs_dict_device_property

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_property ON public.hs_dict_device_property;

CREATE TRIGGER trigger_hs_dict_device_property
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_property
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_property();


-- Table: public.hs_dict_device_standard_property

-- DROP TABLE IF EXISTS public.hs_dict_device_standard_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_standard_property
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    content character varying(255) COLLATE pg_catalog."default",
    dict_data_id uuid,
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    title character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_dict_device_group_property_copy1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_standard_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_standard_property
    IS '设备字典-标准属性';

COMMENT ON COLUMN public.hs_dict_device_standard_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_standard_property.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_dict_device_standard_property.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_dict_device_standard_property.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_dict_device_standard_property.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_dict_device_standard_property.content
    IS '内容';

COMMENT ON COLUMN public.hs_dict_device_standard_property.dict_data_id
    IS '数据字典Id';

COMMENT ON COLUMN public.hs_dict_device_standard_property.dict_device_id
    IS '设备字典Id';

COMMENT ON COLUMN public.hs_dict_device_standard_property.name
    IS '属性名称';

COMMENT ON COLUMN public.hs_dict_device_standard_property.sort
    IS '排序字段';

COMMENT ON COLUMN public.hs_dict_device_standard_property.title
    IS '标题';

-- Trigger: trigger_hs_dict_device_standard_property

-- DROP TRIGGER IF EXISTS trigger_hs_dict_device_standard_property ON public.hs_dict_device_standard_property;

CREATE TRIGGER trigger_hs_dict_device_standard_property
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_dict_device_standard_property
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_hs_dict_device_standard_property();





-- Table: public.hs_energy_chart

-- DROP TABLE IF EXISTS public.hs_energy_chart;

CREATE TABLE IF NOT EXISTS public.hs_energy_chart
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    capacity_added_value character varying(255) COLLATE pg_catalog."default",
    capacity_first_time bigint,
    capacity_first_value character varying(255) COLLATE pg_catalog."default",
    capacity_last_time bigint,
    capacity_value character varying(255) COLLATE pg_catalog."default",
    date date,
    electric_added_value character varying(255) COLLATE pg_catalog."default",
    electric_first_time bigint,
    electric_first_value character varying(255) COLLATE pg_catalog."default",
    electric_last_time bigint,
    electric_value character varying(255) COLLATE pg_catalog."default",
    entity_id uuid,
    gas_added_value character varying(255) COLLATE pg_catalog."default",
    gas_first_time bigint,
    gas_first_value character varying(255) COLLATE pg_catalog."default",
    gas_last_time bigint,
    gas_value character varying(255) COLLATE pg_catalog."default",
    ts bigint,
    water_added_value character varying(255) COLLATE pg_catalog."default",
    water_first_time bigint,
    water_first_value character varying(255) COLLATE pg_catalog."default",
    water_last_time bigint,
    water_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_energy_chart_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_energy_chart
    OWNER to postgres;




-- Table: public.hs_energy_hour

-- DROP TABLE IF EXISTS public.hs_energy_hour;

CREATE TABLE IF NOT EXISTS public.hs_energy_hour
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    capacity_added_value character varying(255) COLLATE pg_catalog."default",
    capacity_first_time bigint,
    capacity_first_value character varying(255) COLLATE pg_catalog."default",
    capacity_last_time bigint,
    capacity_value character varying(255) COLLATE pg_catalog."default",
    date date,
    electric_added_value character varying(255) COLLATE pg_catalog."default",
    electric_first_time bigint,
    electric_first_value character varying(255) COLLATE pg_catalog."default",
    electric_last_time bigint,
    electric_value character varying(255) COLLATE pg_catalog."default",
    entity_id uuid,
    gas_added_value character varying(255) COLLATE pg_catalog."default",
    gas_first_time bigint,
    gas_first_value character varying(255) COLLATE pg_catalog."default",
    gas_last_time bigint,
    gas_value character varying(255) COLLATE pg_catalog."default",
    ts bigint,
    water_added_value character varying(255) COLLATE pg_catalog."default",
    water_first_time bigint,
    water_first_value character varying(255) COLLATE pg_catalog."default",
    water_last_time bigint,
    water_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_energy_hour_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_energy_hour
    OWNER to postgres;




-- Table: public.hs_energy_minute

-- DROP TABLE IF EXISTS public.hs_energy_minute;

CREATE TABLE IF NOT EXISTS public.hs_energy_minute
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    capacity_added_value character varying(255) COLLATE pg_catalog."default",
    capacity_first_time bigint,
    capacity_first_value character varying(255) COLLATE pg_catalog."default",
    capacity_last_time bigint,
    capacity_value character varying(255) COLLATE pg_catalog."default",
    date date,
    electric_added_value character varying(255) COLLATE pg_catalog."default",
    electric_first_time bigint,
    electric_first_value character varying(255) COLLATE pg_catalog."default",
    electric_last_time bigint,
    electric_value character varying(255) COLLATE pg_catalog."default",
    entity_id uuid,
    gas_added_value character varying(255) COLLATE pg_catalog."default",
    gas_first_time bigint,
    gas_first_value character varying(255) COLLATE pg_catalog."default",
    gas_last_time bigint,
    gas_value character varying(255) COLLATE pg_catalog."default",
    ts bigint,
    water_added_value character varying(255) COLLATE pg_catalog."default",
    water_first_time bigint,
    water_first_value character varying(255) COLLATE pg_catalog."default",
    water_last_time bigint,
    water_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_energy_minute_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_energy_minute
    OWNER to postgres;



-- Table: public.hs_enery_time_gap

-- DROP TABLE IF EXISTS public.hs_enery_time_gap;

CREATE TABLE IF NOT EXISTS public.hs_enery_time_gap
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    entity_id uuid,
    key_name character varying(255) COLLATE pg_catalog."default",
    time_gap bigint,
    ts bigint,
    value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_enery_time_gap_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_enery_time_gap
    OWNER to postgres;




-- Table: public.hs_factory

-- DROP TABLE IF EXISTS public.hs_factory;

CREATE TABLE IF NOT EXISTS public.hs_factory
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    address character varying(1000) COLLATE pg_catalog."default",
    area character varying(1000) COLLATE pg_catalog."default",
    city character varying(1000) COLLATE pg_catalog."default",
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    country character varying(255) COLLATE pg_catalog."default",
    created_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    email character varying(255) COLLATE pg_catalog."default",
    latitude character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(2000000) COLLATE pg_catalog."default",
    logo_images character varying(1000000) COLLATE pg_catalog."default",
    longitude character varying(255) COLLATE pg_catalog."default",
    mobile character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    postal_code character varying(255) COLLATE pg_catalog."default",
    province character varying(1000) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    admin_user_id uuid,
    admin_user_name character varying(255) COLLATE pg_catalog."default",
    additional_info character varying COLLATE pg_catalog."default",
    CONSTRAINT tb_factory_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_factory
    OWNER to postgres;

COMMENT ON COLUMN public.hs_factory.area
    IS '区';

COMMENT ON COLUMN public.hs_factory.city
    IS '市';

COMMENT ON COLUMN public.hs_factory.province
    IS '省';

-- Trigger: trigger_hs_factory

-- DROP TRIGGER IF EXISTS trigger_hs_factory ON public.hs_factory;

CREATE TRIGGER trigger_hs_factory
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_factory
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_factory();





-- Table: public.hs_factory_url_app_table

-- DROP TABLE IF EXISTS public.hs_factory_url_app_table;

CREATE TABLE IF NOT EXISTS public.hs_factory_url_app_table
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    app_url character varying(255) COLLATE pg_catalog."default",
    factory_id character varying(255) COLLATE pg_catalog."default",
    notes character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_factory_url_app_table_pkey PRIMARY KEY (id),
    CONSTRAINT app_url_index UNIQUE (app_url)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_factory_url_app_table
    OWNER to postgres;

-- Trigger: trigger_hs_factory_url_app_table

-- DROP TRIGGER IF EXISTS trigger_hs_factory_url_app_table ON public.hs_factory_url_app_table;

CREATE TRIGGER trigger_hs_factory_url_app_table
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_factory_url_app_table
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();





-- Table: public.hs_file

-- DROP TABLE IF EXISTS public.hs_file;

CREATE TABLE IF NOT EXISTS public.hs_file
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid,
    file_name character varying(255) COLLATE pg_catalog."default",
    check_sum character varying(5000) COLLATE pg_catalog."default",
    content_type character varying(255) COLLATE pg_catalog."default",
    checksum_algorithm character varying(32) COLLATE pg_catalog."default",
    data_size bigint,
    additional_info character varying COLLATE pg_catalog."default",
    scope character varying(255) COLLATE pg_catalog."default",
    entity_id uuid,
    location character varying(1000) COLLATE pg_catalog."default",
    CONSTRAINT hs_file_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_file
    OWNER to postgres;

COMMENT ON COLUMN public.hs_file.id
    IS 'id';

COMMENT ON COLUMN public.hs_file.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_file.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_file.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_file.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_file.tenant_id
    IS '租户Id';

COMMENT ON COLUMN public.hs_file.file_name
    IS '文件名';

COMMENT ON COLUMN public.hs_file.check_sum
    IS '校验和';

COMMENT ON COLUMN public.hs_file.content_type
    IS '类型';

COMMENT ON COLUMN public.hs_file.checksum_algorithm
    IS '校验和算法';

COMMENT ON COLUMN public.hs_file.data_size
    IS '大小';

COMMENT ON COLUMN public.hs_file.additional_info
    IS '附加信息';

COMMENT ON COLUMN public.hs_file.scope
    IS '范围';

COMMENT ON COLUMN public.hs_file.entity_id
    IS '实体Id';

COMMENT ON COLUMN public.hs_file.location
    IS '存储位置';




-- Table: public.hs_init

-- DROP TABLE IF EXISTS public.hs_init;

CREATE TABLE IF NOT EXISTS public.hs_init
(
    id uuid NOT NULL,
    created_time bigint,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    init_data jsonb,
    scope character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_init_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_init
    OWNER to postgres;

COMMENT ON TABLE public.hs_init
    IS '初始化';

COMMENT ON COLUMN public.hs_init.id
    IS 'Id';

COMMENT ON COLUMN public.hs_init.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_init.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_init.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_init.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_init.init_data
    IS '初始化数据';

COMMENT ON COLUMN public.hs_init.scope
    IS '范围';

-- Trigger: trigger_hs_init

-- DROP TRIGGER IF EXISTS trigger_hs_init ON public.hs_init;

CREATE TRIGGER trigger_hs_init
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_init
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_all();





-- Table: public.hs_order

-- DROP TABLE IF EXISTS public.hs_order;

CREATE TABLE IF NOT EXISTS public.hs_order
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    additional_amount numeric(16,2),
    biz_practice character varying(255) COLLATE pg_catalog."default",
    comment character varying(255) COLLATE pg_catalog."default",
    contract_no character varying(255) COLLATE pg_catalog."default",
    currency character varying(255) COLLATE pg_catalog."default",
    customer character varying(255) COLLATE pg_catalog."default",
    customer_order_no character varying(255) COLLATE pg_catalog."default",
    emergency_degree character varying(255) COLLATE pg_catalog."default",
    exchange_rate character varying(255) COLLATE pg_catalog."default",
    factory_id uuid,
    intended_time bigint,
    merchandiser character varying(255) COLLATE pg_catalog."default",
    num numeric(64,2),
    order_no character varying(255) COLLATE pg_catalog."default",
    over_shipment character varying(255) COLLATE pg_catalog."default",
    payment_method character varying(255) COLLATE pg_catalog."default",
    production_line_id uuid,
    ref_order_no character varying(255) COLLATE pg_catalog."default",
    salesman character varying(255) COLLATE pg_catalog."default",
    season character varying(255) COLLATE pg_catalog."default",
    short_shipment character varying(255) COLLATE pg_catalog."default",
    standard_available_time numeric(64,2),
    take_time bigint,
    tax_rate character varying(255) COLLATE pg_catalog."default",
    taxes character varying(255) COLLATE pg_catalog."default",
    technological_requirements character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid,
    total numeric(64,2),
    total_amount numeric(16,2),
    type character varying(255) COLLATE pg_catalog."default",
    unit character varying(255) COLLATE pg_catalog."default",
    unit_price_type character varying(255) COLLATE pg_catalog."default",
    workshop_id uuid,
    CONSTRAINT hs_dict_data_copy1_pkey PRIMARY KEY (id),
    CONSTRAINT uk_hs_order_no UNIQUE (tenant_id, order_no)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_order
    OWNER to postgres;

COMMENT ON TABLE public.hs_order
    IS '订单';

COMMENT ON COLUMN public.hs_order.id
    IS 'Id';

COMMENT ON COLUMN public.hs_order.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_order.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_order.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_order.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_order.additional_amount
    IS '附加金额';

COMMENT ON COLUMN public.hs_order.biz_practice
    IS '经营方式';

COMMENT ON COLUMN public.hs_order.comment
    IS '备注';

COMMENT ON COLUMN public.hs_order.contract_no
    IS '合同号';

COMMENT ON COLUMN public.hs_order.currency
    IS '币种';

COMMENT ON COLUMN public.hs_order.customer
    IS '客户';

COMMENT ON COLUMN public.hs_order.customer_order_no
    IS '客户订单号';

COMMENT ON COLUMN public.hs_order.emergency_degree
    IS '紧急程度';

COMMENT ON COLUMN public.hs_order.exchange_rate
    IS '汇率';

COMMENT ON COLUMN public.hs_order.factory_id
    IS '工厂id';

COMMENT ON COLUMN public.hs_order.intended_time
    IS '计划完工时间';

COMMENT ON COLUMN public.hs_order.merchandiser
    IS '跟单员';

COMMENT ON COLUMN public.hs_order.num
    IS '数量';

COMMENT ON COLUMN public.hs_order.order_no
    IS '订单号';

COMMENT ON COLUMN public.hs_order.over_shipment
    IS '溢装';

COMMENT ON COLUMN public.hs_order.payment_method
    IS '付款方式';

COMMENT ON COLUMN public.hs_order.production_line_id
    IS '产线id';

COMMENT ON COLUMN public.hs_order.ref_order_no
    IS '参考合同号';

COMMENT ON COLUMN public.hs_order.salesman
    IS '销售员';

COMMENT ON COLUMN public.hs_order.season
    IS '季节';

COMMENT ON COLUMN public.hs_order.short_shipment
    IS '短装';

COMMENT ON COLUMN public.hs_order.standard_available_time
    IS '标准可用时间';

COMMENT ON COLUMN public.hs_order.take_time
    IS '接单日期';

COMMENT ON COLUMN public.hs_order.tax_rate
    IS '税率';

COMMENT ON COLUMN public.hs_order.taxes
    IS '税种';

COMMENT ON COLUMN public.hs_order.technological_requirements
    IS '工艺要求';

COMMENT ON COLUMN public.hs_order.tenant_id
    IS '租户Id';

COMMENT ON COLUMN public.hs_order.total
    IS '总数量';

COMMENT ON COLUMN public.hs_order.total_amount
    IS '总金额';

COMMENT ON COLUMN public.hs_order.type
    IS '订单类型';

COMMENT ON COLUMN public.hs_order.unit
    IS '单位';

COMMENT ON COLUMN public.hs_order.unit_price_type
    IS '单价类型';

COMMENT ON COLUMN public.hs_order.workshop_id
    IS '车间Id';
-- Index: hs_order_tenant_id

-- DROP INDEX IF EXISTS public.hs_order_tenant_id;

CREATE INDEX IF NOT EXISTS hs_order_tenant_id
    ON public.hs_order USING btree
    (tenant_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Trigger: trigger_hs_order

-- DROP TRIGGER IF EXISTS trigger_hs_order ON public.hs_order;

CREATE TRIGGER trigger_hs_order
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_order
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();





-- Table: public.hs_order_plan

-- DROP TABLE IF EXISTS public.hs_order_plan;

CREATE TABLE IF NOT EXISTS public.hs_order_plan
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    actual_end_time bigint,
    actual_start_time bigint,
    device_id uuid NOT NULL,
    enabled boolean NOT NULL DEFAULT false,
    intended_end_time bigint,
    intended_start_time bigint,
    order_id uuid NOT NULL,
    sort integer DEFAULT 0,
    tenant_id uuid NOT NULL,
    actual_capacity character varying(255) COLLATE pg_catalog."default",
    intended_capacity character varying(255) COLLATE pg_catalog."default",
    maintain_start_time bigint,
    maintain_end_time bigint,
    factory_id uuid,
    production_line_id uuid,
    workshop_id uuid,
    CONSTRAINT hs_order_copy1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_order_plan
    OWNER to postgres;

COMMENT ON TABLE public.hs_order_plan
    IS '订单-设备关联表';

COMMENT ON COLUMN public.hs_order_plan.id
    IS 'Id';

COMMENT ON COLUMN public.hs_order_plan.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_order_plan.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_order_plan.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_order_plan.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_order_plan.actual_end_time
    IS '实际结束时间';

COMMENT ON COLUMN public.hs_order_plan.actual_start_time
    IS '实际开始时间';

COMMENT ON COLUMN public.hs_order_plan.device_id
    IS '设备Id';

COMMENT ON COLUMN public.hs_order_plan.enabled
    IS '是否参与运算';

COMMENT ON COLUMN public.hs_order_plan.intended_end_time
    IS '计划结束时间';

COMMENT ON COLUMN public.hs_order_plan.intended_start_time
    IS '计划开始时间';

COMMENT ON COLUMN public.hs_order_plan.order_id
    IS '订单Id';

COMMENT ON COLUMN public.hs_order_plan.sort
    IS '排序';

COMMENT ON COLUMN public.hs_order_plan.tenant_id
    IS '租户Id';

COMMENT ON COLUMN public.hs_order_plan.actual_capacity
    IS '实际产量';

COMMENT ON COLUMN public.hs_order_plan.intended_capacity
    IS '计划产量';

COMMENT ON COLUMN public.hs_order_plan.maintain_start_time
    IS '维护开始时间';

COMMENT ON COLUMN public.hs_order_plan.maintain_end_time
    IS '维护结束时间';

COMMENT ON COLUMN public.hs_order_plan.factory_id
    IS '工厂Id';

COMMENT ON COLUMN public.hs_order_plan.production_line_id
    IS '产线Id';

COMMENT ON COLUMN public.hs_order_plan.workshop_id
    IS '车间Id';
-- Index: hs_order_device_tenant_id

-- DROP INDEX IF EXISTS public.hs_order_device_tenant_id;

CREATE INDEX IF NOT EXISTS hs_order_device_tenant_id
    ON public.hs_order_plan USING btree
    (tenant_id ASC NULLS LAST)
    TABLESPACE pg_default;

-- Trigger: trigger_hs_order_plan

-- DROP TRIGGER IF EXISTS trigger_hs_order_plan ON public.hs_order_plan;

CREATE TRIGGER trigger_hs_order_plan
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_order_plan
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();





-- Table: public.hs_production_calendar

-- DROP TABLE IF EXISTS public.hs_production_calendar;

CREATE TABLE IF NOT EXISTS public.hs_production_calendar
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid,
    device_id uuid NOT NULL,
    start_time bigint,
    end_time bigint,
    factory_id uuid,
    device_name character varying(255) COLLATE pg_catalog."default",
    factory_name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_dict_device_copy1_pkey1 PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_production_calendar
    OWNER to postgres;

COMMENT ON TABLE public.hs_production_calendar
    IS '设备字典';

COMMENT ON COLUMN public.hs_production_calendar.id
    IS 'Id';

COMMENT ON COLUMN public.hs_production_calendar.created_time
    IS '创建时间';

COMMENT ON COLUMN public.hs_production_calendar.created_user
    IS '创建人';

COMMENT ON COLUMN public.hs_production_calendar.updated_time
    IS '更新时间';

COMMENT ON COLUMN public.hs_production_calendar.updated_user
    IS '更新人';

COMMENT ON COLUMN public.hs_production_calendar.tenant_id
    IS '租户Id';

COMMENT ON COLUMN public.hs_production_calendar.device_id
    IS '设备Id';

COMMENT ON COLUMN public.hs_production_calendar.start_time
    IS '开始时间';

COMMENT ON COLUMN public.hs_production_calendar.end_time
    IS '结束时间';

-- Trigger: trigger_hs_production_calendar

-- DROP TRIGGER IF EXISTS trigger_hs_production_calendar ON public.hs_production_calendar;

CREATE TRIGGER trigger_hs_production_calendar
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_production_calendar
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_factory();





-- Table: public.hs_production_line

-- DROP TABLE IF EXISTS public.hs_production_line;

CREATE TABLE IF NOT EXISTS public.hs_production_line
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    bg_images character varying(100000) COLLATE pg_catalog."default",
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    created_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    factory_id uuid NOT NULL,
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000000) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    workshop_id uuid NOT NULL,
    additional_info character varying COLLATE pg_catalog."default",
    CONSTRAINT tb_production_line_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_production_line
    OWNER to postgres;

-- Trigger: trigger_hs_production_line

-- DROP TRIGGER IF EXISTS trigger_hs_production_line ON public.hs_production_line;

CREATE TRIGGER trigger_hs_production_line
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_production_line
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_factory();





-- Table: public.hs_statistical_data

-- DROP TABLE IF EXISTS public.hs_statistical_data;

CREATE TABLE IF NOT EXISTS public.hs_statistical_data
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    capacity_added_value character varying(255) COLLATE pg_catalog."default",
    capacity_first_time bigint,
    capacity_first_value character varying(255) COLLATE pg_catalog."default",
    capacity_last_time bigint,
    capacity_value character varying(255) COLLATE pg_catalog."default",
    date date,
    electric_added_value character varying(255) COLLATE pg_catalog."default",
    electric_first_time bigint,
    electric_first_value character varying(255) COLLATE pg_catalog."default",
    electric_last_time bigint,
    electric_value character varying(255) COLLATE pg_catalog."default",
    entity_id uuid,
    gas_added_value character varying(255) COLLATE pg_catalog."default",
    gas_first_time bigint,
    gas_first_value character varying(255) COLLATE pg_catalog."default",
    gas_last_time bigint,
    gas_value character varying(255) COLLATE pg_catalog."default",
    ts bigint,
    water_added_value character varying(255) COLLATE pg_catalog."default",
    water_first_time bigint,
    water_first_value character varying(255) COLLATE pg_catalog."default",
    water_last_time bigint,
    water_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_statistical_data_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_statistical_data
    OWNER to postgres;







 -- Table: public.hs_system_version

 -- DROP TABLE IF EXISTS public.hs_system_version;

 CREATE TABLE IF NOT EXISTS public.hs_system_version
 (
     id uuid NOT NULL,
     created_time bigint NOT NULL,
     comment character varying(255) COLLATE pg_catalog."default",
     created_user uuid,
     publish_time bigint NOT NULL,
     tenant_id uuid NOT NULL,
     updated_time character varying(255) COLLATE pg_catalog."default",
     updated_user uuid,
     version character varying(225) COLLATE pg_catalog."default" NOT NULL DEFAULT '0.0.1'::character varying,
     CONSTRAINT hs_system_version_pkey PRIMARY KEY (id)
 )

 TABLESPACE pg_default;

 ALTER TABLE IF EXISTS public.hs_system_version
     OWNER to postgres;

 COMMENT ON TABLE public.hs_system_version
     IS '系统版本表';

 -- Trigger: trigger_hs_system_version

 -- DROP TRIGGER IF EXISTS trigger_hs_system_version ON public.hs_system_version;

 CREATE TRIGGER trigger_hs_system_version
     AFTER INSERT OR DELETE OR UPDATE
     ON public.hs_system_version
     FOR EACH ROW
     EXECUTE FUNCTION public.notify_global_data_change_by_tenant();







-- Table: public.hs_workshop

-- DROP TABLE IF EXISTS public.hs_workshop;

CREATE TABLE IF NOT EXISTS public.hs_workshop
(
    id uuid NOT NULL,
    created_time bigint NOT NULL,
    bg_images character varying(1000000) COLLATE pg_catalog."default",
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    created_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    factory_id uuid NOT NULL,
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000000) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    additional_info character varying COLLATE pg_catalog."default",
    CONSTRAINT tb_workshop_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_workshop
    OWNER to postgres;

-- Trigger: trigger_hs_workshop

-- DROP TRIGGER IF EXISTS trigger_hs_workshop ON public.hs_workshop;

CREATE TRIGGER trigger_hs_workshop
    AFTER INSERT OR DELETE OR UPDATE
    ON public.hs_workshop
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_factory();




-- Table: public.tb_energy_chart

-- DROP TABLE IF EXISTS public.tb_energy_chart;

CREATE TABLE IF NOT EXISTS public.tb_energy_chart
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    capacity_added_value character varying(255) COLLATE pg_catalog."default",
    capacity_first_time bigint,
    capacity_first_value character varying(255) COLLATE pg_catalog."default",
    capacity_last_time bigint,
    capacity_value character varying(255) COLLATE pg_catalog."default",
    date date,
    electric_added_value character varying(255) COLLATE pg_catalog."default",
    electric_first_time bigint,
    electric_first_value character varying(255) COLLATE pg_catalog."default",
    electric_last_time bigint,
    electric_value character varying(255) COLLATE pg_catalog."default",
    entity_id uuid,
    gas_added_value character varying(255) COLLATE pg_catalog."default",
    gas_first_time bigint,
    gas_first_value character varying(255) COLLATE pg_catalog."default",
    gas_last_time bigint,
    gas_value character varying(255) COLLATE pg_catalog."default",
    ts bigint,
    water_added_value character varying(255) COLLATE pg_catalog."default",
    water_first_time bigint,
    water_first_value character varying(255) COLLATE pg_catalog."default",
    water_last_time bigint,
    water_value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_energy_chart_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_energy_chart
    OWNER to postgres;






-- Table: public.tb_enery_time_gap

-- DROP TABLE IF EXISTS public.tb_enery_time_gap;

CREATE TABLE IF NOT EXISTS public.tb_enery_time_gap
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    entity_id uuid,
    key_name character varying(255) COLLATE pg_catalog."default",
    time_gap bigint,
    ts bigint,
    value character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_enery_time_gap_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_enery_time_gap
    OWNER to postgres;





-- Table: public.tb_menu

-- DROP TABLE IF EXISTS public.tb_menu;

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
    region character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    menu_type character varying(255) COLLATE pg_catalog."default",
    path character varying(255) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    is_button boolean DEFAULT false,
    lang_key character varying(255) COLLATE pg_catalog."default"
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_menu
    OWNER to postgres;

-- Trigger: trigger_tb_menu

-- DROP TRIGGER IF EXISTS trigger_tb_menu ON public.tb_menu;

CREATE TRIGGER trigger_tb_menu
    AFTER INSERT OR DELETE OR UPDATE
    ON public.tb_menu
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_all();





-- Table: public.tb_tenant_menu

-- DROP TABLE IF EXISTS public.tb_tenant_menu;

CREATE TABLE IF NOT EXISTS public.tb_tenant_menu
(
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    sys_menu_id uuid,
    sys_menu_code character varying(255) COLLATE pg_catalog."default",
    sys_menu_name character varying(255) COLLATE pg_catalog."default",
    tenant_menu_name character varying(255) COLLATE pg_catalog."default",
    tenant_menu_code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    level bigint NOT NULL,
    sort bigint NOT NULL,
    url character varying(1000) COLLATE pg_catalog."default",
    parent_id uuid,
    tenant_menu_icon character varying(255) COLLATE pg_catalog."default",
    tenant_menu_images character varying(1000) COLLATE pg_catalog."default",
    region character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    menu_type character varying(255) COLLATE pg_catalog."default",
    path character varying(255) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    is_button boolean DEFAULT false,
    lang_key character varying(255) COLLATE pg_catalog."default",
    has_children boolean DEFAULT false,
    CONSTRAINT tb_tenant_menu_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_tenant_menu
    OWNER to postgres;

-- Trigger: trigger_tb_tenant_menu

-- DROP TRIGGER IF EXISTS trigger_tb_tenant_menu ON public.tb_tenant_menu;

CREATE TRIGGER trigger_tb_tenant_menu
    AFTER INSERT OR DELETE OR UPDATE
    ON public.tb_tenant_menu
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();







-- Table: public.tb_tenant_menu_role

-- DROP TABLE IF EXISTS public.tb_tenant_menu_role;

CREATE TABLE IF NOT EXISTS public.tb_tenant_menu_role
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    remark character varying(255) COLLATE pg_catalog."default",
    tenant_menu_id uuid,
    tenant_sys_role_id uuid,
    flg character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_tenant_menu_role_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_tenant_menu_role
    OWNER to postgres;

-- Trigger: trigger_tb_tenant_menu_role

-- DROP TRIGGER IF EXISTS trigger_tb_tenant_menu_role ON public.tb_tenant_menu_role;

CREATE TRIGGER trigger_tb_tenant_menu_role
    AFTER INSERT OR DELETE OR UPDATE
    ON public.tb_tenant_menu_role
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();






-- Table: public.tb_tenant_sys_role

-- DROP TABLE IF EXISTS public.tb_tenant_sys_role;

CREATE TABLE IF NOT EXISTS public.tb_tenant_sys_role
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    role_code character varying(255) COLLATE pg_catalog."default",
    role_desc character varying(255) COLLATE pg_catalog."default",
    role_name character varying(255) COLLATE pg_catalog."default",
    factory_id uuid,
    system_tab character varying(255) COLLATE pg_catalog."default",
    type character varying(255) COLLATE pg_catalog."default",
    operation_type integer DEFAULT 0,
    user_level integer DEFAULT 0,
    CONSTRAINT tb_tenant_sys_role_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_tenant_sys_role
    OWNER to postgres;

-- Trigger: trigger_tb_tenant_sys_role

-- DROP TRIGGER IF EXISTS trigger_tb_tenant_sys_role ON public.tb_tenant_sys_role;

CREATE TRIGGER trigger_tb_tenant_sys_role
    AFTER INSERT OR DELETE OR UPDATE
    ON public.tb_tenant_sys_role
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();








-- Table: public.tb_user_menu_role

-- DROP TABLE IF EXISTS public.tb_user_menu_role;

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

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_user_menu_role
    OWNER to postgres;

-- Trigger: trigger_tb_user_menu_role

-- DROP TRIGGER IF EXISTS trigger_tb_user_menu_role ON public.tb_user_menu_role;

CREATE TRIGGER trigger_tb_user_menu_role
    AFTER INSERT OR DELETE OR UPDATE
    ON public.tb_user_menu_role
    FOR EACH ROW
    EXECUTE FUNCTION public.notify_global_data_change_by_tenant();






































