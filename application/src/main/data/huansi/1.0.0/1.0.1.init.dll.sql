
----修改表结构得 v1.2需 涉及到需求
ALTER TABLE tb_user add COLUMN user_level integer DEFAULT 0;

alter table tenant
    add county_level varchar(255);
alter table tenant
    add longitude varchar(255);
alter table tenant
    add latitude varchar(255);


 alter table device
	add flg varchar(255)  default '0';
