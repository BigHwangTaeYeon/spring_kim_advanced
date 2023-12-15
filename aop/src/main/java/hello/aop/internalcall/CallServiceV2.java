package hello.aop.internalcall;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/*
 *  생성자 주입은 순환 사이클을 만들기 때문에 실패한다.
 *
 *  주의 : 스프링 부트 2.6부터는 순환 참조를 기본적으로 금지하도록 정책이 변경 되어 위의 코드는 별도의 설정을 추가하지 않으면 정상 실행되지 않는다.
 */
@Slf4j
@Component
public class CallServiceV2 {
    // private ApplicationContext applicationContext;
    // Application을 사용해서 현재 하려는 지연해서 callService를 호출하는 거에 비해 너무 과하다.
    private final ObjectProvider<CallServiceV2> callServiceProvider;

    public CallServiceV2(ObjectProvider<CallServiceV2> callServiceProvider) {
        // this.applicationContext = applicationContext;
        this.callServiceProvider = callServiceProvider;
    }

    public void external() {
        log.info("call external");
        // CallServiceV2 callServiceV2 = applicationContext.getBean(CallServiceV2.class);
        CallServiceV2 callServiceV2 = callServiceProvider.getObject();
        callServiceV2.internal(); //외부 메서드 호출
    }
    
    public void internal() {
        log.info("call internal");
    }
}