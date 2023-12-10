package hello.aop.order.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class AspectV6Advice {

    // @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    // public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
    //     try {
    //         // @Before
    //         log.info("[트랜젝션 시작] {}", joinPoint.getSignature());
    //         Object result = joinPoint.proceed();
    //         // @AfterReturning
    //         log.info("[트랜젝션 커밋] {}", joinPoint.getSignature());
    //         return result;
    //     } catch (Exception e) {
    //         // @AfterThrowing
    //         log.info("[트랜젝션 롤백] {}", joinPoint.getSignature());
    //         throw e;
    //     } finally {
    //         // @After
    //         log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
    //     }
    // }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    // Around에서 ProceedingJoinPoint 매개변수를 사용한 것과 달리 JoinPoint를 받는다
    public void doBeafore(JoinPoint joinPoint){
        
        log.info("[before] {}", joinPoint.getSignature());
        // log.info("hello");
    }

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} result = {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(value="hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message = {}", ex);
    }

    @After(value="hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}
