

# 长胜的生产环境

## 数据库的密码

```shell
Newtech2023.
111.229.157.131 30682 postgres
```



# 长胜环境



##    二期看板的测试地址

> http://ntx-dashboard.textile-saas.huansi.net/dashboard/factory
>
> 785785785@qq.com
>
> 123456





## 更新系统管理用户密码

```shell
明文码： huansi@.net  iot.system@huansi.net
加密后： $2a$10$FSBJstseQkF.yI/wca8RDu3672nBpBgELP0Tjf3MNo5r/ofDjr2dK

V3明文密码： huansi.net@2023  sysadmin@huansi.net
加密后  $2a$10$zmAR11xf.0lXgbbk2fNtMu4v3hDTv1LEVz7zrsRZ9ME5vhk9rlDUq

update   user_credentials  set "password"='加密后' where  user_id=(
select id   from tb_user  where  authority='SYS_ADMIN' and email='系统用户邮箱')
```







# V3 	

##   V3 生产环境

> http://iot.v3.saas.huansi.net/apiManagement
