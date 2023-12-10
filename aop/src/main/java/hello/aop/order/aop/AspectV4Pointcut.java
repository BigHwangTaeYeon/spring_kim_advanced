package hello.aop.order.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Order(1)
public class AspectV4Pointcut {
    
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")  // 위와 다르게 pointcut의 메소드를 담았다.
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
        log.info("[log] {}", joinPoint.getSignature());             // JoinPointSignature
        return joinPoint.proceed();
    }

    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
        try {
            log.info("[트랜젝션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜젝션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜젝션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
