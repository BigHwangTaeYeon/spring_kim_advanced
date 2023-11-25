package hello.advanced.trace.strategy.code;

import org.junit.jupiter.api.Test;

import hello.advanced.trace.strategy.code.strategy.ContextV2;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextV2Test {
    /*
     * 전략 패턴 사용
     * 실행할 때 인수로 던져준다.
     */
    @Test
    void strategyV1() {
        ContextV2 context = new ContextV2();
        context.execute(new StrategyLogic1());
        context.execute(new StrategyLogic2());

    }
    /*
     * 전략 패턴 익명 내부 클래스
     */
    @Test
    void strategyV2() {
        ContextV2 context = new ContextV2();
        context.execute(new StrategyLogic1(){
            @Override
            public void call(){
                log.info("비즈니스 로직 1 실행 익명 내부 클래스");
            }
        });


        context.execute(new StrategyLogic2(){
            @Override
            public void call(){
                log.info("비즈니스 로직 2 실행 익명 내부 클래스");
            }
        });

    }
    /*
     * 전략 패턴 익명 람다
     */
    @Test
    void strategyV3() {
        ContextV2 context = new ContextV2();
        context.execute(() -> log.info("비즈니스 로직 1 실행 람다"));
        context.execute(() -> log.info("비즈니스 로직 2 실행 람다"));
    }
}
