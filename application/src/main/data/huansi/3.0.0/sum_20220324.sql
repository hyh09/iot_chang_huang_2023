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
    sort bigint,
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
    is_done bool NOT NULL DEFAULT false,
    sort bigint,
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






------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------thingsboard原生表的扩展--------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--设备表新增字段
ALTER TABLE public.device ADD COLUMN production_line_id uuid;
ALTER TABLE public.device ADD COLUMN workshop_id uuid;
ALTER TABLE public.device ADD COLUMN factory_id uuid;
ALTER TABLE public.device ADD COLUMN code character varying(1000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN picture character varying(1000000) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN icon character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.device ADD COLUMN dict_device_id uuid;
ALTER TABLE public.device ADD COLUMN created_user uuid;
ALTER TABLE public.device ADD COLUMN updated_time bigint;
ALTER TABLE public.device ADD COLUMN updated_user uuid;
ALTER TABLE public.device ADD COLUMN comment character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.comment IS '备注';
ALTER TABLE public.device ADD COLUMN device_no character varying(255) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.device.device_no IS '设备编号';
alter table device add flg boolean default false;
alter table device add sort bigint;




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
ALTER TABLE public.tb_user
    ADD COLUMN type character varying(255) COLLATE pg_catalog."default";
ALTER TABLE public.tb_user
    ADD COLUMN factory_id uuid;
ALTER TABLE tb_user
    ADD COLUMN user_level integer DEFAULT 0;
ALTER TABLE tb_user
    ADD COLUMN operation_type integer DEFAULT 0;


-----租户表
alter table tenant add county_level varchar(255);
alter table tenant add longitude varchar(255);
alter table tenant add latitude varchar(255);










--设备字典属性初始数据
INSERT INTO "public"."hs_init"("id", "init_data", "scope", "created_time", "created_user", "updated_time", "updated_user") VALUES ('a6bcd176-7538-402c-9035-5b966888faa0', '[{"id": null, "name": "能耗", "groupPropertyList": [{"id": null, "name": "water", "unit": "T", "title": "耗水量", "content": "0", "createdTime": null}, {"id": null, "name": "electric", "unit": "KWH", "title": "耗电量", "content": "0", "createdTime": null}, {"id": null, "name": "gas", "unit": "T", "title": "耗气量", "content": "0", "createdTime": null}]}, {"id": null, "name": "产能", "groupPropertyList": [{"id": null, "name": "capacities", "unit": "", "title": "总产能", "content": "0", "createdTime": null}]}]', 'DICT_DEVICE_GROUP', 1636522070426, '07b770d0-3bb3-11ec-ad5a-9bec5deb66b9', 1636522070426, '07b770d0-3bb3-11ec-ad5a-9bec5deb66b9');






---初始化系统菜单
INSERT INTO "public"."tb_menu" ("id", "code", "name", "level", "sort", "url", "parent_id", "menu_icon", "menu_images", "region", "created_time", "created_user", "updated_time", "updated_user", "menu_type", "path", "is_button", "lang_key") VALUES
('570d8ce0-4906-11ec-b2c3-7f574d105067', 'XTCD1637305212069', '边缘实例', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'router', NULL, 'Global', 1637305212099, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305212099', NULL, 'PC', '/edgeInstances', 'f', 'edge.edge-instances'),
('376a56b0-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163127194', '设备字典 - 删除', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636163127196, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163127196', NULL, 'PC', NULL, 't', 'action.delete'),
('67e660c0-3ea5-11ec-9090-a3316146e5ad', 'XTCD1636164067530', '数据字典 - 编辑', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636164067592, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636164067592', NULL, 'PC', NULL, 't', 'action.edit'),
('d171c040-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636436116013', '角色管理 - 关联用户', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436116048, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436116048', NULL, 'PC', NULL, 't', 'auth-mng.bind-users'),
('eccb2a40-3d48-11ec-9809-df813b08b61b', 'XTCD1636014396130', '设备管理', 0, 1, NULL, NULL, 'devices', NULL, 'Global', 1636014396134, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636014396134', NULL, 'PC', '/deviceManagement', 'f', 'device-mng.device-mng'),
('976c8770-3e0a-11ec-866a-059202e55853', 'XTCD1636097575270', '权限管理', 0, 1, NULL, NULL, 'verified_user', NULL, 'Global', 1636097575272, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097575272', NULL, 'PC', '/authManagement', 'f', 'auth-mng.auth-mng'),
('aa2af5e0-3e0a-11ec-866a-059202e55853', 'XTCD1636097606717', '用户管理', 1, 1, NULL, '976c8770-3e0a-11ec-866a-059202e55853', 'people', NULL, 'Global', 1636097606719, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097606719', NULL, 'PC', '/authManagement/userManagement', 'f', 'auth-mng.user-mng'),
('c84605b0-3e0a-11ec-866a-059202e55853', 'XTCD1636097657227', '角色管理', 1, 1, NULL, '976c8770-3e0a-11ec-866a-059202e55853', 'mdi:shield-account', NULL, 'Global', 1636097657228, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636097657228', NULL, 'PC', '/authManagement/roleManagemnet', 'f', 'auth-mng.role-mng'),
('f74d38e0-3ea2-11ec-80ce-5546796a1b1d', 'XTCD1636163019626', '数据字典 - 添加数据字典', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636163019637, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163019637', NULL, 'PC', NULL, 't', 'device-mng.add-data-dic'),
('07a2e4b0-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163047034', '数据字典 - 删除', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1636163047037, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163047037', NULL, 'PC', NULL, 't', 'action.delete'),
('27e0fb90-3ea3-11ec-80ce-5546796a1b1d', 'XTCD1636163101127', '设备字典 - 添加设备字典', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636163101130, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636163101130', NULL, 'PC', NULL, 't', 'device-mng.add-device-dic'),
('07025df0-411f-11ec-8ed3-dda66ae3bde5', 'XTCD1636436205885', '角色管理 - 配置权限', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436205920, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436205920', NULL, 'PC', NULL, 't', 'auth-mng.set-permissions'),
('0486cc10-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435772225', '用户管理 - 删除', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435772247, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435772247', NULL, 'PC', NULL, 't', 'action.delete'),
('eed8ded0-411d-11ec-8ed3-dda66ae3bde5', 'XTCD1636435735851', '用户管理 - 添加用户', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435790262, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435790262', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', NULL, 't', 'user.add'),
('279d0930-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435831090', '用户管理 - 编辑', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435831116, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435831116', NULL, 'PC', NULL, 't', 'action.edit'),
('3a985990-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435862929', '用户管理 - 修改密码', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435862964, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435862964', NULL, 'PC', NULL, 't', 'auth-mng.change-pwd'),
('52b523f0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435903390', '角色管理 - 添加角色', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435903417, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435903417', NULL, 'PC', NULL, 't', 'auth-mng.add-role'),
('630885d0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636435930784', '角色管理 - 删除', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636435930807, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636435930807', NULL, 'PC', NULL, 't', 'action.delete'),
('9e599ca0-411e-11ec-8ed3-dda66ae3bde5', 'XTCD1636436030296', '角色管理 - 编辑', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1636436030323, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636436030323', NULL, 'PC', NULL, 't', 'action.edit'),
('4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', 'XTCD1636442768781', '工厂管理', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:factory-mng', NULL, 'Global', 1636442768841, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636442768841', NULL, 'PC', '/deviceManagement/factoryManagement', 'f', 'device-mng.factory-mng'),
('ffae2640-435f-11ec-956d-c10484bf3370', 'XTCD1636684013211', '实时监控', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684013303, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684013303', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('08b713a0-4360-11ec-956d-c10484bf3370', 'XTCD1636684028377', '报警记录', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684028379, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684028379', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('11692e70-4360-11ec-956d-c10484bf3370', 'XTCD1636684042965', '产能分析', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684042968, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684042968', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('1b5c2db0-4360-11ec-956d-c10484bf3370', 'XTCD1636684059658', '能耗分析', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684059660, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684059660', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('23cc0f60-4360-11ec-956d-c10484bf3370', 'XTCD1636684073813', '运行状态', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1636684073815, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1636684073815', NULL, 'APP', NULL, 'f', 'auth-mng.set-permissions'),
('80cda600-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058321502', '工厂管理 - 编辑', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058321505, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058321505', NULL, 'PC', NULL, 't', 'action.edit'),
('a0bf71a0-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058375095', '工厂管理 - 分配设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1637058375101, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637058375101', NULL, 'PC', NULL, 't', 'device-mng.distribute-device'),
('26607400-46d0-11ec-9d4c-d5cce199ce69', 'XTCD1637062035259', '报警记录 - 确认', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637062035282, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637062035282', NULL, 'PC', NULL, 't', 'action.confirm'),
('50f506e0-46d0-11ec-9d4c-d5cce199ce69', 'XTCD1637062106681', '报警记录 - 清除', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637062106744, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637062106744', NULL, 'PC', NULL, 't', 'alarm.clear'),
('77c83c20-3ea5-11ec-9090-a3316146e5ad', 'XTCD1636164094175', '设备字典 - 编辑', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1637065134887, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637065134887', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', NULL, 't', 'action.edit'),
('763c6cb0-4750-11ec-bee8-51118663de80', 'XTCD1637117144794', '报警规则', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:alarm-rules', NULL, 'Global', 1637117546745, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637117546745', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/alarmRules', 'f', 'device-monitor.alarm-rules'),
('32db2320-3e0a-11ec-866a-059202e55853', 'XTCD1636097406545', '报警记录', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:alarm-records', NULL, 'Global', 1637118082797, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118082797', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/alarmRecord', 'f', 'device-monitor.alarm-record'),
('10fc7ba0-3e0a-11ec-866a-059202e55853', 'XTCD1636097349722', '实时监控', 1, 1, NULL, 'f7e301c0-3e09-11ec-866a-059202e55853', 'mdi:real-time-monitor', NULL, 'Global', 1637118568386, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118568386', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor/realTimeMonitor', 'f', 'device-monitor.real-time-monitor'),
('f7e301c0-3e09-11ec-866a-059202e55853', 'XTCD1636097307611', '设备监控', 0, 1, NULL, NULL, 'mdi:device-monitor', NULL, 'Global', 1637118819789, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1637118819789', 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', 'PC', '/deviceMonitor', 'f', 'device-monitor.device-monitor'),
('f28628c0-4907-11ec-b2c3-7f574d105067', 'XTCD1637305902402', '部件库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'now_widgets', NULL, 'Global', 1637305902429, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637305902429', NULL, 'PC', '/widgets-bundles', 'f', 'widget.widget-library'),
('3e433280-4908-11ec-b2c3-7f574d105067', 'XTCD1637306029473', '仪表板库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'dashboards', NULL, 'Global', 1637306029495, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306029495', NULL, 'PC', '/dashboards', 'f', 'dashboard.dashboards'),
('a67f2840-4908-11ec-a773-27e2f8b15961', 'XTCD1637306204338', '审计日志', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'track_changes', NULL, 'Global', 1637306204483, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306204483', NULL, 'PC', '/auditLogs', 'f', 'audit-log.audit-logs'),
('3abb75e0-4909-11ec-a773-27e2f8b15961', 'XTCD1637306453045', 'Api使用统计', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'insert_chart', NULL, 'Global', 1637306453066, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637306453066', NULL, 'PC', '/usage', 'f', 'api-usage.api-usage'),
('f3fe94a0-481a-11ec-931b-a78b7aaa97d6', 'XTCD1637204114129', '设备', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'devices_other', NULL, 'Global', 1637204114165, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637204114165', NULL, 'PC', '/devices', 'f', 'device.devices'),
('e23f9d60-4831-11ec-931b-a78b7aaa97d6', 'XTCD1637213962800', '设备配置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'mdi:alpha-d-box', NULL, 'Global', 1637213962817, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637213962817', NULL, 'PC', '/deviceProfiles', 'f', 'device-profile.device-profiles'),
('39c6c030-490a-11ec-a773-27e2f8b15961', 'XTCD1637306880936', '资源库', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'folder', NULL, 'Global', 1637315730975, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315730975', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/resources-library', 'f', 'resource.resources-library'),
('0d3114e0-4904-11ec-b2c3-7f574d105067', 'XTCD1637304229158', '规则链', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_ethernet', NULL, 'Global', 1637304407484, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304407484', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/ruleChains', 'f', 'rulechain.rulechains'),
('61d700e0-4904-11ec-b2c3-7f574d105067', 'XTCD1637304371176', '资产', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'domain', NULL, 'Global', 1637304416874, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304416874', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/assets', 'f', 'asset.assets'),
('4f6af830-4904-11ec-b2c3-7f574d105067', 'XTCD1637304340266', '客户', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'supervisor_account', NULL, 'Global', 1637304432742, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304432742', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/customers', 'f', 'customer.customers'),
('db9bb1f0-4909-11ec-a773-27e2f8b15961', 'XTCD1637306722953', '首页设置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_applications', NULL, 'Global', 1637315752750, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315752750', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/home', 'f', 'admin.home-settings'),
('94eb8590-4905-11ec-b2c3-7f574d105067', 'XTCD1637304886368', '客户实体视图', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'view_quilt', NULL, 'Global', 1637304886386, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304886386', NULL, 'PC', '/entityViews', 'f', 'entity-view.entity-views'),
('49039b40-4905-11ec-b2c3-7f574d105067', 'XTCD1637304759019', 'OAT更新', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'memory', NULL, 'Global', 1637304904392, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637304904392', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/otaUpdates', 'f', 'ota-update.ota-updates'),
('173b0fd0-490a-11ec-a773-27e2f8b15961', 'XTCD1637306822980', '主题设置', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'color_lens', NULL, 'Global', 1637315819609, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637315819609', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', '/settings/custom-ui', 'f', 'system-settings.custom-ui'),
('1ee3f690-4c00-11ec-ac73-c546a4193fa0', 'XTCD1637632394354', '产能分析', 1, 1, NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:capacity', NULL, 'Global', 1637632394367, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637632394367', NULL, 'PC', '/potency/deviceCapacity', 'f', 'potency.device-capacity'),
('42462640-4bff-11ec-ac73-c546a4193fa0', 'XTCD1637632024202', '效能分析', 0, 1, NULL, NULL, 'mdi:potency', NULL, 'Global', 1637632332123, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637632332123', '5a797660-4612-11e7-a919-92ebcb67fe33', 'PC', 'potency', 'f', 'potency.potency'),
('be0754b0-4c32-11ec-a459-159d9ed885bc', 'XTCD1637654136176', '运行状态', 1, 1, NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:running-state', NULL, 'Global', 1637654136624, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637654136624', NULL, 'PC', '/potency/runningState', 'f', 'potency.running-state'),
('2c1c9600-4c23-11ec-b550-4f9d96b0fc51', 'XTCD1637647448914', '能耗分析', 1, 1, NULL, '42462640-4bff-11ec-ac73-c546a4193fa0', 'mdi:energy', NULL, 'Global', 1637647452108, '5a797660-4612-11e7-a919-92ebcb67fe33', '1637647452108', NULL, 'PC', '/potency/energyConsumption', 'f', 'potency.energy-consumption'),
('4cea95a0-531a-11ec-8f0d-e7a9f5cb74f8', 'XTCD1638413296623', '工厂管理 - 管理工厂管理员', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1638413297057, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638413297057', NULL, 'PC', NULL, 't', 'device-mng.manage-factory-manager'),
('74357bc0-531a-11ec-8f0d-e7a9f5cb74f8', 'XTCD1638413362546', '工厂管理 - 配置管理员权限', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1638413362567, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638413362567', NULL, 'PC', NULL, 't', 'device-mng.set-factory-manager-permissions'),
('5c082880-4907-11ec-b2c3-7f574d105067', 'XTCD1637305649919', '规则链模板', 1, 1, NULL, '9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'settings_ethernet', NULL, 'Global', 1638772682272, '5a797660-4612-11e7-a919-92ebcb67fe33', '1638772682272', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/edgeManagement/ruleChains', 'f', 'edge.rulechain-templates'),
('9e1b46a0-481a-11ec-931b-a78b7aaa97d6', 'XTCD1637203970039', '平台管理', 0, 1, NULL, NULL, 'mdi:platform-mng', NULL, 'Global', 1638777266756, '5a797660-4612-11e7-a919-92ebcb67fe33', '1638777266756', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 'f', 'platform.platform-mng'),
('d9a70800-5669-11ec-bdfd-b18997b56dbf', 'XTCD1638777316463', '系统管理', 0, 1, NULL, NULL, 'settings', NULL, 'Global', 1638777316553, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638777316553', NULL, 'PC', NULL, 'f', 'system-mng.system-mng'),
('18245880-566a-11ec-bdfd-b18997b56dbf', 'XTCD1638777421305', '工厂软件版本', 1, 1, NULL, 'd9a70800-5669-11ec-bdfd-b18997b56dbf', 'mdi:software', NULL, 'Global', 1638777904214, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638777904214', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/systemManagement/factoryVersion', 'f', 'system-mng.factory-version'),
('ca940330-570a-11ec-bc52-37d90f432ffa', 'XTCD1638846440141', '产能运算配置', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:switch-config', NULL, 'Global', 1638846800395, '265dd610-40a8-11ec-afb6-7fee9c405457', '1638846800395', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/productionCapacitySettings', 'f', 'device-mng.prod-capactity-settings'),
('2df216f0-3d49-11ec-9809-df813b08b61b', 'XTCD1636014505438', '数据字典', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:data-dictionary', NULL, 'Global', 1638847276351, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1638847276351', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/dataDictionary', 'f', 'device-mng.data-dic'),
('c3b93c80-3e08-11ec-866a-059202e55853', 'XTCD1636096790591', '设备字典', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:device-dictionary', NULL, 'Global', 1638847308338, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1638847308338', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/deviceDictionary', 'f', 'device-mng.device-dic'),
('5374c300-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058245420', '工厂管理 - 添加工厂', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041038760, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1639041038760', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'device-mng.add-factory'),
('f2e2ff00-58cf-11ec-b7c6-c9d29ed15db6', 'XTCD1639041069806', '工厂管理 - 添加车间', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041069809, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639041069809', NULL, 'PC', NULL, 't', 'device-mng.add-work-shop'),
('04d284b0-58d0-11ec-b7c6-c9d29ed15db6', 'XTCD1639041099899', '工厂管理 - 添加产线', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041099900, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639041099900', NULL, 'PC', NULL, 't', 'device-mng.add-prod-line'),
('26e163f0-58d0-11ec-b7c6-c9d29ed15db6', 'XTCD1639041157039', '工厂管理 - 添加设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1639041157040, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639041157040', NULL, 'PC', NULL, 't', 'device-mng.add-device'),
('e4452c30-595a-11ec-b7c6-c9d29ed15db6', 'XTCD1639100745330', '系统版本', 1, 1, NULL, 'd9a70800-5669-11ec-bdfd-b18997b56dbf', 'mdi:version', NULL, 'Global', 1639123254701, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639123254701', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/systemManagement/systemVersion', 'f', 'system-mng.system-version'),
('23d4f2c0-5bfb-11ec-b3d1-453d9782c4b2', 'XTCD1639389473769', '设备字典 - 配置下发', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1639389473787, '265dd610-40a8-11ec-afb6-7fee9c405457', '1639389473787', NULL, 'PC', NULL, 't', 'device-mng.distribut-config'),
('6d9dff30-46c7-11ec-9d4c-d5cce199ce69', 'XTCD1637058289313', '工厂管理 - 删除工厂', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050431742, 'fc4b0e50-0c5a-11ec-bdcf-83a250730c01', '1640050431742', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'device-mng.delete-factory'),
('1d8055a0-61fe-11ec-8332-7b8df1e64f2d', 'XTCD1640050458615', '工厂管理 - 删除车间', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050458619, '265dd610-40a8-11ec-afb6-7fee9c405457', '1640050458619', NULL, 'PC', NULL, 't', 'device-mng.delete-work-shop'),
('2f16e5e0-61fe-11ec-8332-7b8df1e64f2d', 'XTCD1640050488125', '工厂管理 - 删除产线', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050488127, '265dd610-40a8-11ec-afb6-7fee9c405457', '1640050488127', NULL, 'PC', NULL, 't', 'device-mng.delete-prod-line'),
('43a07760-61fe-11ec-8332-7b8df1e64f2d', 'XTCD1640050522581', '工厂管理 - 删除设备', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1640050522582, '265dd610-40a8-11ec-afb6-7fee9c405457', '1640050522582', NULL, 'PC', NULL, 't', 'device-mng.delete-device'),
('4bb5f1a0-9dd6-11ec-a79b-07ce2c030081', 'XTCD1646630426041', '生产管理 - 管理日历', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1647833476427, '265dd610-40a8-11ec-afb6-7fee9c405457', '1647833476427', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'device-mng.mng-calendars'),
('2e0c8f60-9dd6-11ec-9386-9d079fb5289f', 'XTCD1646630376277', '生产管理', 0, 1, NULL, NULL, 'mdi:prod-mng', NULL, 'Global', 1647833556149, '265dd610-40a8-11ec-afb6-7fee9c405457', '1647833556149', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/prodManagement', 'f', 'device-mng.prod-mng'),
('1346b330-6eb1-11ec-9772-d79aa7bd0c88', 'XTCD1641446735331', '角色管理 - 查看', 1, 1, NULL, 'c84605b0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448524178, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448524178', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('df6720e0-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446648301', '用户管理 - 查看', 1, 1, NULL, 'aa2af5e0-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448539990, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448539990', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('b680da40-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446579683', '报警规则 - 查看', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641448560803, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448560803', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('ab0b80c0-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446560460', '报警记录 - 查看', 1, 1, NULL, '32db2320-3e0a-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448578095, '825fafb0-694d-11ec-b4a8-134013dcacfd', '1641448578095', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('719ce590-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446464104', '工厂管理 - 查看', 1, 1, NULL, '4ecf6b00-412e-11ec-8ed3-dda66ae3bde5', NULL, NULL, 'Global', 1641448590223, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641448590223', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('459561c0-6eb0-11ec-9772-d79aa7bd0c88', 'XTCD1641446390236', '设备字典 - 查看', 1, 1, NULL, 'c3b93c80-3e08-11ec-866a-059202e55853', NULL, NULL, 'Global', 1641448603623, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641448603623', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('e95bc340-6eaf-11ec-9772-d79aa7bd0c88', 'XTCD1641446235507', '数据字典 - 查看', 1, 1, NULL, '2df216f0-3d49-11ec-9809-df813b08b61b', NULL, NULL, 'Global', 1641448615681, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641448615681', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', NULL),
('92c431b0-6eb5-11ec-9772-d79aa7bd0c88', 'XTCD1641448667211', '报警规则 - 添加', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641449905099, '50570d90-6eae-11ec-9772-d79aa7bd0c88', '1641449905099', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', 'device-monitor.add-alarm-rule'),
('dfd5f601-6eb5-11ec-9772-d79aa7bd0c88', 'XTCD1641448796512', '报警规则 - 删除', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641449935238, '50570d90-6eae-11ec-9772-d79aa7bd0c88', '1641449935238', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', 'action.delete'),
('b0c9e4c0-6eb5-11ec-9772-d79aa7bd0c88', 'XTCD1641448717579', '报警规则 - 编辑', 1, 1, NULL, '763c6cb0-4750-11ec-bee8-51118663de80', NULL, NULL, 'Global', 1641449956341, '50570d90-6eae-11ec-9772-d79aa7bd0c88', '1641449956341', '50570d90-6eae-11ec-9772-d79aa7bd0c88', 'PC', NULL, 't', 'action.edit'),
('34ca7f50-71b8-11ec-b62f-bd99beea2ad2', 'XTCD1641779651509', '订单列表 - 删除', 1, 1, NULL, '2e5837d0-71b7-11ec-b62f-bd99beea2ad2', NULL, NULL, 'Global', 1641779651544, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779651544', NULL, 'PC', NULL, 't', 'action.delete'),
('f0b7bd50-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779537308', '订单列表 - 编辑', 1, 1, NULL, '2e5837d0-71b7-11ec-b62f-bd99beea2ad2', NULL, NULL, 'Global', 1641779537328, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779537328', NULL, 'PC', NULL, 't', 'action.edit'),
('de594930-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779506484', '订单列表 - 添加订单', 1, 1, NULL, '2e5837d0-71b7-11ec-b62f-bd99beea2ad2', NULL, NULL, 'Global', 1641779506516, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779506516', NULL, 'PC', NULL, 't', 'order.add-order'),
('badcd030-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779446942', '订单管理 - 订单产能监控', 1, 1, NULL, 'd296d690-71b6-11ec-b62f-bd99beea2ad2', 'mdi:order-capacity', NULL, 'Global', 1641779446985, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779446985', NULL, 'PC', '/orderFormManagement/orderCapacity', 'f', 'order.order-capacity'),
('2e5837d0-71b7-11ec-b62f-bd99beea2ad2', 'XTCD1641779211199', '订单管理 - 订单列表', 1, 1, NULL, 'd296d690-71b6-11ec-b62f-bd99beea2ad2', 'mdi:order', NULL, 'Global', 1641779211233, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779211233', NULL, 'PC', '/orderFormManagement/orders', 'f', 'order.orders'),
('d296d690-71b6-11ec-b62f-bd99beea2ad2', 'XTCD1641779057244', '订单管理', 0, 1, NULL, NULL, 'mdi:order-mng', NULL, 'Global', 1641779091264, '265dd610-40a8-11ec-afb6-7fee9c405457', '1641779091264', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 'f', 'order.order-form-mng'),
('13796970-7437-11ec-b80f-7b335e68957e', 'XTCD1642054044037', '订单管理', 0, 1, NULL, NULL, NULL, NULL, 'Global', 1642154780041, '265dd610-40a8-11ec-afb6-7fee9c405457', '1642154780041', '265dd610-40a8-11ec-afb6-7fee9c405457', 'APP', NULL, 'f', NULL),
('9aa35850-9ba8-11ec-96ee-613d9bcc5d10', 'XTCD1646390899284', '数据关联', 1, 1, NULL, 'eccb2a40-3d48-11ec-9809-df813b08b61b', 'mdi:chart-timeline-variant', NULL, 'Global', 1646624291529, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624291529', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', '/deviceManagement/chartSettings', 'f', 'device-mng.chart-settings'),
('e3017fa0-9ba8-11ec-8347-6366c0cb66a6', 'XTCD1646391020697', '数据关联 - 删除', 1, 1, NULL, '9aa35850-9ba8-11ec-96ee-613d9bcc5d10', NULL, NULL, 'Global', 1646624299556, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624299556', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'action.delete'),
('cf171450-9ba8-11ec-96ee-613d9bcc5d10', 'XTCD1646390987284', '数据关联 - 编辑', 1, 1, NULL, '9aa35850-9ba8-11ec-96ee-613d9bcc5d10', NULL, NULL, 'Global', 1646624306969, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624306969', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'action.edit'),
('b831b880-9ba8-11ec-8347-6366c0cb66a6', 'XTCD1646390948871', '数据关联 - 添加', 1, 1, NULL, '9aa35850-9ba8-11ec-96ee-613d9bcc5d10', NULL, NULL, 'Global', 1646624315774, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646624315774', '265dd610-40a8-11ec-afb6-7fee9c405457', 'PC', NULL, 't', 'action.add'),
('60b4b320-9dd6-11ec-9386-9d079fb5289f', 'XTCD1646630461266', '生产管理 - 管理日历 - 添加', 1, 1, NULL, '2e0c8f60-9dd6-11ec-9386-9d079fb5289f', NULL, NULL, 'Global', 1646630461267, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646630461267', NULL, 'PC', NULL, 't', 'action.add'),
('74608ed0-9dd6-11ec-a79b-07ce2c030081', 'XTCD1646630494268', '生产管理 - 管理日历 - 编辑', 1, 1, NULL, '2e0c8f60-9dd6-11ec-9386-9d079fb5289f', NULL, NULL, 'Global', 1646630494269, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646630494269', NULL, 'PC', NULL, 't', 'action.edit'),
('89e52220-9dd6-11ec-9386-9d079fb5289f', 'XTCD1646630530370', '生产管理 - 管理日历 - 删除', 1, 1, NULL, '2e0c8f60-9dd6-11ec-9386-9d079fb5289f', NULL, NULL, 'Global', 1646630530371, '265dd610-40a8-11ec-afb6-7fee9c405457', '1646630530371', NULL, 'PC', NULL, 't', 'action.delete');
























