package hello.proxy.config.v1_proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.proxy.app.v1.OrderControllerV1;
import hello.proxy.app.v1.OrderControllerV1Impl;
import hello.proxy.app.v1.OrderRepositoryV1;
import hello.proxy.app.v1.OrderRepositoryV1Impl;
import hello.proxy.app.v1.OrderServiceV1;
import hello.proxy.app.v1.OrderServiceV1Impl;
import hello.proxy.config.v1_proxy.interface_proxy.OrderControllerInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderRepositoryInterfaceProxy;
import hello.proxy.config.v1_proxy.interface_proxy.OrderServiceInterfaceProxy;
import hello.proxy.trace.logtrace.LogTrace;

@Configuration
public class InterfaceProxyConfig {
    @Bean
    public OrderControllerV1 orderController(LogTrace logTrace) {
        // 구현체
        OrderControllerV1Impl controllerImpl = new OrderControllerV1Impl(orderService(logTrace));
        // Proxy 반환
        return new OrderControllerInterfaceProxy(controllerImpl, logTrace);
        // return new OrderControllerV1Impl(orderService());
    }
    @Bean
    public OrderServiceV1 orderService(LogTrace logTrace) {
        // 구현체
        OrderServiceV1Impl serviceImpl = new OrderServiceV1Impl(orderRepository(logTrace));
        // Proxy 반환
        return new OrderServiceInterfaceProxy(serviceImpl, logTrace);
        // return new OrderServiceV1Impl(orderRepository());
    }
    @Bean
    public OrderRepositoryV1 orderRepository(LogTrace logTrace) {
        // 구현체
        OrderRepositoryV1Impl repositoryImpl = new OrderRepositoryV1Impl();
        // Proxy 반환
        return new OrderRepositoryInterfaceProxy(repositoryImpl, logTrace);
        // return new OrderRepositoryV1Impl();
    }
}
