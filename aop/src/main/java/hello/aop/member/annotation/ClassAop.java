package hello.aop.member.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)   // AOP에는 @Target이 필요하다.
@Retention(RetentionPolicy.RUNTIME) // Runtime, 실제 실행할 때까지 살아있다. 다른걸로하면 Compile되면 사라진다.
public @interface ClassAop {
    //@interface 사용함으로써 AOP가 된다.
}
