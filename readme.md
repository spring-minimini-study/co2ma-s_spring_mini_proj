# Spring Boot 미니 프로젝트

--- 
## 목적

백엔드 개발자로서 요구 되어지는 역량 중 하나인 spring 그 중에서도 spring boot 학습을 통하여, RESTful한 API를 설계, MVC 패턴 이해, 복잡한 비즈니스 로직등 안정적인 시스템으로 설계하고 운영할 수 있는 역량을 키우기 위함이다.

---
## 1주차 (GET 메소드로 Hello 엔드포인트 만들기)

### 환경 설정
start.spring.io 에서 프로젝트를 생성한다.
- 설정: gradle-groovy, java, v4.0.9, jdk 21
- 추가한 의존성: Spring Web, Lombok, Spring Boot DevTools, Spring Data JPA, PostgreSQL Driver

#### 의존성에 대한 설명
- Spring Web: Tomcat 내장서버를 포함한 HTTP 요청을 처리 해주는 역할
- Lombok: 반복된 코드 사용을 줄여주는 @getter, @setter 등 어노테이션 지원
- Spring Boot DevTools: 서버 재시작 없이 수정 사항을 실시간 반영 (live server 느낌)
- Spring Data JPA: 쿼리문 대신 자바 메서드를 이용해서 DB를 조작 할 수 있게 해준다.
- PostgreSQL Driver: mysql, oracle과 같은 오픈소스 DB 드라이버

### HelloController 생성
1. src/main/java/[패키지명] 에 HelloController.java 파일을 생성한다.
2. Spring Web 어노테이션을 추가한다.
   1. @RestController: 외부 요청을 받는 컨트롤러임을 명시
   2. @GetMapping: get 명령이 들어왔을 때 해당 함수를 실행 하도록 함. 
3. return 값으로 "Hello, World"을 반환 함으로써 브라우저에서 해당 결과를 확인 할 수 있음.


