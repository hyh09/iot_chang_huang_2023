
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



--- Table: public.hs_dict_data

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
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_data.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_data.code
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.name
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.type
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.unit
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.comment
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_data.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_data.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_data.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_data.icon
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.picture
    IS '??????';

COMMENT ON COLUMN public.hs_dict_data.tenant_id
    IS '??????Id';



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
    CONSTRAINT dict_device_pkey PRIMARY KEY (id),
    CONSTRAINT uk_code_and_tenant_id_2 UNIQUE (code, tenant_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device.code
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.name
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.type
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.supplier
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device.model
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.version
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device.warranty_period
    IS '?????????(???)';

COMMENT ON COLUMN public.hs_dict_device.picture
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.icon
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device.tenant_id
    IS '??????Id';

COMMENT ON COLUMN public.hs_dict_device.comment
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device.is_default
    IS '????????????';




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
    IS '????????????-??????';

COMMENT ON COLUMN public.hs_dict_device_component.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_component.code
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.name
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.type
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.supplier
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_component.model
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.version
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_component.warranty_period
    IS '?????????(???)';

COMMENT ON COLUMN public.hs_dict_device_component.picture
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.parent_id
    IS '???Id';

COMMENT ON COLUMN public.hs_dict_device_component.dict_device_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_component.icon
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_component.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_component.comment
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.sort
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component.key
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component.dict_data_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_component.content
    IS '??????';
-- Index: idx_dict_device_id_2

-- DROP INDEX IF EXISTS public.idx_dict_device_id_2;

CREATE INDEX IF NOT EXISTS idx_dict_device_id_2
    ON public.hs_dict_device_component USING btree
    (dict_device_id ASC NULLS LAST)
    TABLESPACE pg_default;




-- Table: public.hs_dict_device_component_property

-- DROP TABLE IF EXISTS public.hs_dict_device_component_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_component_property
(
    id uuid NOT NULL,
    component_id uuid,
    content character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    title character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    dict_data_id uuid,
    CONSTRAINT hs_dict_device_component_property_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_component_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_component_property
    IS '????????????-????????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.component_id
    IS '??????Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.content
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component_property.name
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.dict_device_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_component_property.title
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_component_property.sort
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_component_property.dict_data_id
    IS '????????????Id';





-- Table: public.hs_dict_device_group

-- DROP TABLE IF EXISTS public.hs_dict_device_group;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_group
(
    id uuid NOT NULL,
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    CONSTRAINT dict_device_group_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_group
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_group
    IS '????????????-??????';

COMMENT ON COLUMN public.hs_dict_device_group.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_group.dict_device_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_group.name
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_group.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_group.sort
    IS '????????????';




-- Table: public.hs_dict_device_group_property

-- DROP TABLE IF EXISTS public.hs_dict_device_group_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_group_property
(
    id uuid NOT NULL,
    dict_device_group_id uuid,
    content character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    title character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    dict_data_id uuid,
    CONSTRAINT hs_dict_device_group_property_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_group_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_group_property
    IS '????????????-????????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.dict_device_group_id
    IS '??????????????????Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.content
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_group_property.name
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.dict_device_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_group_property.title
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_group_property.sort
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_group_property.dict_data_id
    IS '????????????Id';


-- Table: public.hs_dict_device_property

-- DROP TABLE IF EXISTS public.hs_dict_device_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_property
(
    id uuid NOT NULL,
    dict_device_id uuid,
    name character varying(255) COLLATE pg_catalog."default",
    content character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    CONSTRAINT "dict_device_ property_pkey" PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_property
    IS '????????????-??????';

COMMENT ON COLUMN public.hs_dict_device_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_property.dict_device_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_property.name
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_property.content
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_property.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_property.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_property.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_property.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_property.sort
    IS '????????????';
-- Index: idx_dict_device_id

-- DROP INDEX IF EXISTS public.idx_dict_device_id;

CREATE INDEX IF NOT EXISTS idx_dict_device_id
    ON public.hs_dict_device_property USING btree
    (dict_device_id ASC NULLS LAST)
    TABLESPACE pg_default;



-- Table: public.hs_dict_device_standard_property

-- DROP TABLE IF EXISTS public.hs_dict_device_standard_property;

CREATE TABLE IF NOT EXISTS public.hs_dict_device_standard_property
(
    id uuid NOT NULL,
    content character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    created_time bigint NOT NULL,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    dict_device_id uuid,
    title character varying(255) COLLATE pg_catalog."default",
    sort bigint,
    dict_data_id uuid,
    CONSTRAINT hs_dict_device_group_property_copy1_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_dict_device_standard_property
    OWNER to postgres;

COMMENT ON TABLE public.hs_dict_device_standard_property
    IS '????????????-????????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.id
    IS 'Id';

COMMENT ON COLUMN public.hs_dict_device_standard_property.content
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.name
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.updated_user
    IS '?????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.dict_device_id
    IS '????????????Id';

COMMENT ON COLUMN public.hs_dict_device_standard_property.title
    IS '??????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.sort
    IS '????????????';

COMMENT ON COLUMN public.hs_dict_device_standard_property.dict_data_id
    IS '????????????Id';






-- Table: public.hs_factory

-- DROP TABLE IF EXISTS public.hs_factory;

CREATE TABLE IF NOT EXISTS public.hs_factory
(
    id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000000) COLLATE pg_catalog."default",
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
    province character varying(1000) COLLATE pg_catalog."default",
    city character varying(1000) COLLATE pg_catalog."default",
    area character varying(1000) COLLATE pg_catalog."default",
    country character varying(255) COLLATE pg_catalog."default",
    additional_info character varying COLLATE pg_catalog."default",
    CONSTRAINT tb_factory_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_factory
    OWNER to postgres;

COMMENT ON COLUMN public.hs_factory.province
    IS '???';

COMMENT ON COLUMN public.hs_factory.city
    IS '???';

COMMENT ON COLUMN public.hs_factory.area
    IS '???';




-- Table Definition
CREATE TABLE IF NOT EXISTS "public"."hs_file" (
  "id" uuid NOT NULL,
  "created_time" int8 NOT NULL,
  "created_user" varchar(255),
    "updated_time" int8,
    "updated_user" varchar(255),
    "tenant_id" uuid,
    "file_name" varchar(255),
    "check_sum" varchar(5000),
    "content_type" varchar(255),
    "checksum_algorithm" varchar(32),
    "data_size" int8,
    "additional_info" varchar,
    "scope" varchar(255),
    "entity_id" uuid,
    "location" varchar(1000),
    PRIMARY KEY ("id")
    );

-- Column Comment
COMMENT ON COLUMN "public"."hs_file"."id" IS 'id';
COMMENT ON COLUMN "public"."hs_file"."created_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_file"."created_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."updated_time" IS '????????????';
COMMENT ON COLUMN "public"."hs_file"."updated_user" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."tenant_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_file"."file_name" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."check_sum" IS '?????????';
COMMENT ON COLUMN "public"."hs_file"."content_type" IS '??????';
COMMENT ON COLUMN "public"."hs_file"."checksum_algorithm" IS '???????????????';
COMMENT ON COLUMN "public"."hs_file"."data_size" IS '??????';
COMMENT ON COLUMN "public"."hs_file"."additional_info" IS '????????????';
COMMENT ON COLUMN "public"."hs_file"."scope" IS '??????';
COMMENT ON COLUMN "public"."hs_file"."entity_id" IS '??????Id';
COMMENT ON COLUMN "public"."hs_file"."location" IS '????????????';




-- Table: public.hs_init

-- DROP TABLE IF EXISTS public.hs_init;

CREATE TABLE IF NOT EXISTS public.hs_init
(
    id uuid NOT NULL,
    init_data jsonb,
    scope character varying(255) COLLATE pg_catalog."default",
    created_time bigint,
    created_user character varying(255) COLLATE pg_catalog."default",
    updated_time bigint,
    updated_user character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_dict_data_copy1_pkey PRIMARY KEY (id),
    CONSTRAINT uk_init_scope UNIQUE (scope)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_init
    OWNER to postgres;

COMMENT ON TABLE public.hs_init
    IS '?????????';

COMMENT ON COLUMN public.hs_init.id
    IS 'Id';

COMMENT ON COLUMN public.hs_init.init_data
    IS '???????????????';

COMMENT ON COLUMN public.hs_init.scope
    IS '??????';

COMMENT ON COLUMN public.hs_init.created_time
    IS '????????????';

COMMENT ON COLUMN public.hs_init.created_user
    IS '?????????';

COMMENT ON COLUMN public.hs_init.updated_time
    IS '????????????';

COMMENT ON COLUMN public.hs_init.updated_user
    IS '?????????';



-- Table: public.hs_production_line

-- DROP TABLE IF EXISTS public.hs_production_line;

CREATE TABLE IF NOT EXISTS public.hs_production_line
(
    id uuid NOT NULL,
    workshop_id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000000) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    mobile character varying(255) COLLATE pg_catalog."default",
    factory_id uuid,
    bg_images character varying(100000) COLLATE pg_catalog."default",
    CONSTRAINT tb_production_line_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_production_line
    OWNER to postgres;





--?????????????????????
CREATE TABLE IF NOT EXISTS public.hs_system_version
(
    id uuid NOT NULL,
    version character varying(225) COLLATE pg_catalog."default" NOT NULL DEFAULT '0.0.1'::character varying,
    publish_time bigint NOT NULL,
    comment character varying(255) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_user uuid,
    created_time bigint NOT NULL,
    updated_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT hs_system_version_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_system_version
    OWNER to postgres;

COMMENT ON TABLE public.hs_system_version
    IS '???????????????';




-- Table: public.hs_workshop

-- DROP TABLE IF EXISTS public.hs_workshop;

CREATE TABLE IF NOT EXISTS public.hs_workshop
(
    id uuid NOT NULL,
    factory_id uuid NOT NULL,
    code character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    logo_icon character varying(255) COLLATE pg_catalog."default",
    logo_images character varying(1000000) COLLATE pg_catalog."default",
    remark character varying(1000) COLLATE pg_catalog."default",
    tenant_id uuid NOT NULL,
    created_time bigint NOT NULL,
    created_user uuid,
    updated_time character varying(255) COLLATE pg_catalog."default",
    updated_user uuid,
    del_flag character varying(255) COLLATE pg_catalog."default",
    bg_images character varying(1000000) COLLATE pg_catalog."default",
    CONSTRAINT tb_workshop_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.hs_workshop
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
    lang_key character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT tb_menu_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_menu
    OWNER to postgres;


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
    CONSTRAINT tb_tenant_sys_role_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tb_tenant_sys_role
    OWNER to postgres;



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




----??????????????????????????????????????????
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