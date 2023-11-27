package hello.proxy.pureproxy.proxy;

import org.junit.jupiter.api.Test;

import hello.proxy.pureproxy.proxy.code.CacheProxy;
import hello.proxy.pureproxy.proxy.code.ProxyPatternClient;
import hello.proxy.pureproxy.proxy.code.RealSubject;

public class ProxyPatternTest {
    
    @Test
    void noProxyTest() {
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
    }

    @Test
    void CacheProxyTest() {
        RealSubject realSubject = new RealSubject();
        CacheProxy CacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient client = new ProxyPatternClient(CacheProxy);

        client.execute();
        client.execute();
        client.execute();
    }
}
