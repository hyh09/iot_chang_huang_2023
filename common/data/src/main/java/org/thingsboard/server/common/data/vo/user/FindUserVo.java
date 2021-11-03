package org.thingsboard.server.common.data.vo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @program: thingsboard
 * @description: 查询用户返回的对象
 * @author: HU.YUNHUI
 * @create: 2021-11-03 14:40
 **/
@Data
@ToString
public class FindUserVo {

    private  String id;

    @JsonProperty("userCreator")
    private  String  usercreator;

    @JsonProperty("phoneNumber")
    private String phonenumber;

    @JsonProperty("activeStatus")
    private  String activestatus;

    @JsonProperty("userCode")
    private  String usercode;

    @JsonProperty("email")
    private  String email;

    @JsonProperty("authority")
    private  String authority;

    @JsonProperty("tenantId")
    private  String tenantid;

    @JsonProperty("userName")
    private  String username;

    //    {
//        "usercreator": "9007cec0-3c4a-11ec-9ccb-3504b42aaeaf",
//            "authority": "TENANT_ADMIN",
//            "phonenumber": "12345674322",
//            "tenantid": "94892bc0-3ad7-11ec-a910-51a6dac8d734",
//            "id": "b87312d0-3c67-11ec-a746-759677c5ac72",
//            "usercode": "YH0004",
//            "email": "342@324.com",
//            "activestatus": "0",
//            "username": "用户5"
//    }

}
