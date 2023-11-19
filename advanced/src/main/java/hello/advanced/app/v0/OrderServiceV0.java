package hello.advanced.app.v0;

import org.springframework.stereotype.Service;

import hello.advanced.app.v1.OrderRepositoryV1;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV0 {

    private final OrderRepositoryV1 orderRepository;

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }

}
