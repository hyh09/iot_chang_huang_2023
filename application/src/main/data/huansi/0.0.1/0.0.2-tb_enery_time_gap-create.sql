
----新增表用于趋势图的统计时间差
CREATE TABLE IF NOT EXISTS public.tb_enery_time_gap
(
    id uuid NOT NULL,
    created_time bigint,
    created_user uuid,
    tenant_id uuid,
    updated_time bigint,
    updated_user uuid,
    entity_id uuid,
    ts character varying(255) COLLATE pg_catalog."default",
    time_gap bigint,
    key bigint,
    CONSTRAINT tb_enery_time_gap_pkey PRIMARY KEY (id)
)
