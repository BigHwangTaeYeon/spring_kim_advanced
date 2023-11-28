package hello.proxy.app.v2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping 
// @Controller를 사용하면 Component scan 대상이 되고 @RequestMapping을 사용하면 대상이 되지않는다.
// 이미 Application class에서 scan 대상을 지정해주었기 때문에 이렇게 사용한다.
@ResponseBody
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;
    
    public OrderControllerV2(OrderServiceV2 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v2/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "ok";
    }
    
}
