package hello.aop.internalcall;

/*
 *  생성자 주입은 순환 사이클을 만들기 때문에 실패한다.
 *
 *  주의 : 스프링 부트 2.6부터는 순환 참조를 기본적으로 금지하도록 정책이 변경 되어 위의 코드는 별도의 설정을 추가하지 않으면 정상 실행되지 않는다.
 */
// @Slf4j
// @Component
// public class CallServiceV1 {
//     private CallServiceV1 callServiceV1;
    
//     @Autowired
//     public void setCallServiceV1(CallServiceV1 callServiceV1) {
//         log.info("callServiceV1 setter = {}", callServiceV1.getClass());
//         this.callServiceV1 = callServiceV1;
//     }
    
//     public void external() {
//         log.info("call external");
//         callServiceV1.internal(); //외부 메서드 호출
//     }
    
//     public void internal() {
//         log.info("call internal");
//     }
// }