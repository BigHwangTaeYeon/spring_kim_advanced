# Aspect AOP
@Aspect는 관점 지향 프로그래밍(AOP)을 가능하게 하는 AspectJ 프로젝트에서 제공하는 어노테이션이다.
스프링은 이걸을 차용하여 프록시를 통한 AOP를 가능하게 한다.

```java
@Aspect
public class LogTraceAspect {
    // ...
    @Around("execution(* hello.proxy.app...*(..))")                             // pointcut
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {     // advisor
    // ...
    }
}
```
@Around Annotation으로 pointcut을 설정하고
method안에 로직을 advisor와 같이 그려주면 된다.

AnnotationAwareAspectAutoProxyCreator는 Advisor를 자동으로 찾아와 필요한 곳에 프록시를 생성하고 적용해준다.
@Aspect를 추가하면 @Aspect를 찾아 Advisor로 만들어준다.

1. @Aspect를 어드바이저로 변환하여 저장하는 과정
    - @Aspect 조회
    - @Around(..)   // pointcut 
        public ...  // advisor
    - BeanFactoryAspectJAdvisorsBuilder class이다.
        @Aspect를 기반으로 포인트컷, 어드바이스, 어드바이저를 생성 보관을 담당한다.
        @Aspect 어드바이저 빌더 내부 저장소에 캐시한다.
        이미 만들어져 있는 경우, 캐시에 저장된 어드바이저를 반환한다.
2. 어드바이저를 기반으로 프록시 생성
    1) 생성 : 빈 대상 객체 생성
    2) 전달 : 빈 등록 전, 빈 후처리기에 전달
    3-1) Advisor 빈 조회 : IOC Container에 Advisor빈을 모두 조회
    3-2) @Aspect Advisor 조회 : @Aspect 어드바이저 빌더 내부에 저장된 Advisor를 모두 조회
    4) 프록시 적용 대상 체크 : 3-1, 3-2 에서 조회한 Advisor에 포함된 포인트컷을 사용하여 프록시 대상여부를 판단
        객체의 모든 메서드를 포인트컷에 하나하나 매칭해본다.
    5) 프록시 생성 : 적용 대상이면 프록시를 생성하고 반환한다.
    6) 반환된 객체를 빈으로 등록한다.


@Aspect가 있으면, Spring이 @Around("...")를 보고 Advisor를 만든다.
@Around("...")로 pointcut을, public Object execute(ProceedingJoinPoint ...)를 보고 Advice를 만든다.



### AOP 소개
핵심 기능과 부가 기능

핵심기능 : 객체가 제공하는 고유의 기능 (OrderService의 핵심 기능은 주문 로직)
부가기능 : 핵심 기능을 보조하기 위해 제공되는 기능 (로그 추적 로직, 트랜젝션 등..)

보통 부가 기능은 여러 클래스에 걸쳐 함께 사용된다.
어플리케이션 호출을 로깅해야하는 경우, 이러한 부가 기능은 횡단 관심사(cross-cutting concerns)가 된다.
하나의 부가 기능이 여러 곳에 동일하게 사용된다는 뜻이다.

부가 기능 문제
    아주 많은 반복이 필요하다.
    여러 곳에 퍼져서 중복 코드를 만든다.
    변경할 때 중복 때문에 많은 수정 필요하다.
    적용 대상을 변경할 때 많은 수정 필요하다.

Aspect
애플리케이션을 바라보는 관점을 하나하나의 기능에서 횡단 관심사 관점으로 달리 보는 것이다.
"에스펙트를 사용한 프로그래밍 방식을 관점 지향 프로그래밍 AOP(Aspect-Oriented Programming)이라 한다."
AOP는 OOP를 대체하기 위한 것이 아닌, 횡단 관심사를 깔끔하게 처리하기 어려운 OOP의 부족한 부분을 보조하는 목적으로 개발되었다.

### AOP 적용 방식

컴파일 시점
    .java를 컴파일하여 .class만드는 시점에 부가 기능 로직을 추가할 수 있다.
    AspectJ가 제공하는 특별한 컴파일러를 사용해야 한다.
    컴파일 된 .class를 디컴파일 해보면 에스팩트 관련 호출 코드가 들어간다.
    부가 기능 코드가 핵심 기능이 있는 컴파일된 코드 주변에 실제로 붙어 버린다고 생각하면 된다.
    AspectJ 컴파일러는 Aspect를 확인해서 해당 클래스가 적용 대상인지 먼저 확인하고, 적용 대상인 경우에 부가 기능 로직을 적용한다.
    이렇게 원본 로직에 부가 기능 로직이 추가되는 것을 위빙(Weaving)이라 한다.
    에스펙트와 실제 코드를 연결해서 붙이는 것

    단점 : 특별한 컴파일러도 필요하고 복잡하다. 그래서 잘 사용하지 않는다.

클래스 로딩 시점
    자바를 실행하면 .class파일을 JVM 내부의 클래스 로더에 보관한다.
    이때, 중간에서 .class 파일을 조작한 다음 JVM에 올릴 수 있다.
    자바 언어는 JVM에 저장 전에 .class를 조작할 수 있는 기능을 제공한다.
    *** 수많은 모니터링 툴들이 이 방식을 사용한다. (java instrumentation)***
    이 시점에 에스펙트를 적용하는 것을 로드 타임 위빙이라 한다.

    단점 : 로드 타임 위빙은 자바를 실행할 때, 특별한 옵션(java -javaagent)을 통해 클래스 로더 조작기를 지정해야하는데,
        이 부분이 번거롭고 운영하기 어렵다. 그래서 잘 사용하지 않는다.

컴파일, 클래스 로딩 시점에서는 코드에 붙여버리기 때문에 프록시를 만들 필요 없다.

런타임 시점(Spring AOP)
    컴파일, 클래스 로더에 클래스가 올라가서 자바가 실행되고 난 다음을 말한다.
    따라서 자바 언어가 제공하는 범위 안에서 부가 기능을 적용해야 한다.
    스프링과 같은 컨테이너의 도움을 받고 프록시와 DI, BeanPostProcessor 같은 개념들을 총 동원해야 한다.
    이렇게 하면 최종적으로 프록시를 통해 빈 부가 기능을 적용할 수 있다.
    여태 학습한 내용이 프록시 방식의 AOP 이다.

    프록시를 사용하기에 AOP기능에 일부 제약이 있다.
    그래도 컴파일, 클래스 로더 조작기를 설정하지 않아도 된다.
    스프링만 있으면 AOP를 적용할 수 있다.

### AOP 적용 위치
적용 가능 지점(JoinPoint) : 생성자, 필드 값 접근, static method, method 실행
    이렇게 AOP 적용할 수 있는 지점을 조인 포인트(JoinPoint)라 한다.
AspectJ를 사용해서 컴파일 시점과 클래스 로딩 시점에 적용하는 AOP는 바이트코드를 실제 조작하기에
    해당 기능을 모든 지점에 다 적용할 수 있다.
프록시 방식을 사용하는 스프링 AOP는 메서드 실행 지점에만 AOP를 적용할 수 있다.
    프록시는 메서드 오버라이딩 개념으로 동작한다. 따라서 생성자나 static 메서드, 필드 값 접근에는 프록시 개념이 적용될 수 없다.
    프록시를 사용하는 *** Spring AOP의 JoinPoint는 Method 실행으로 제한 *** 된다.
프록시 방식을 사용하는 Spring AOP는 Spring Container가 관리할 수 있는 *** Spring Bean에만 AOP를 적용 ***할 수 있다.

*** 중요 ***
스프링이 제공하는 AOP는 프록시를 사용한다.
따라서 프록시를 통해 메서드를 실행하는 시점에만 AOP가 적용된다.
AspectJ를 사용하면 더 복잡하고 더 다양한 기능을 사용할 수 있다.
하지만 공부할 내용이 너무 많고, 자바 관련 설정이 복잡하다.
반면에 Spring AOP는 별도 java 설정없이 Spring만 있으면 편리하게 AOP를 사용할 수 있다.
실무에서는 Spring이 제공하는 AOP만 사용해도 대부분 문제를 해결할 수 있다.

### AOP 용어 정리
조인포인트(joinPoint) : AOP를 적용할 수 있는 위치, 메소드 실행, 생성자 호출, 필드 값 접근 static 메서드 접근 같은 프로그램 실행 중 지점
    추상적인 개념, AOP를 적용할 수 있는 모든 지점
    *** Spring AOP는 Proxy방식을 사용하므로 항상 메소드 실행 지점으로 제한한다. ***
포인트컷(Pointcut)
    Advice가 적용될 위치를 선별하는 기능
    주로 AspectJ 표현식을 사용해서 지정
    *** 프록시를 사용하는 Spring AOP는 메서드 실행 지점만 포인트컷으로 선별 가능 ***
타겟(Target)
    어드바이스를 받는 객첵, 포인트컷으로 결정
어드바이스(Advice)
    부가기능 그 자체.
    특정 조인 포인트에서 Aspect에 의해 취해지는 조치
    Around, Before, After와 같은 다양한 종류의 어드바이스가 있다.
에스펙트(Aspect)
    어드바이스 + 포인트컷을 모듈화 한 것
    @Aspect로 생각하자
    여러 어드바이스와 포인트컷이 함께 존재
어드바이저(Advisor)
    하나의 어드바이스와 하나의 포인트 컷으로 구성
    Spring AOP에서만 사용되는 특별한 용어
위빙(Weaving)
    포인트컷으로 결정된 타겟의 조인 포인트에 어드바이스를 적용하는 것
    위빙을 통해 핵심 기능 코드에 영향을 주지 않고 부가 기능을 추가 할 수 있다.
    AOP 저굥ㅇ을 위해 에스펙트를 객체에 연결한 상태
        컴파일 타임(AspectJ compiler)
        로드 타임
        런타임, Spring AOP는 런타임, 프록시 방식
AOP Proxy
    AOP 기능을 구현하기 위해 만든 프록시 객체, Spring에서 AOP Proxy는 JDK 동적 프록시 또는 CGLIB 프록시를 사용한다.



















