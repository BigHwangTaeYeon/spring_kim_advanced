package hello.advanced.app.v2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.advanced.app.v3.OrderServiceV3;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV2;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderControllerV2 {
    
    private final OrderServiceV3 orderService;
    private final HelloTraceV2 trace;

    @GetMapping("/v2/request")
    public String request(String itemId) {

        // TraceStatus status = trace.begin("OrderController.request()");
        TraceStatus status = null;

        try{
            status = trace.begin("OrderControllerV2.request()");
            orderService.orderItem(status.getTraceId(), itemId);
            trace.end(status);
            return "ok";
        } catch (Exception e ){
            trace.exception(status, e);
            throw e;    // 예외를 꼭 다시 던져주어야 한다.
            //http://localhost:8080/v1/request?itemId=ex    ex를 예외처리로 로직을 구현했음
        }

        // return "ok";
    }

}
