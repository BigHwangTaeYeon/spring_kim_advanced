package hello.aop.exam;

import org.springframework.stereotype.Repository;

import hello.aop.exam.annotation.Retry;
import hello.aop.exam.annotation.Trace;

@Repository
public class ExamRepository {
    private static int seq = 0;

    /*
     * 5번에 1번 실패하는 요청
     */
    @Trace
    @Retry(4) // 조심해야 할 것이, 횟수 지정해야한다. 아니면 계속 오류를 던지기 때문에 문제가 된다.
    //  @Retry(4) default로 3이 지정되어있지만 4를 강제로 넣을 수 있다.
    public String save(String itemId) {
        seq++;
        if (seq % 5 == 0) {
            throw new IllegalStateException("예외 발생");
        }
        return "ok";
    }
}
