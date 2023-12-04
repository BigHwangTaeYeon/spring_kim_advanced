# 동적Proxy

자바가 제공하는 JDK 동적 프록시 기술이나 CGLIB 같은 프록시 생성 오픈소스 기술을 활요하면
프록시 객체를 동적으로 만들 수 있다.
프록시 적용할 코드를 하나만 만들어두고, 동적 프록시 기술을 사용하여 프록시 객체를 찍어낸다.

JDK 동적 프록시를 이해하기 위해 자바의 리플렉션 기술을 이해해야한다.
리플렉션 기술을 사용하면 클래스나, 메서드의 메타정보를 동적으로 획득하고, 코드도 동적으로 호출 가능하다.

```java
    @Test
    void reflection0() {
        Hello target = new Hello();

        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA();            // 호출하는 메서드가 다름
        log.info("result = {}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB();            // 호출하는 메서드가 다름
        log.info("result = {}", result2);
        //공통 로직2 종료
    }
```
위와같은 상황에 리플렉션을 사용한다.
리플렉션은 클래스나 메서드의 메타정보를 사용하여 동적으로 호출하는 메서드를 변경할 수 있다.
* 참고로 람다를 사용해서 공통화 하는 것도 가능하다.

```java
    @Test
    void reflection1() throws Exception {
        // class info
        Class classHeloo = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        // CallA info
        Method methodCallA = classHeloo.getMethod("callA");
        Object result1 = methodCallA.invoke(target);
        log.info("result = {}", result1);

        // CallB info
        Method methodCallB = classHeloo.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("result = {}", result2);
    }
```
CallA(), CallB() 메서드를 직접 호출하는 부분이 Method로 대체되었다.
이로인해 공통 로직을 만들 수 있게 되었다.

```java
        // CallB info
        Method methodCallB = classHeloo.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        log.info("start");

        Object result1 = method.invoke(target); // 호출하는 메서드가 다름

        log.info("result = {}", result1);
    }
```

어플리케이션을 유연하게 만들어주지만,
하지만 리플렉션은 가급적 사용하지 말자.
런타임 시점에 실행되기에 컴파일 시점의 문제를 확인할 수 없다.
(서비스 배포 다 되고, 버튼눌렀을 때 오류가 뜨는 경우이다)
    런타임 오류
        classHeloo.getMethod("callBbbbb");
    컴파일 오류
        classHeloo.getMethoddddd("callB");




config파일에서 빈 생성할 때 패턴을 정의하여 생성자에 주입해준다.
```java
import java.lang.reflect.Proxy;
public class DynamicProxyFilterConfig {
    private static final String[] PATTERNS = { "request*", "order*", "save*" };
}

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
public class LogTraceFilterHandler
// save, request, reqe*, *est ...
// PatternMathchUtils spring에서 제공(org.springframework.util.PatternMatchUtils)
if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
    return method.invoke(target, args);
}
```
handler에서 패턴을 받아 스프링에서 제공하는 PatternMatchUtils를 사용하여 검사하고 invoke를 호출하여 메소드를 실행해준다.
이렇게 함으로써, no-log 주소에서는 로그가 남지 않는다.
(JDK에서 제공하는 동적 프록시 사용)


```java
TimeInvocationHandler handler = new TimeInvocationHandler(target);

// Object proxy = Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);
AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);
proxy.call();
```
위 처럼 TimeInvocationHandler를 사용하여 따로 Proxy객체를 만들어주지 않고 JDK 동적 프록시를 사용해서 동적으로 만들고 TimeInvocationHandler를 공통으로 사용했다.


=========================================================================================================

CGLIB
외부 라이브러리였는데 스프링 내부 소스 코드에 포함시켰다.
직접 사용할 경우는 거의 없다.
개념만 잡자

CGLIB는 Enhancer를 사용해서 프록시를 생성한다.

enhancer.setSuperclass(ConcreteService.class)
구체클래스를 상속받아 프록시를 생성할 수 있다.

프록시 생성 후 참조 Class
hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$25d6b0e3

참고로 JDK 프록시는
proxyClass=class com.sun.proxy.$Proxy1



=========================================================================================================

정리

reflection
class와 method의 메타정보를 얻고 동적으로 바꿔가며 호출할 수 있다.

JDK 동적 프록시
proxy를 Class로 따로 만들어 사용하지 않고 편리하게 InvocationHandler를 상속받아 사용할 수 있다.
 + 추가 메서드 이름 필터 (no-log)

CGLIB
구체클래스를 상속받아 사용할 수 있다.


