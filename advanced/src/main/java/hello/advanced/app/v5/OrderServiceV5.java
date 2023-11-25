package hello.advanced.app.v5;

import org.springframework.stereotype.Service;

import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;

@Service
public class OrderServiceV5 {

    private final OrderRepositoryV5 orderRepository;
    private final TraceTemplate template;
    
    public OrderServiceV5(OrderRepositoryV5 orderRepository, LogTrace logTrace) {
        this.orderRepository = orderRepository;
        this.template = new TraceTemplate(logTrace);
    }

    public void orderItem(String itemId) {
        // template.execute("OrderServiceV4.orderItem()", new TraceCallback<Void>() {
        //     @Override
        //     public Void call() {
        //         orderRepository.save(itemId);
        //         return null;
        //     }
        // });
        template.execute("OrderServiceV4.orderItem()", () -> {
            orderRepository.save(itemId);
            return null;
        });
    }

}
