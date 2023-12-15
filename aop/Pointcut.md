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


### within
특정 타입 내의 조인 포인트에 대한 매칭을 제한한다.
해당 타입이 매칭되면, 그 안의 메서드(조인 포인트)들이 자동으로 매칭된다.
*** 타입만 매치되면 메서드는 전부 자동으로 매치된다. ***

### args
인자가 주어진 타입의 인스턴스인 조인 포인트로 매칭
기본 문법은 execution의 args 부분과 같다
*** execution과 차이점 ***
execution은 파라미터 타입이 정확하게 매칭되어야 한다. execution은 클래스에 선언된 정보를 기반으로 판단한다.
args는 부모 타입을 허용한다. args는 실제 넘어온 파라미터 객체 인스턴스를 보고 판단한다.
```java
assertThat(pointcut("args(java.io.Serializable)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();

// Execution은 부모 타입을 받아들일 수 없기에 False
assertThat(pointcut("execution(* *(java.io.Serializable))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
// 정적으로 클래스에 선언된 정보만 포고 판단하는 execution(* *(Object))는 매칭에 실패한다.
assertThat(pointcut("execution(* *(Object))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
```

### @target @within
@target : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
@within : 주어진 애노테이션이 있는 타입 내 조인 포인트
@target, @within은 타입에 있는 애노테이션으로 AOP 적용 여부를 판단한다.
@target(hello.aop.member.annotation.ClassAop)
@within(hello.aop.member.annotation.ClassAop)
```java
@ClassAop   // 위 어노테이션에 정의된 것이 MemberServiceImpl class의 @ClassAop 보고 적용된다.
@Component
public class MemberServiceImpl implements MemberService {
    //...
}
```
@Target은 부모 클래스의 메서드까지 어드바이스를 다 적용하고,
@within은 자기 자신의 클래스에 정의된 메서드에만 어드바이스를 적용한다.

AtTargetAtWithinTest
[@target] void hello.aop.pointcut.AtTargetAtWithinTest$Child.childMethod()
[@within] void hello.aop.pointcut.AtTargetAtWithinTest$Child.childMethod()
[@target] void hello.aop.pointcut.AtTargetAtWithinTest$Parent.parentMethod()

@Around("execution(* hello.aop..*(..)) && @target(hello.aop.member.annotation.ClassAop)")로 위치를 전체를 지정해주고,
    @ClassAop을 Child class에만 적용되있는 상황에서,
@target은 2번 호출이 되며 *부모와 자식* 모두 호출되는 것을 볼 수 있다.
@within은 한번 호출이 되며 *자식*에만 호출되는 것을 볼 수 있다.

*** 주의 ***
**포인트컷 지시자는 단독으로 사용하면 안된다.**
execution(* hello.aop..*(..)) 를 통해 적용 대상을 줄여준 것을 확인할 수 있다.
args, @args, @target 은 실제 객체 인스턴스가 생성되고 실행될 때, 어드바이스 적용 여부를 확인할 수 있다.
결국 프록시가 있어야 실행 시점을 판단할 수 있다. 프록시가 없다면 판단 자체가 불가능하다.
스프링 빈에 AOP 프록시를 적용하려하면 스프링이 내부에서 사용하는 빈 중에 final로 지정된 빈들도 이기에 오류 발생 가능성이 있다.
따라서 이러한 표현식은 최대한 프록시 적용 대상을 축소하는 표현식과 함께 사용하여야 한다.

현재 Test code에서도 execution을 제외하고 @target만 사용해도 에러가 뜬다.

### @annotation @args
@annotaion 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
@annotation(hello.aop.member.annotation.MethodAop)

AtAnnotationAspect 없을 때 Log.
    memberService Proxy = class hello.aop.member.MemberServiceImpl
AtAnnotationAspect 있을 때 Log.
    memberService Proxy = class hello.aop.member.MemberServiceImpl$$SpringCGLIB$$0
    [@annotation] String hello.aop.member.MemberServiceImpl.hello(String)

@args 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
잘 안써 !

### bean
bean 스프링 전용 포인트컷 지시자, 빈 이름으로 지정한다.
    스프링에서만 사용할 수 있는 특별한 지시자.
bean(orderService) || bean(*Repository)
*과 같은 패턴을 사용할 수 있다.

### 매개변수 전달
```java
@Before("allMember() && args(arg,..)")
public void logArgs3(String arg) {
    log.info("[logArgs3] arg = {}", arg);
}
```
포인트컷의 이름과 매개변수의 이름을 맞추어야 한다. 여기서는 arg로 맞추었다.
추가로 타입이 메서드에 지정한 타입으로 제한된다.
    위 코드에서는 메서드타입이 String으로 되어있기에
    @Before("allMember() && args(arg,..)")를
    @Before("allMember() && args(String,..)")으로 정의되는 것으로 이해하면 된다.

```java
@Around("allMember() && args(arg,..)")
public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg) throws Throwable {
    log.info("[logArgs2]{}, arg = {}", joinPoint.getSignature(), arg);
    return joinPoint.proceed();
}
@Before("allMember() && args(arg,..)")
public void logArgs3(String arg) throws Throwable {
    log.info("[logArgs2] arg = {}", arg);
}
```
    * 여기서 지시자는 무엇을 사용하던 중요한게 아니다
@Around
[logArgs2]String hello.aop.member.MemberServiceImpl.hello(String), arg = helloA
@Before
[logArgs2] arg = helloA

최적화 완료,
```java
public String hello(String param) {
    return "ok";
}
memberService.hello("helloA");
```
"ok"라는 반환값이 아닌 param값으로 arg가 넘어와 hello method에 넘겨준 "helloA"값이 출력된다.

logArgs1 : joinPoint.getArgs()[0]와 같이 매개변수를 전달 받는다.
logArgs2 : args(arg,..)와 같이 매개변수를 전달 받는다.
logArgs3 : @Before를 사용한 축약 버전이다. 추가로 타입을 String으로 제한했다.
this : 프록시 객체를 전달 받는다.
target : 실제 대상 객체를 전달 받는다.
@target, @within : 타입의 애노테이션을 전달 받는다.
@annotation : 메서드의 애노테이션을 전달 받는다.
    여기서는 annotation.value()로 해당 애노테이션의 값을 출력하는 모습을 확인할 수 있다.

### this, target
this : 스프링 빈 객체(Spring AOP Proxy)를 대상으로 하는 조인 포인트
target : Target 객체(Spring AOP Proxy가 가르키는 실제 대상)를 대상으로 하는 조인 포인트

this target은 타입 하나를 정확하게 지정해야한다.
'*' 같은 패턴을 사용할 수 없다.
부모 타입을 허용한다.

Spring에서 AOP 적용하면 실제 target 객체 대신에 프롲ㄱ시 객체가 스프링 빈으로 등록된다.
this는 스프링 빈으로 등록되어 있는 프록시 객체를 대상으로 포인트컷을 매칭한다.
target은 실제 target객체를 대상으로 포인트컷을 매칭한다.

JDK 동적 프록시는 interface(memberService)를 기반으로 프록시를 만들지만,
CGLIB 동적 프록시는 구체 클래스(memberServiceImpl)를 기반으로 프록시를 만든다


### 정리



@Trace 로그 출력하기
@Retry 예외 발생시 재시도 하기

10초 이상 걸리면 로그 남기고 원인 남기고 하면 문제점을 찾기, 해결하기 좋다.




