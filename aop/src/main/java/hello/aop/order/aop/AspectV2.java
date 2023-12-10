package hello.aop.order.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class AspectV2 {

    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder(){}   // pointcut signature라고 부른다.

    // @Around("execution(* hello.aop.order..*(..))")
    @Around("allOrder()")  // 위와 다르게 pointcut의 메소드를 담았다.
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
        log.info("[log] {}", joinPoint.getSignature());             // JoinPointSignature
        return joinPoint.proceed();
    }
    // 실무에서 포인트컷을 모아두고, 가져와서 사용한다.
}
