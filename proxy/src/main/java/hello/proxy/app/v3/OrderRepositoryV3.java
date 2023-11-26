package hello.proxy.app.v3;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryV3 {
    public void save(String itemId) {
        // 저장로직
        if(itemId.equals("ex")){
            throw new IllegalStateException("예외 발생");
        }
        sleep(1000);
    }
    private void sleep (int mill) {
        try {
            Thread.sleep(mill);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
