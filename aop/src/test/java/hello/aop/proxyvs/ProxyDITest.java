package hello.aop.proxyvs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"})   // JDK 동적 프록시 적용
@Import(ProxyDIAspect.class)
public class ProxyDITest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberServiceImpl memberServiceImpl;
    // UnsatisfiedDependencyException: Error creating bean with name 'hello.aop.proxyvs.ProxyDITest': Unsatisfied dependency expressed through
    // field 'memberServiceImpl': Bean named 'memberServiceImpl' is expected to be of type 'hello.aop.member.MemberServiceImpl' but was actually of type 'jdk.proxy2.$Proxy50'

    @Test
    void go() {
        log.info("memberService class = {}", memberService.getClass());
        log.info("memberServiceImpl class = {}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }

}
