# SpringAOP실무주의사항

1. Proxy와 내부 호출 문제
    Spring은 Proxy 방식의 AOP를 사용한다.
    프록시를 통해 대상 객체(Target)을 호출해야 한다.
    그래야 먼저 Advice를 호출하고, 이후에 대상 객체를 호출한다.
    * 프록시를 거치지 않고 대상 객체를 직접 호출하면 AOP가 적용되지 않고, 어드바이스도 호출되지 않는다. *

    AOP를 적용하면 Spring은 대상 객체 대신, Proxy를 Bean으로 등록한다.
    DI 시, 프록시 객체를 주입한다.
    *** 대상 객체의 내부에서 메서드 호출이 발생하면 프록시를 거치지 않고 대상 객체를 직접 호출하는 문제가 발생한다. ***
    실무에서 한번쯤은 고생하는 문제이니 이해하고 넘어갑시다.

    ```java
    @Test
    void external() {
        callServiceVO.external();
        // aop = void hello.aop.internalcall.CallServiceVO.external()
        // hello.aop.internalcall.CallServiceVO     : call external
        // hello.aop.internalcall.CallServiceVO     : call internal
        // 여기서 external 호출 시, AOP 적용되지만 external() 안에서 internal() 호출 시, AOP가 적용되지 않았다.
    }
    ```
    callServiceVO.external() 실행 시, 클라이언트에서 프록시를 호출하게 된다.
    CallLogAspect 어드바이스가 호출된 것을 확인할 수 있다. // aop = void hello.aop.internalcall.CallServiceVO.external()
    그리고 AOP Proxy는 target.external()을 호출한다.
    이때는 CallLogAspect 어드바이스가 호출되지 않는다.

    자바 언어에서는 메서드 앞에 별도 참조가 없으면 this라는 뜻으로 자기 자신의 인스턴스를 가리킨다.
    target.external()은 this.internal()을 뜻하게 되는데, 실제 대상 객체(target)의 인스턴스를 뜻한다.
    결과적으로 내부 호출은 프록시를 거치지 않는다.
    따라서 어드바이스도 적용할 수 없다.

    반면
    ```java
    @Test
    void internal() {
        callServiceVO.internal();
    }
    ```
    aop = void hello.aop.internalcall.CallServiceVO.internal()
    call internal
    너무나도 당연하게 외부에서 internal()를 호출하면 프록시를 거친다.

    Spring은 Proxy 방식으로 AOP를 구현하기에 이러한 한계가 있다.

    AOP를 직접 적용하는 AspectJ를 사용하면 이러한 문제가 발생하지 않는다.
    Proxy를 통하는 것이 아닌, 해당 코드에 직접 AOP 적용 코드가 붙어 있기 때문에 내부 호출과 무관하게 AOP를 적용할 수 있다.
    하지만 로드 타임 위빙 등을 사용해야 하는데, 설정도 복잡하고 JVM옵션을 주어야 한다는 부담이 있다.
    그리고 프록시 방식의 AOP에서 내부 호출에 대응할 수 있는 대안들도 있다.

    이러한 이유로 AspectJ를 직접 실무에서 사용하지는 않는다.

    ```java
    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class CallServiceV3 {
        private final InternalService internalService;
        public void external() {
            log.info("call external");
            internalService.internal(); //외부 메서드 호출
        }
    }
    @Slf4j
    @Component
    public class InternalService {
        public void internal() {
            log.info("call internal");
        }
    }
    ```

    내부 호출 자체가 사라지고, callService > internalService를 호출하는 구조로 변경되었다.
    이렇게 구조를 변경시킴으로써, 자연스럽게 AOP를 적용시킨다.

    인터페이스에 메서드가 나올 정도의 규모에 AOP를 적용하는 것이 적당하다.
    AOP는 public 메서드에만 적용한다.
    private 메서드처럼 작은 단위에는 AOP를 적용하지 않는다.

    여기서
    첫번째 대안 자기 자신 주입
    두번째 대안 지연 조회
    세번째 대안 구조 변경을 배웠다.
    가장 실용적인 것은 세번째, 구조 변경이다.
    더 깊게 AOP 활용에 문제가 되는 것은 한계로 보고, 다른 방법을 고안해 보는 것이 좋을 것 같다.

2. Proxy 기술의 한계

*** JDK 동적 프록시 한계 ***
    타입 캐스팅
    JDK 동적 프록시와 CGLIB를 사용해서 AOP 프록시를 만드는 방법의 장단점이 있다.
    JDK 동적 프록시는 인터페이스가 필수이고, 인터페이스 기반으로 프록시를 생성한다.
    CGLIB는 구체 클래스를 기반으로 프록시를 생성한다.

    스프링이 프록시를 만들때 제공하는 ProxyFactory에 proxyTargetClass 옵션에 따라 둘중 하나를 선택해서 프록시를 만들 수 있다.

    proxyTargetClass=false JDK 동적 프록시, 인터페이스 기반 프록시 생성
    proxyTargetClass=true CGLIB를 사용하여 구체 클래스 기반 프록시 생성
    옵션과 무관하게 인터페이스가 없으면 JDK 적용못하고 CGLIB 사용

    JDK Proxy는 MemberService interface를 구현한거지, MemberServiceImpl은 존재자체도 모른다.
    JDK Proxy는 Interface를 기반으로 프록시를 생성하기 때문이다.
    CGLIB는 구현 클래스로 Proxy를 생성하기 때문에 casting 성공

    @SpringBootTest(properties = {"spring.aop.proxy-target-class=false"})   // JDK 동적 프록시 적용
    BeanNotOfRequiredTypeException: Bean named 'memberServiceImpl' is expected to be of type 'hello.aop.member.MemberServiceImpl' but was actually of type 'jdk.proxy2.$Proxy50'
    memberServiceImpl에 주입되길 기대하는 타입은 hello.aop.member.MemberServiceImpl이지만 실제 넘어온 타입은 jdk.proxy2.$Proxy50이기에 타입 예외가 발생한다.

    MemberService memberService;
    MemberServiceImpl memberServiceImpl;
    JDK의 Proxy는 interface를 구현하고 구현 클래스는 모른다.

    @SpringBootTest(properties = {"spring.aop.proxy-target-class=true"})   // CGLIB 동적 프록시 적용
    CGLIB Proxy는 MemberServiceImpl인 구현 클레스를 기반으로 만들기 때문에 부모인 MemberService interface까지 사용이 가능하다.

*** CGLIB 동적 프록시 한계 ***
    CGLIB는 구체 클래스를 상속받기에 문제가 있다.
    1. 대상 클래스에 기본 생성자 필수
        CGLIB는 자바 언어에서 상속 받으면, 자식 클래스의 생성자를 호출할 때 자식 클래스의 생성자에서 부모 클래스의 생성자도 호출해야한다.
        (이 부분이 생략되어 있다면, 자식 클래스의 생성자 첫줄에 부모 클래스의 기본 생성자를 호출하는 super()가 자동으로 들어간다.)
        CGLIB를 사용할 때 CGLIB가 만드는 프록시의 생성자는 우리가 호출하는 것이 아니다.
        CGLIB 프록시는 대상 클래스를 상속 받고, 생성자에서 대상 클래스의 기본 생성자를 호출한다.
        따라서 대상 클래스에 기본 생성자를 만들어야 한다.
        (기본 생성자는 파라미터가 하나도 없는 생성자를 뜻한다. 생성자가 하나도 없으면 자동으로 만들어진다.)

    2. 생성자 2번 호출 문제
        CGLIB는 구체 클래스를 상속 받는다.
        자바 언어에서 상속을 받으면 자식 클래스의 생성자를 호출할 때 부모 클래스의 생성자도 호출해야한다.
            1) 실제 target의 객체를 생성할 때
            2) 프록시 객체를 생성할 때 부모 클래스의 생성자 호출 (Proxy에서 target을 매개변수로 담아 생성하니까)
                MemberServiceImpl target = new MemberServiceImpl();
                ProxyFactory proxyFactory = new ProxyFactory(target);

    3. final 키워드 클래스, 메서드 사용 불가 (사실 크게 문제가 되지 않는다. 일반 프로그래밍할 때 잘 사용 안하기 때문에.)
        final 키워드가 있으면 상속이 불가능하고 메서드에 있으면 오버라이딩이 불가능하다.

*** 스프링의 해결책 ***
스프링 3.2, CGLIB를 스프링 내부에 함께 패키징
    CGLIB를 사용하려면 CGLIB 라이브러리가 별도로 필요했다.
    스프링은 CGLIB 라이브러리를 스프링 내부에 함께 패키징해서 별도의 라이브러리 추가 없이 CGLIB를 사용할 수 있게 되었다.
    `CGLIB spring-core org.springframework`

CGLIB 기본 생성자 필수 문제 해결
    스프링 4.0부터 CGLIB의 기본 생성자가 필수인 문제가 해결되었다.
    `objenesis`라는 특별한 라이브러리를 사용해서 기본 생성자 없이 객체 생성이 가능하다.
    참고로 이 라이브러리는 생성자 호출 없이 객체를 생성할 수 있게 해준다.

생성자 2번 호출 문제
    스프링 4.0부터 해결되었다.
    이역시 `objenesis`라는 특별한 라이브러리 덕분에 가능해졌다.
    이제 생성자가 1번만 호출된다.

스프링 부트 2.0-CGLIB 기본 사용
    부트 2.0부터 CGLIB를 기본으로 사용하도록 했다. 이렇게 해서 구체 클래스 타입으로 의존관계를 주입하는 문제를 해결했다.
    스프링 부트는 별도의 설정이 없다면 AOP를 적용할 때 기본적으로 proxyTargetClass=true로 설정해서 사용한다.
    따라서 Interface가 있어도 JDK가 아닌 CGLIB를 사용해서 구체클래스를 기반으로 프록시를 생성한다.
    물론 application.properties에서 spring.aop.proxy-target-class=false로 JDK 동적 프록시 생성이 가능하다.







