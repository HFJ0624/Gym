package com.sau.gym.common.log.aspect;

import com.sau.gym.common.log.annotation.Log;
import com.sau.gym.common.log.service.AsyncOperaLogService;
import com.sau.gym.common.log.utils.LogUtil;
import com.sau.gym.model.entity.system.OperaLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 16:12
 */
@Aspect
@Component
@Slf4j
public class LogAspect {// 环绕通知切面类定义

    @Autowired
    private AsyncOperaLogService asyncOperaLogService;

    @Around(value = "@annotation(sysLog)")
    public Object doAroundAdvice(ProceedingJoinPoint joinPoint , Log sysLog) {

        // 构建前置参数
        OperaLog operaLog = new OperaLog() ;

        LogUtil.beforeHandleLog(sysLog , joinPoint , operaLog) ;

        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
            // 执行业务方法
            LogUtil.afterHandlLog(sysLog , proceed , operaLog , 0 , null) ;
            // 构建响应结果参数
        } catch (Throwable e) {                                 // 代码执行进入到catch中，
            // 业务方法执行产生异常
            e.printStackTrace();                                // 打印异常信息
            LogUtil.afterHandlLog(sysLog , proceed , operaLog , 1 , e.getMessage()) ;
            throw new RuntimeException();
        }

        //保存日志数据
        asyncOperaLogService.saveSysOperLog(operaLog);

        //返回执行结果
        return proceed;
    }
}
