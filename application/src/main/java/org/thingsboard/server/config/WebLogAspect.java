package org.thingsboard.server.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.exception.IncorrectParameterException;

import javax.mail.MessagingException;
import java.util.Optional;

/**
 * 日志记录
 *
 * @author wwj
 * @since 2021.11.5
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    // @Pointcut("execution(public * org.thingsboard.server.controller..*(..))")
    // @Pointcut("execution(public * org.thingsboard.server.dao.hs.service.*.*(..))")
    @Pointcut(
            // custom
            "execution(public * org.thingsboard.server.controller.AlarmRecordController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.AlarmRuleController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.RTMonitorAppController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.RTMonitorController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.BoardSceneController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.BoardRTMonitorController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.DictDataController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.DictDeviceController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.FileController.*(..))"
                    + "||execution(public * org.thingsboard.server.controller.OrderController.*(..))"
    )
    public void webLog() {
    }


    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        log.info("===============================Start========================");
//        long startTime = System.currentTimeMillis();
//        ServletRequestAttributes attributes = (ServletRequestAttributes)
//                RequestContextHolder.getRequestAttributes();
//        Optional.ofNullable(attributes).ifPresentOrElse((r) -> {
//            HttpServletRequest request = r.getRequest();
//            log.info("IP                 : {}", request.getRemoteAddr());
//            log.info("URL                : {}", request.getRequestURL().toString());
//            log.info("HTTP Method        : {}", request.getMethod());
//            log.info("Params             : {}", request.getQueryString());
//            log.info("In Params          : {}", new Gson().toJson(proceedingJoinPoint.getArgs()));
//            log.info("Class Method       : {}.{}", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
//                    proceedingJoinPoint.getSignature().getName());
//        }, () -> log.debug("ServletRequestAttributes 为 null"));

        try {
            Object result = proceedingJoinPoint.proceed();
//            log.info("Response           : {}", new Gson().toJson(result));
//            log.info("Response Time      : {} ms", System.currentTimeMillis() - startTime);
//            log.info("================================End=========================");
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @SuppressWarnings("all")
    private ThingsboardException handleException(Exception exception) {
        log.error("Error [{}]", exception.getMessage(), exception);

        String c = "";

        // 处理sql异常提示
        ConstraintViolationException sqlEx;
        if (exception instanceof ConstraintViolationException) {
            sqlEx = Optional.of((ConstraintViolationException) exception).orElse(null);
        } else if (exception.getCause() instanceof ConstraintViolationException) {
            sqlEx = Optional.of((ConstraintViolationException) (exception.getCause())).orElse(null);
        } else {
            sqlEx = null;
        }

        if (sqlEx != null && sqlEx.getConstraintName() != null) {
            if (sqlEx.getConstraintName().equalsIgnoreCase("uk_code_and_tenant_id"))
                return new ThingsboardException("数据字典编码重复！请重新输入", ThingsboardErrorCode.GENERAL);
            else if (sqlEx.getConstraintName().equalsIgnoreCase("uk_code_and_tenant_id_2"))
                return new ThingsboardException("设备字典编码重复！请重新输入", ThingsboardErrorCode.GENERAL);
            else if (sqlEx.getConstraintName().equalsIgnoreCase("uk_component"))
                return new ThingsboardException("设备字典部件编码重复！请重新输入", ThingsboardErrorCode.GENERAL);
            else if (sqlEx.getConstraintName().equalsIgnoreCase("uk_hs_order_no"))
                return new ThingsboardException("订单编码重复！请重新输入", ThingsboardErrorCode.GENERAL);
            else if (sqlEx.getConstraintName().equalsIgnoreCase("uk_graph_item"))
                return new ThingsboardException("属性重复使用！请重新输入", ThingsboardErrorCode.GENERAL);
        }

        if (exception.getCause() != null) {
            c = exception.getCause().getClass().getCanonicalName();
        }

        if (exception instanceof ThingsboardException) {
            return (ThingsboardException) exception;
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
                || exception instanceof DataValidationException || c.contains("IncorrectParameterException")) {
            return new ThingsboardException(exception.getMessage(), ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new ThingsboardException("Unable to send mail: " + exception.getMessage(), ThingsboardErrorCode.GENERAL);
        } else {
            return new ThingsboardException(exception.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

}
