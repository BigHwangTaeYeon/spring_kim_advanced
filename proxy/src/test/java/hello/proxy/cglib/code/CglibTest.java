package hello.proxy.cglib.code;

import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CglibTest {
    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService) enhancer.create(); // proxy 생성
        log.info("targetClass = {}", target.getClass());
        log.info("ProxyClass = {}", proxy.getClass());

        proxy.call();
    }
}
