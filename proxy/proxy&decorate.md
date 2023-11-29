# proxy&decorate

클라이언트, 서버 광범위한 의미로 사용
클라이언트 : 요청 - 요청을 하는 객체
서버 : 요청을 처리 - 요청을 처리하는 객체

Client  >   Proxy1  >   Proxy2  >   Server
                프록시 체인

객체에서 프록시가 되려면, 클라이언트는 서버에게 요청을 한 것인지 프록시에게 요청을 한 것인지 몰라야한다.
서버와 프록시는 같은 인터페이스를 사용해야한다.
클라이언트가 사용하는 서버 객체를 프록시 객체로 변경해도 클라이언트 코드를 변경하지 않고 동작할 수 있어야 한다.

        Client    >     ServerInterface
    
                      Proxy       Server

런타임 객체 의존 관계 - 프록시 도입 전

    Client      >       Server

런타임 객체 의존 관계 - 프록시 도입 후

    Client  >   Proxy   >   Server

런타임(어플리케이션 실행 시점)에 클라이언트 객체에 DI를 사용하여 객체 의존관계를 변경해도 클라이언트 코드를 변경하지 않아도 된다.

GOF 디자인 패턴
의도(intent)에 따라 프록시 패턴과 데코레이터 패턴으로 구분된다.
프록시 패턴 : 접근 제어가 목적 (다른 객체에 대한 접근 제어를 위한 대리자를 제공)
데코레이터 패턴 : 새로운 기능 추가가 목적 (객체에 추가 책임(기능)을 동적으로 추가하고 확장을 위한 유연한 대안 제공)

Decorator Pattern은 내부에 호출 대상인 Component를 가지고 있어야 한다.
```java
    class TimeDecorator {
        private Component component;
        
        public TimeDecorator(Component component) {
            this.component = component;
        }
    }
    class MessageDecorator {
        private Component component;
        
        public MessageDecorator(Component component) {
            this.component = component;
        }
    }
```
위 와 같이 공통 부분을 추상클래스로 만들어 사용도 가능하다.

V1 interface
V2 class
V3 Component scan


V2, 클레스 기반 프록시의 단점
```java
public OrderServiceConcreteProxy(OrderRepositoryV2 orderRepository, OrderServiceV2 target, LogTrace logTrace) {
    // Proxy로 사용하기에 상위 데이터를 가져올 필요가 없으므로 null값으로 정의한다.
    super(null);
    this.target = target;
    this.logTrace = logTrace;
}

class ConcreteProxyConfig
    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 controllerV2 = new OrderControllerV2(OrderServiceV2(logTrace));
        return new OrderControllerConcreteProxy(controllerV2, logTrace);
    }
```

ConcreteProxyConfig class에서 위와같이 DI를 하기에 OrderServiceConcreteProxy 생성자에 super를 null 값으로 주입한다.
OrderControllerV2인 본체는 생성자를 받기에 상속받은 class에서 생성자에 super 부모의 값을 가져오게 되어있다.

