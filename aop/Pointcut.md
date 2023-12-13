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





### this, target





### 정리










