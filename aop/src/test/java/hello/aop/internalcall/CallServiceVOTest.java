package hello.aop.internalcall;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import hello.aop.internalcall.aop.CallLogAspect;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(CallLogAspect.class)
@SpringBootTest
public class CallServiceVOTest {
    @Autowired
    CallServiceVO callServiceVO;

    @Test
    void external() {
        callServiceVO.external();
        // aop = void hello.aop.internalcall.CallServiceVO.external()
        // hello.aop.internalcall.CallServiceVO     : call external
        // hello.aop.internalcall.CallServiceVO     : call internal
        // 여기서 external 호출 시, AOP 적용되지만 external() 안에서 internal() 호출 시, AOP가 적용되지 않았다.
    }

    @Test
    void internal() {
        callServiceVO.internal();
        
    }
}
