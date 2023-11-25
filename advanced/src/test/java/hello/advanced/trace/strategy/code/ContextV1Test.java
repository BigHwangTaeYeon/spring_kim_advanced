package hello.advanced.trace.strategy.code;

import org.junit.jupiter.api.Test;

import hello.advanced.trace.strategy.code.strategy.ContextV1;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextV1Test {
    @Test
    void templateMethodVO() {
        logic1();
        logic2();
    }

    private void logic1() {
        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");
        // 비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultType = {}" , resultTime);
    }

    private void logic2() {
        long startTime = System.currentTimeMillis();
        // 비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        // 비즈니스 로직 종료
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("resultType = {}" , resultTime);
    }

    /*
     * 전략 패턴 사용
     */
    @Test
    void strategyV1() {
        StrategyLogic1 strategyLogic1 = new StrategyLogic1();
        ContextV1 context1 = new ContextV1(strategyLogic1);
        context1.execute();

        StrategyLogic2 strategyLogic2 = new StrategyLogic2();
        ContextV1 context2 = new ContextV1(strategyLogic2);
        context2.execute();
    }

    @Test
    void strategyV2() {
        Strategy strategy1 = new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 1 실행 ~");
            }
        };
        ContextV1 context1 = new ContextV1(strategy1);
        log.info("strategyLogic1= {}", strategy1.getClass());
        context1.execute();
        Strategy strategy2 = new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 1 실행 ~");
            }
        };
        ContextV1 context2 = new ContextV1(strategy2);
        log.info("strategyLogic2= {}", strategy2.getClass());
        context2.execute();
    }

    @Test
    void strategyV3() {
        ContextV1 context1 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 1 실행 ~");
            }
        });

        context1.execute();

        ContextV1 context2 = new ContextV1(new Strategy() {
            @Override
            public void call() {
                log.info("비즈니스 로직 2 실행 ~");
            }
        });

        context2.execute();
    }

    // 람다로 실행하기 위해서는 interface에 메소드가 하나여야한다. ( call() )
    @Test
    void strategyV4() {
        ContextV1 contextx1 = new ContextV1(() -> log.info("비즈니스로직1 실행 람다."));
        contextx1.execute();

        ContextV1 contextx2 = new ContextV1(() -> log.info("비즈니스로직2 실행 람다."));
        contextx2.execute();
    }

}
