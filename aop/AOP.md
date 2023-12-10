# AOP

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

AspectJ를 차용해서 사용하고, 실제로 AspectJ를 직접 사용하는 것은 아니다.
Aspectjweaver.jar 사용

### Advice 순서
    디폴트로 순서 보장하지 않는다.
    org.springframework.core.annotation.Order @Order 어노테이션을 적용해야한다.
    문제는 어드바이스 단위가 아닌 클래스 단위로 적용할 수 있다.
    그래서 하나의 에스펙트에 여러 어드바이스가 있으면 순서를 보장할 수 없다.

```java
@Aspect
@Order(1)   // 이곳에만 적용된다.
public class AspectV4Pointcut {
    // Order(1) Method에는 적용이 되지 않는다.
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")  // 위와 다르게 pointcut의 메소드를 담았다.
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
        // ...
    }
    // Order(2) Method에는 적용이 되지 않는다.
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
        // ...
    }
}
```
그래서 아래와같이 Inner Class로 만들어준다.
```java
@Aspect
public class AspectV5Order {
    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")  // 위와 다르게 pointcut의 메소드를 담았다.
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable{   // advice
            // ...
        }
    }
    @Aspect
    @Order(1)
    public static class TxAspect {
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable{
            // ...
        }
    }
}
// 그리고 application 안에 아래와 같이 import 해준다.
@Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
```

### Advice의 종류

@Around         : 메서드 호출 전후에 수행, 가장 강력한 어드바이스, 조인포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능 
@Before         : 조인 포인트 실행 이전에 실행
@AfterRetuning  : 조인 포인트가 정상 완료 후 실행
@AgterThrowing  : 조인 포인트가 예외를 던지는 경우 실행
@After          : 조인 포인트가 정상 또는 예외에 관계없이 실행(finally)

모든 어드바이스는 JoinPoint를 첫번째 파라미터에 사용하고
@Around는 ProceedJoinPoint를 사용해야한다.
    @Around는 Object result = joinPoint.proceed();를 꼭 실행해주어야 하기 때문이다.

```java
@AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
public void doReturn(JoinPoint joinPoint, Object result) {
    log.info("[return] {} result = {}", joinPoint.getSignature(), result);
}
```
위처럼 Object result의 반환 타입을 달리 해도 된다.
하지만 Service의 해당 메서드 반환 타입이 void이기에 출력이 되지 않는다.
만약 Controller에 value값을 넣고 String result로 받는다면
Controller에 Method가 String의 "ok"를 반환하기에 ok가 출력된다.
결론은 반환 타입에 맞는 변수 타입을 지정해주면 출력이 되고 달리하면 안나온다.

```java
@AfterThrowing(value="hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
public void doThrowing(JoinPoint joinPoint, Exception ex) {
    log.info("[ex] {} message = {}", ex);
}
```
이것도 Exception 부모 타입을 지정하면 모든 자식 타입은 인정이 된다.

@Around에서는 joinPoint.proceed() 로직을 실행하지 않을 수도,
Object result = joinPoint.proceed(args[]); 바꿀 수도,
joinPoint.proceed()
joinPoint.proceed()
joinPoint.proceed() 이렇게 여러번 실행할 수도 있다.


Spring 5.2.7부터 @Aspect안에서 동일한 조인포인트의 우선순위를 정했다.
@Around @Before @After @AfterReturning @AfterThrowing
@Aspect안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다
    이 경우에, @Aspect를 분리하고 @Order를 사용하자.

@Around는 가장 넓은 기능을 제공하지만 실수할 가능성이 높다.
@Before @After 같은 어드바이스는 기능은 적지만 실수할 가능성이 낮고 코드가 단순하다.

*** 좋은 설계는 제약이 있는 것이다. ***
제약은 실수를 방지해준다.
내 생각에는 역할을 명확히 나누어 주므로 코드의 의도 파악과 고민이 줗어들 듯하다.
유지보수성이 높아진다 생각이 된다.