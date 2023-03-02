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


----修改表结构得 v1.2需 涉及到需求
ALTER TABLE tb_user add COLUMN user_level integer DEFAULT 0;