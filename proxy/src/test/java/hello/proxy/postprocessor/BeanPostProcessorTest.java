package hello.proxy.postprocessor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanPostProcessorTest {
    @Test
    void basicTest() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BeanPostProcessorConfig.class);

        // A는 빈으로 등록
        // A a = applicationContext.getBean("beanA", A.class);
        // a.helloA();

        // beanA 이름으로 B 객체가 빈으로 등록된다.
        B b = applicationContext.getBean("beanA", B.class);
        b.helloB();
        // beanName = beanA, bean = hello.proxy.postprocessor.BeanPostProcessorTest$A@4f2b503c
        // 12:06:02.990 [main] INFO hello.proxy.postprocessor.BeanPostProcessorTest$B - hello B
        // beanName은 beanA를 가져오고 실행하면 B를 실행한다.

        // B는 빈으로 등록되지 않았다.
        Assertions.assertThrows(NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("beanB", B.class));

    }

    @Slf4j
    @Configuration
    // static class BasicConfig {
    static class BeanPostProcessorConfig {
        @Bean(name = "beanA")
        public A a() {
            return new A();
        }
        @Bean   // Bean 생성에 우선권이 있다.
        public AToBPostProcessor helloPostProcessor() {
            return new AToBPostProcessor();
        }
    }
    @Slf4j
    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    @Slf4j
    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }
    
    @Slf4j
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
}
