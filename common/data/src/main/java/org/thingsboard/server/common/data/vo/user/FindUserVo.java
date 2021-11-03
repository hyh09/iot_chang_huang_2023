package org.thingsboard.server.common.data.vo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.vo.RowName;

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
    @RowName("t1.user_creator")
    private  String  usercreator;

    @JsonProperty("phoneNumber")
    @RowName("t1.phone_number")
    private String phonenumber;

    @JsonProperty("activeStatus")
    @RowName("t1.active_status")
    private  String activestatus;

    @JsonProperty("userCode")
    @RowName("t1.user_code")
    private  String usercode;

    @JsonProperty("email")
    @RowName("email")
    private  String email;

    @JsonProperty("authority")
    private  String authority;

    @JsonProperty("tenantId")
    private  String tenantid;

    @JsonProperty("userName")
    @RowName("t1.user_name")
    private  String username;


    private  String time;

    @RowName("t1.created_time")
    private  long createdTime;

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getCreatedTime() {
        if(!StringUtils.isEmpty(time)) {
            return Long.parseLong(time);
        }
        return  0;
    }


}
