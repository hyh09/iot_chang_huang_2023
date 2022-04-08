
---长胜生产环境客户需要的设备排序


--查询工厂
select * from tenant where title like '%上海长胜集团%';
select * from hs_factory where name like '%上海%' and tenant_id='34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17';
select * from hs_factory where name like '%新乡%' and tenant_id='34b42c20-4e61-11ec-8ae5-dbf4f4ba7d17';

--上海：单进单出冷转移印刷机-低给液机-卫星式转移印花机-蒸化机-水洗烘干机
update device set sort = 1
where factory_id = 'e7fd0750-589a-11ec-afcd-2bd77acada1c'
and name='单进单出冷转移印刷机';
update device set sort = 2
where factory_id = 'e7fd0750-589a-11ec-afcd-2bd77acada1c'
and name='低给液机';
update device set sort = 3
where factory_id = 'e7fd0750-589a-11ec-afcd-2bd77acada1c'
and name='卫星式转移印花机';
update device set sort = 4
where factory_id = 'e7fd0750-589a-11ec-afcd-2bd77acada1c'
and name='蒸化机';
update device set sort = 5
where factory_id = 'e7fd0750-589a-11ec-afcd-2bd77acada1c'
and name='水洗烘干机';

--新乡-平幅煮漂机-1#定型机-2#定型机-双进双出冷转移印刷机-冷转移中样印花机-1#冷转移印花机1-1#冷转移印花机2-2#冷转移印花机-1#冷转移染色机-2#冷转移染色机-卫星式直印机-圆网转移印花机-1#长环蒸化机1-1#长环蒸化机2-2#长环蒸化机1-2#长环蒸化机2-小蒸化机-1#印花后水洗机-2#印花后水洗机-小水洗机-绳状水洗机


update device set sort = 1
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='平幅煮漂机';
update device set sort = 2
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#定型机';
update device set sort = 3
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='2#定型机';
update device set sort = 4
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='双进双出冷转移印刷机';
update device set sort = 5
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='冷转移中样印花机';

update device set sort = 6
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#冷转移印花机1';
update device set sort = 7
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#冷转移印花机2';
update device set sort = 8
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='2#冷转移印花机-1';
update device set sort = 9
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#冷转移染色机-2';
update device set sort = 10
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='2#冷转移染色机';

update device set sort = 11
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='卫星式直印机';
update device set sort = 12
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='圆网转移印花机';
update device set sort = 13
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#长环蒸化机1';
update device set sort = 14
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#长环蒸化机2';
update device set sort = 15
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='2#长环蒸化机1';

--2#长环蒸化机2-小蒸化机-1#印花后水洗机-2#印花后水洗机-小水洗机-绳状水洗机
update device set sort = 16
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='2#长环蒸化机2';
update device set sort = 17
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='小蒸化机';
update device set sort = 18
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='1#印花后水洗机';
update device set sort = 19
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='2#印花后水洗机';
update device set sort = 20
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='小水洗机';
update device set sort = 21
where factory_id = '24d0aa00-589c-11ec-afcd-2bd77acada1c'
and name='绳状水洗机';



