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

타입 캐스팅
JDK 동적 프록시와 CGLIB를 사용해서 AOP 프록시를 만드는 방법의 장단점이 있다.
JDK 동적 프록시는 인터페이스가 필수이고, 인터페이스 기반으로 프록시를 생성한다.
CGLIB는 구체 클래스를 기반으로 프록시를 생성한다.

스프링이 프록시를 만들때 제공하는 ProxyFactory에 proxyTargetClass 옵션에 따라 둘중 하나를 선택해서 프록시를 만들 수 있다.

proxyTargetClass=false JDK 동적 프록시, 인터페이스 기반 프록시 생성
proxyTargetClass=true CGLIB를 사용하여 구체 클래스 기반 프록시 생성
옵션과 무관하게 인터페이스가 없으면 JDK 적용못하고 CGLIB 사용

