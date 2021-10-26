package org.thingsboard.server.entity.role;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResultVo {

    /**
     * 状态码
     */
    private  String code;
    /**
     * 错误提示信息
     */
    private  String msg;

    /**
     * 成功返回得数据
     */
    private  Object data;


   public static ResultVo  getFail(String  msg)
    {
        ResultVo  resultVo = new ResultVo();
        resultVo.setCode("0");
        resultVo.setMsg(msg);
        return  resultVo;
    }
}
