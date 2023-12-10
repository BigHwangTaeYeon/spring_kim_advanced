# Bean후처리기

@Bean 이나 ComponentScan으로 빈을 등록하면
스프링은 대상 객체를 생성하고, IOC 컨테이너 내부의 빈 저장소에 등록한다.
이후에는 등록한 빈을 IOC 컨테이너를 통해 조회하여 사용하면 된다.

### 빈 후처리기 - BeanPostProcessor
빈 저장소에 등록하기 직전에 조작하기 위해 사용
BeanPostProcessor는 빈을 생성한 후에 무언가를 처리하는 용도로 사용
    기능
        - 객체를 조작할 수 있고, 다른 객체로 바꾸는 것도 가능하다.
    과정
        1. 생성 : @Bean
        2. 전달 : 생성된 객체를 빈 저장소에 등록하기 직전에 후처리기에 전달
        3. 작업 : 빈 객체를 조작하거나 객체를 바꿀 수 있다.
        4. 등록 : 빈 후처리기는 빈을 반환한다. 그러면 그대로 빈이 등록되고, 바꾸면 다른 객체가 빈 저장소에 등록된다.

BeanPostProcessor인터페이스를 구현하고 빈으로 등록.
postProcessBeforeInitialization : 객체 생성 이후, @PostConstruct같은 초기화가 발생하기 *** 전에 *** 호출되는 포스트 프로세서.
postProcessAfterInitialization : 객체 생성 이후, @PostConstruct같은 초기화가 발생한 *** 다음에 *** 호출되는 포스트 프로세서.

```java
    static class AToBPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            log.info("beanName = {}, bean = {}", beanName, bean);
            if (bean instanceof A) {
                return new B();
            }
		    return bean;
	    }
    }
```

### 정리

ProxyFactoryConfigV1, V2 등 반복되는 코드 등으로 프록시 관련 설정이 지나치게 많다는 문제가 있다.
Bean을 편리하게 하려고 ComponentScan을 사용하지만 프록시를 적용하는 코드까지 빈 생성 코드에 넣어야 했다.

Application V3 처럼 ComponentScan을 사용하면 프록시 적용이 불가했다.
IOC에서 실제 객체를 빈으로 등록해버린 상태이기 때문이다.
ProxyFactoryConfigV1에서는 IOC에 등록하는 코드를 작성했고,
ComponentScan은 자동으로 빈을 등록하기에 프록시 적용이 불가했다.

빈 후처리기로 프록시를 생성하는 부분을 하나로 집중할 수 있다.
ProxyFactoryConfigV1처럼 Repository, Service, Controller 각각 적용해주거나 여러 Bean을 각각 잡아주지않고
자동등록되는 ComponentScan까지 조작할 수 있도록 또한 적용할 패키지 적용하지 않을 패키지를 지정하여 프록시를 적용할 수 있었다.

*** 스프링은 프록시 생성을 위한 빈 후처리기를 이미 만들어 제공한다. ***

포인트컷을 사용하면 더 깔끔하다.
포인트컷은 클래스, 메서드 단위로 필터링이 가능하기에 적용 대상 여부를 더욱 정밀하게 설정할 수 있다.
(Advisor는 포인트컷을 가지고 있다. advisor를 통해 포인트컷을 확인 할 수 있다.)
AOP 포인트컷을 사용하여 적용대상여부를 체크한다.

포인트컷
    1. 프록시 적용 대상 여부를 체크하여 필요한 곳만 적용한다.(빈 후처리기 - 자동 프록시 생성) - Class
    2. 프록시의 어떤 메서드가 호출 되었을 때, 어드바이스를 적용할 지 판단한다.(프록시 내부) - Method


implementation('org.springframework.boot:spring-boot-starter-aop')
AnnotationAwareAspectJAutoProxyCreator(자동프록시생성기)라는 빈 후처리기가 스프링 빈에 자동으로 등록된다.
@AspectJ와 관련된 AOP 기능도 자동으로 찾아 처리해준다.

### Pointcut 2가지에 사용된다.
1. 프록시 적용 여부 판단 - 생성 단계
    객체가 pointcut 판단에 적용 대상이면 프록시 객체를 생성한다.
2. 어드바이스 적용 여부 판단 - 사용 단계
    프록시 호출 시, 부가 기능인 어드바이스를 적용할 지 pointcut으로 판단한다.

*** AspectJExpressionPointcut ***
더욱 정밀한 포인트컷 세팅

```java
NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
pointcut.setMappedNames("request*", "order*", "save*");
```
위와 같이 포인트컷을 세팅하면
서버만 올려도
AppV1Config.orderControllerV1()
|--> AppV1Config.orderServiceV1()
|    |--> AppV1Config.orderRepositoryV1()
|    |<-- AppV1Config.orderRepositoryV1() time=1ms
|<-- AppV1Config.orderServiceV1() time=13ms
AppV1Config.orderControllerV1() time=29ms

AppV1Config가 시작으로 된다.

```java
@Bean
public Advisor advisor2(LogTrace logTrace) {
    //pointcut
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    // app.. - app 하위의 모든 패키지, (..) 파라미터에 상관 없다.
    pointcut.setExpression("execution(* hello.proxy.app..*(..))");
    
    //...
}
```
위와같이 AspectJExpressionPointcut을 사용하여 세팅해주면
OrderControllerV3.request()
|--> OrderServiceV3.orderItem()
|    |--> OrderRepositoryV3.save()
|    |<-- OrderRepositoryV3.save() time=1029ms
|<-- OrderServiceV3.orderItem() time=1040ms
OrderControllerV3.request() time=1060ms  

의도대로 정상적으로 나온다.

OrderControllerV3.noLog()
OrderControllerV3.noLog() time=9ms   

위에 처럼 nolog가 나오던 것을
```java
pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");
```
표현식에 부정으로 추가해주면, 나오지 않는다.



### 하나의 프록시, 여러 Advisor 적용
프록시 자동 생성기는 프록시 하나만 생성한다.
프록시 내부에 여러 advisor들을 포함할 수 있기에 여러개를 생성하여 비용을 낭비할 이유가 없다.




























