
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

CREATE INDEX hs_device_oee_every_hour_device_id_ts
ON hs_device_oee_every_hour (device_id, ts);