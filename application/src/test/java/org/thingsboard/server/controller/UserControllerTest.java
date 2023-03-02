package org.thingsboard.server.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thingsboard.server.ThingsboardServerApplication;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ThingsboardServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest  {
    @Autowired
    protected UserService userService;

    @Test
    public  void  Test001()
    {
        TenantId tenantId = new TenantId(EntityId.NULL_UUID);
        PageLink pageLink = new PageLink(10,1);

        userService.findUsersByTenantId(tenantId,pageLink);

    }


    /**
     * 添加
     */
    @Test
    public   void  saveTest001()
    {
        User  user1 = new User();
        user1.setEmail("123453333678@qq.com");
        user1.setPhoneNumber("12333334567");
        user1.setAuthority(Authority.SYS_ADMIN);
        user1.setLastName("悟空");
        user1.setUserCode("0002");
        user1.setUserName("孙悟空");
        User  user = userService.saveUser(user1);
        System.out.println("打印当前的数据:"+user);
    }

}
