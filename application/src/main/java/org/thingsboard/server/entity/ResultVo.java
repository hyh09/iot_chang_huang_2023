package org.thingsboard.server.entity;

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


    public static ResultVo  getSuccessFul(Object  data)
    {
        ResultVo  resultVo = new ResultVo();
        resultVo.setCode("1");
        resultVo.setMsg("success");
        resultVo.setData(data);
        return  resultVo;
    }


    public static ResultVo  getSuccessFul(Object  data,String msg)
    {
        ResultVo  resultVo = new ResultVo();
        resultVo.setCode("1");
        resultVo.setMsg(msg);
        resultVo.setData(data);
        return  resultVo;
    }
}
