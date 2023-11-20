package hello.advanced.app.v3;

import org.springframework.stereotype.Service;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV3 {

    private final OrderRepositoryV3 orderRepository;
    private final LogTrace trace;

    public void orderItem(TraceId traceId, String itemId) {
        
        // 기존 로직
        // orderRepository.save(itemId);

        // Trace 추가
        TraceStatus status = null;

        try{
            status = trace.begin("OrderServiceV3.orderItem()");
            orderRepository.save(status.getTraceId(), itemId);
            trace.end(status);
        } catch (Exception e ){
            trace.exception(status, e);
            throw e;
        }

    }

}
