
----修改表结构得 v1.2需 涉及到需求
alter table tb_user
	add user_level integer;
alter table tenant
    add county_level varchar(255);
alter table tenant
    add longitude varchar(255);
alter table tenant
    add latitude varchar(255);