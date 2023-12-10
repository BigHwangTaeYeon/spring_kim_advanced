package hello.aop.order.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class AspectV3 {

    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder(){}   // pointcut signature라고 부른다.

    // class 이름 패턴이 *Service인 것. 보통 비즈니스 로직은 Service에 들어간다.
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService(){}

    // @Around("execution(* hello.aop.order..*(..))")
    @Around("allOrder()")  // 위와 다르게 pointcut의 메소드를 담았다.
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
        log.info("[log] {}", joinPoint.getSignature());             // JoinPointSignature
        return joinPoint.proceed();
    }
    // 실무에서 포인트컷을 모아두고, 가져와서 사용한다.
    
    // hello.aop.order 패키지와 하위 패키지 이면서, 클래스 이름 패턴이 *Service인 것.
    // *Service는 class 뿐 아니라 interface에서도 적용이 된다.
    // && || ! 다 적용 된다.
    @Around("allOrder() && allService()") 
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
