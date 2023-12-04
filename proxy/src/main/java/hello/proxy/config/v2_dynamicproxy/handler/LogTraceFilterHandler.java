package hello.proxy.config.v2_dynamicproxy.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.util.PatternMatchUtils;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

public class LogTraceFilterHandler implements InvocationHandler, org.springframework.cglib.proxy.InvocationHandler {
    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns ) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TraceStatus status = null;
        // 메서드 이름 필터
        String methodName = method.getName();
        // save, request, reqe*, *est ...
        // PatternMathchUtils spring에서 제공(org.springframework.util.PatternMatchUtils)
        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            return method.invoke(target, args);
        }

        try {
            // mehtod에서 선언한 메소드 이름을 가져온다
            String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
            status = logTrace.begin(message);

            // 로직 호출
            Object result = method.invoke(target, args);
            // String result = target.request(itemId);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
    
}
