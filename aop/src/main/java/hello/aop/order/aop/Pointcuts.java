package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
    
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder(){}   // pointcut signature라고 부른다.

    // class 이름 패턴이 *Service인 것. 보통 비즈니스 로직은 Service에 들어간다.
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService(){}

    // allOrder() && allService()
    @Pointcut("allOrder() && allService()")
    public void orderAndService() {}

}
