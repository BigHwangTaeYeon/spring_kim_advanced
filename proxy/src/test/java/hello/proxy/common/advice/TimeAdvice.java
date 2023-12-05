package hello.proxy.common.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeAdvice implements MethodInterceptor {
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.info("TimeAdvice 실행");
        long startTime = System.currentTimeMillis();

        // 원래 target 클래스에서 가져왔는데 invocation에서 가져온다.
        Object result = invocation.proceed();

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeAdvice 종료 resultTime = {}", resultTime);
        return result;
    }
    
}
