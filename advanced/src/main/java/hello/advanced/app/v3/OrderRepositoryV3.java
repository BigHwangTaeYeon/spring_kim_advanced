package hello.advanced.app.v3;

import org.springframework.stereotype.Repository;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV3 {

    private final LogTrace trace;

    public void save(TraceId traceId, String itemId) {
        
        // Trace 추가
        TraceStatus status = null;

        try{
            status = trace.begin("OrderRepositoryV3.save()");
            //저장로직
            if (itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생");
            }
            sleep(1000);
            trace.end(status);
        } catch (Exception e ){
            trace.exception(status, e);
            throw e;
        }

        // 기존 저장로직
        // if (itemId.equals("ex")) {
        //     throw new IllegalStateException("예외 발생");
        // }
        // sleep(1000);
    }
    
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
