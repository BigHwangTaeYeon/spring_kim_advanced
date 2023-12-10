# Pointcut

### 포인트컷 지시자
포인트컷 표현식은 execution같은 포인트컷 지시자(Pointcut Designator)로 시작한다.
줄여서 PCD라고 한다.

*** execution ***   : 조인포인트를 매칭한다. Spring AOP에서 가장 많이 사용하고, 기능도 복잡
within      : 특정 타입 내의 조인 포인트를 매칭한다.
args        : 인자가 주어진 타입의 인스턴스인 조인 포인트
this        : 스프링 빈 객체(Spring AOP Proxy)를 대상으로 하는 조인 포인트
target      : Target 갹채(Spring AOP Proxy가 가르키는 실제 대상)를 대상으로 하는 조인 포인트
@target     : 실행 객체의 클래스에 주어진 타입의 어노테이션이 있는 조인 포인트
@within     : 주어진 어노테이션이 있는 타입 내 조인 포인트
@annotation : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
@args       : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
bean        : 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다.

### execution
execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
//public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
? : 생략가능

*** 매칭조건(public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)) ***
접근 제어자? : public
반환타입     : String
선언타입?    : hello.aop.member.MemberServiceImpl
메서드이름   : hello
파라미터     : (String)
예외?        : 생략

*** setExpression 사용법은 class ExecutionTest에서 확인 ***
```java
pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
Assertions.assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
```
타입으로 매치하는 것으로 부모를 선택하는 것도 된다.














