package hello.aop.pointcut;

import org.aspectj.lang.JoinPoint;

// import static org.assertj.core.api.Assertions.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Import(ParameterTest.ParameterAspect.class)
@SpringBootTest
public class ParameterTest {
    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService Proxy = {}", memberService.getClass());
        memberService.hello("helloA");
    }

    @Slf4j
    @Aspect
    static class ParameterAspect {
        @Pointcut("execution(* hello.aop.member..*.*(..))")
        private void allMember() {

        }

        @Around("allMember()")
        public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
            Object arg1 = joinPoint.getArgs()[0];
            log.info("[logArgs1]{}, arg = {}", joinPoint.getSignature(), arg1);
            return joinPoint.proceed();
        } // [logArgs1]String hello.aop.member.MemberServiceImpl.hello(String), arg = helloA

        @Around("allMember() && args(arg,..)")
        public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg) throws Throwable {
            log.info("[logArgs2]{}, arg = {}", joinPoint.getSignature(), arg);
            return joinPoint.proceed();
        } // [logArgs2]String hello.aop.member.MemberServiceImpl.hello(String), arg = helloA

        @Before("allMember() && args(arg,..)")
        public void logArgs3(String arg) throws Throwable {
            log.info("[logArgs3] arg = {}", arg);
        } // [logArgs3] arg = helloA

        // this는 객체 인스턴스의 오브젝트가 넘어온다
        @Before("allMember() && this(obj)")
        public void thisArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[this] {}, obj = {}", joinPoint.getSignature(), obj.getClass());
        } // [this] String hello.aop.member.MemberServiceImpl.hello(String), obj = class hello.aop.member.MemberServiceImpl$$SpringCGLIB$$0
          // 스프링 컨테이너에 올라간 프록시가 올라간다.

        @Before("allMember() && target(obj)")
        public void targetArgs(JoinPoint joinPoint, MemberService obj) {
            log.info("[target] {}, obj = {}", joinPoint.getSignature(), obj.getClass());
        }// [target] String hello.aop.member.MemberServiceImpl.hello(String), obj = class hello.aop.member.MemberServiceImpl
         // 실제 대상 구현체

        @Before("allMember() && @target(annotation)")
        public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@target] {}, obj = {}", joinPoint.getSignature(), annotation);
        }// [@target] String hello.aop.member.MemberServiceImpl.hello(String), obj = @hello.aop.member.annotation.ClassAop()

        @Before("allMember() && @within(annotation)")
        public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
            log.info("[@within] {}, obj = {}", joinPoint.getSignature(), annotation);
        }// [@within] String hello.aop.member.MemberServiceImpl.hello(String), obj = @hello.aop.member.annotation.ClassAop()

        @Before("allMember() && @annotation(annotation)")
        public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
            log.info("[@annotation] {}, obj = {}", joinPoint.getSignature(), annotation.value());
        }// [@annotation] String hello.aop.member.MemberServiceImpl.hello(String), obj = test value
         // @Override
         // @MethodAop("test value")
         // public String hello(String param) {
         //     return "ok";
         // }
            // @MethodAop annotaion의 value값을 가져올 수 있다.
    }
}
