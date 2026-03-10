package com.sau.gym.common.log.utils;

import com.alibaba.fastjson.JSON;
import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.model.entity.system.OperaLog;
import com.sau.gym.utils.AuthContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 16:18
 */
public class LogUtil {

    //操作执行之后调用
    public static void afterHandlLog(Log sysLog, Object proceed,
                                     OperaLog operaLog, int status ,
                                     String errorMsg) {
        if(sysLog.isSaveResponseData()) {
            operaLog.setJsonResult(JSON.toJSONString(proceed));
        }
        operaLog.setStatus(status);
        operaLog.setErrorMsg(errorMsg);
    }

    //操作执行之前调用
    public static void beforeHandleLog(Log sysLog,
                                       ProceedingJoinPoint joinPoint,
                                       OperaLog operaLog) {

        // 设置操作模块名称
        operaLog.setTitle(sysLog.title());
        operaLog.setOperatorType(sysLog.operatorType().name());

        // 获取目标方法信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature() ;
        Method method = methodSignature.getMethod();
        operaLog.setMethod(method.getDeclaringClass().getName());

        // 获取请求相关参数
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        operaLog.setRequestMethod(request.getMethod());
        operaLog.setOperaUrl(request.getRequestURI());
        operaLog.setOperaIp(request.getRemoteAddr());

        // 设置请求参数
        if(sysLog.isSaveRequestData()) {
            String requestMethod = operaLog.getRequestMethod();
            if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
                String params = Arrays.toString(joinPoint.getArgs());
                operaLog.setOperaParam(params);
            }
        }
        operaLog.setOperaName(AuthContextUtil.get().getUsername());
    }
}
