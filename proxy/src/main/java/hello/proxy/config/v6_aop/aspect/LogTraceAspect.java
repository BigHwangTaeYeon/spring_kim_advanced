package hello.proxy.config.v6_aop.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class LogTraceAspect {
    
    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }
    
    @Around("execution(* hello.proxy.app..*(..))")                              // pointcut
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {     // advisor
        //LogTraceAdvice class 로직과 같다.
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            // Method method = invocation.getMethod();
            // mehtod에서 선언한 메소드 이름을 가져온다
            // String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
            status = logTrace.begin(message);

            // 로직 호출
            // Object result = method.invoke(target, args);
            // Object result = invocation.proceed();
            Object result = joinPoint.proceed();
            
            // String result = target.request(itemId);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

}
