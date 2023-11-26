package hello.proxy.trace.logtrace.template;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

public abstract class AbstractTemplate<T> {
    private final LogTrace trace;

    public AbstractTemplate(LogTrace trace) {
        this.trace = trace;
    }
    
    // 반환 타입이 void일 때도 String일 때도 있기에 제네릭 T로 지정
    public T execute(String message){
        TraceStatus status = null;
        try {
            status = trace.begin(message);
            
            // 로직 호출
            T result = call();

            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }

    protected abstract T call();
}
