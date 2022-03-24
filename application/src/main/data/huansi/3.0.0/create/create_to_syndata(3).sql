------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------数据同步服务要用的三张表-（sql_error_history、table_ds_config、tenant_db_info）--------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



-- Table: public.sql_error_history

-- DROP TABLE IF EXISTS public.sql_error_history;

CREATE TABLE IF NOT EXISTS public.sql_error_history
(
    uuid character varying COLLATE pg_catalog."default" NOT NULL,
    c_time bigint,
    u_time bigint,
    tenant_id uuid,
    factory_id uuid,
    sql_str text COLLATE pg_catalog."default",
    CONSTRAINT sql_error_history_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.sql_error_history
    OWNER to postgres;






-- Table: public.table_ds_config

-- DROP TABLE IF EXISTS public.table_ds_config;

CREATE TABLE IF NOT EXISTS public.table_ds_config
(
    table_name character varying COLLATE pg_catalog."default",
    type character varying COLLATE pg_catalog."default",
    relation_table_name character varying COLLATE pg_catalog."default",
    relation_table_columns character varying COLLATE pg_catalog."default",
    table_columns character varying COLLATE pg_catalog."default"
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.table_ds_config
    OWNER to postgres;









-- Table: public.tenant_db_info

-- DROP TABLE IF EXISTS public.tenant_db_info;

CREATE TABLE IF NOT EXISTS public.tenant_db_info
(
    factory_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    db_url character varying(100) COLLATE pg_catalog."default",
    user_name character varying(100) COLLATE pg_catalog."default",
    password character varying(100) COLLATE pg_catalog."default",
    c_time bigint,
    u_time bigint,
    CONSTRAINT tenant_db_info_pkey PRIMARY KEY (factory_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tenant_db_info
    OWNER to postgres;

COMMENT ON TABLE public.tenant_db_info
    IS '工厂db 信息';