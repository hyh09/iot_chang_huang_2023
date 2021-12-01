
--factory增加字段

ALTER TABLE public.hs_factory
    ADD COLUMN country character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.country IS '国家';

ALTER TABLE public.hs_factory
    ADD COLUMN province character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.province IS '省';

ALTER TABLE public.hs_factory
    ADD COLUMN city character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.city IS '市';

ALTER TABLE public.hs_factory
    ADD COLUMN area character varying(1000) COLLATE pg_catalog."default";
COMMENT ON COLUMN public.hs_factory.area IS '区';

--删除列
ALTER TABLE hs_factory DROP COLUMN admin_user_id;
ALTER TABLE hs_factory DROP COLUMN admin_user_name;