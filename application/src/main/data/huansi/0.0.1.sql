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

