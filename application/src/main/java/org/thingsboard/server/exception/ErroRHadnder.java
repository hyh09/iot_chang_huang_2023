package org.thingsboard.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thingsboard.server.dao.sql.role.userrole.ResultVo;
import org.thingsboard.server.common.data.vo.CustomException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class ErroRHadnder {

    //数据校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultVo error(MethodArgumentNotValidException e){
        e.printStackTrace();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        FieldError error = fieldErrors.get(0);
        String msg = error.getDefaultMessage();
      return ResultVo.getFail("入参校验错误: " +msg);

    }


    /**
     * 全局自定义异常CustomException捕获类
     * @param e
     * @return
     */
    @ExceptionHandler({CustomException.class})
    @ResponseBody
    public ResultVo businessExceptionHandler(CustomException e) {
        log.error(e.getCode(), e.getMessage());
        log.error("系统异常", e);
        return ResultVo.builderFail(e.getCode(), e.getMessage());
    }



}
