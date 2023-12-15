package hello.aop.internalcall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;

/*
 * 객체를 스프링 컨테이너에서 조회하는 것을 스프링 빈 생성 시점이 아니라 실제 객체를 사용하는 시점으로 지연할 수 있다.
 * callServiceProvider.getObject()를 호출하는 시점에 스프링 컨테이너에서 빈을 조회한다.
 * 여기서는 자기 자신을 주입받는 것이 아니기에 순환 사이클이 발생하지 않는다.
 */
@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest
public class CallServiceV3Test {

    @Autowired CallServiceV3 callServiceV3;

    @Test
    void external() {
        callServiceV3.external();
    }

}
