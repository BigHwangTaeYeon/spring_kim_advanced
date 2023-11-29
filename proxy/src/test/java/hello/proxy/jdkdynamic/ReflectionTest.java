package hello.proxy.jdkdynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectionTest {
    @Test
    void reflection0() {
        Hello target = new Hello();

        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); // 호출하는 메서드가 다름
        log.info("result = {}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB(); // 호출하는 메서드가 다름
        log.info("result = {}", result2);
        //공통 로직2 종료
    }
    
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

    @Test
    void reflection2() throws Exception {
        // class info
        Class classHeloo = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        // CallA info - Method로 추상화시키면서 공통화를 시킨다.
        Method methodCallA = classHeloo.getMethod("callA");
        dynamicCall(methodCallA, target);

        // CallB info
        Method methodCallB = classHeloo.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        log.info("start");

        Object result1 = method.invoke(target); // 호출하는 메서드가 다름

        log.info("result = {}", result1);
    }

    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
