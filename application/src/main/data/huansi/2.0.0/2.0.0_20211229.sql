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
COMMENT ON COLUMN public.device.comment IS '设备编号';
alter table device add flg boolean default false;




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