# LOG 추적기

오류나 시간이 얼마나 걸리는지 어디서 문제인지 체크가 가능
모니터링 툴로 해결이 되긴한다.

1. 비즈니스 로직에 영향을 주면 안된다.
2. 메서드 호출에 걸린 시간
3. 정상 흐름과 예외 흐름 구분
4. 메서드 호출의 깊이 표현
5. HTTP 요청 구분
    - HTTP요청 단위로 특정 아이디 값 확인으로 구분
    - 트랜젝션 ID(DB 트랜젝션이 아니다.) 하나의 HTTP 요청이 시작해서 끝날 때 까지를 하나의 트랜젝션이라 한다.

로그 추적기를 위한 기반 데이터, TraceId TraceStatus

TraceId 동기화 필요, 그로인해 관련 메서드 모두 수정 필요
HTTP 요청을 구분하고 표현하기 위해 TraceId 파라미터로 넘기는 방식 말고 구현이 필요

동시성 문제는 FieldLogTrace는 싱글톤으로 등록된 스프링 빈이기에 발생한 문제이다.
작업을 하는 쓰레드는 달라지는데 traceId는 동일하고 다른 작업이 현재 작업에 영향을 미친다.

기존
private String nameStrore;
동기화
private ThreadLocal<String> nameStrore = new ThreadLocal<>();
    - ThreadLocal에 UserA와 UserB를 담고 맞는 곳에 A와 B를 자동으로 넣어준다
    - 사용법
        nameStrore.get()
        nameStrore.set(name);

사용 후 TreadLocal.remove()로 초기화 해줘야한다.