package org.thingsboard.server.common.data.vo.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.thingsboard.server.common.data.StringUtils;

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


    private  String time;

    private  long createTime;

    public long getCreateTime() {
        if(!StringUtils.isEmpty(time)) {
            return Long.parseLong(time);
        }
        return  0;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
