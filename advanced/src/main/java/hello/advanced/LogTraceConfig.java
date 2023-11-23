package hello.advanced;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.logtrace.ThreadLocalLogTrace;

@Configuration
public class LogTraceConfig {
    // @Bean
    // public LogTrace logTrace(){
    //     return new FieldLogTrace();
    // }
        // 동시성 문제가 해결된 생성자를 넣어준다.
    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
