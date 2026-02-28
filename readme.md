# Spring Boot 미니 프로젝트

--- 
## 목적

백엔드 개발자로서 요구 되어지는 역량 중 하나인 spring 그 중에서도 spring boot 학습을 통하여, RESTful한 API를 설계, MVC 패턴 이해, 복잡한 비즈니스 로직을 안정적인 시스템으로 설계하고 운영할 수 있는 역량을 키우기 위함이다.

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

