# @Controller, @RequestMapping

@Controller, @RequestMapping이 있어야 스프링 컨트롤러로 인식되며
HTTP URL이 매핑되고 동작한다.

@Import(AppV1Config.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app") // 주의
public class ProxyApplication
    main method에서 Import로 Spring Bean을 등록해준다.
    scanBasePackages로 빈 찾을 위치를 지정해주고, 그외에 대상에게 @Import()로 Bean 설정을 도와준다.



















