# 6주차

## 목표
* Bean은 뭘까요 + Spring Security의 이해
* JWT가 무엇일까?
* JWT를 적용한 로그인 폼 생성하기

## Bean 이란 무엇일까?
spring boot의 보안 설정을 하기 위해 지난 주에 SecurityConfig.java 파일을 추가 했었다.
이번에는 그 용도에 대해 알아 보고자 한다.
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```
이번에도 상단의 어노테이션의 역할 부터 짚고 넘어 가려 한다.

* `@Configuration`은 이 클래스가 project 전체의 설정을 담고 있다는 것을 명시해준다.
* `@EnableWebSecurity`는 spring security의 웹 보안 기능을 활성화 한다.

즉, 이 클래스를 보안 설정 클래스로 만드려는 것이다.

이제 내부를 보면 `@Bean`이라는 어노테이션이 존재할 것이다. 이것은 스프링 부트 내부에 존재하는
Spring Container라는 객체 보관 창고를 의미한다.

그래서 MemberService.java 에서 객체를 생성하고, `@RequiredArgsConstructor`에 의해 자동으로
passwordEncoder()가 생성자에 주입되게 된다.

다음과 같은 파이프 라인으로 처리가 될 것이다.

`PasswordEncoder 객체 생성 -> @RequiredArgsConstructor에 의한 생성자 주입 
-> @Bean에서 passwordEncoder() 호출 -> BCryptPasswordEncoder() 라는 암호화 알고리즘 반환
-> passwordEncoder 초기화 완료`

그러면 궁금증이 생성자를 주입 할 때, 어떻게 이 passwordEncoder() 라는 이름의 함수가 주입 되는 걸까
싶어질 것이다. 이 때의 규칙은 다음과 같다. 

spring이 Bean에서 일단 같은 타입으로 저장된 객체를 확인하게 된다. 그래서 이때 PasswordEncoder로
구현된 객체가 존재 하므로, 이 객체가 주입 되어진다. 그러나 Bean에 같은 타입의 객체가 2개 이상 있다면,
그때는 변수명을 확인해서 같은 변수명으로 선언된 객체를 찾아서 초기화 해준다.

같은 맥락으로 `filterChain` 함수의 경우에도 따로 호출부는 존재하지 않지만, 사용자가 요청을 보냈을 때 확인 할 수 있다.

Tomcat 서버에 요청이 도착하면 Controller 코드에 도착하기 전에 Spring Security Filter Chain이
작동하게 되는데, 이 곳에서 아까와 같은 원리로 SecurityFilterChain이라는 객체명을 Bean에서 찾아서
내부에서 선언된 규칙에 따라서 보안 검색을 하는 원리이다.

현재 코드에서는 다음과 같은 규칙이 적용 되어있다.
* `csrf().disable()`로 위조 요청 공격을 막는 기능을 끈다. (쿠키 관련인데, 좀 더 알아야 함)
* `formLogin().disable()`로 spring security가 제공하는 기본 로그인 웹페이지를 끈다.
* `permitAll()`로 모든 요청을 허락 하게 한다.(JWT를 구현하면 없앨 내용)

즉 로그인의 보안을 위해 이러한 구조를 가지게 되었다.

## JWT가 무엇인가?
JWT(JSON Web Token)는 클라이언트와 서버간의 정보를 안전하게 주고 받기 위한 JSON 형태의
디지털 출입증 같은 역할을 한다.

로그인을 하게 되면 그 이후에는 이 토큰을 다시 보여주는 것으로 로그인을 대체 할 수 있다.

JWT의 규칙은 Header, Payload, Signature의 3개 부분으로 나뉘어져 있다.

JWT가 나오게 된 배경은 기존에 사용하던 세션 방식의 문제점은 서버가 메모리상에서 로그인 기록을
계속 관리 해주어야 했는데, 접속자 수가 늘어나면 생기는 메모리 문제와 다중 서버에서 다른 서버는
해당 유저의 로그인 사실을 알 수가 없다는 것이 문제가 있었다. 
<br>그러한 문제를 해결 하기 위해, 서버는 로그인 사실을 기억하지 않지만 JWT 토큰 하나에 모든
정보가 담겨 있어 이를 통해 확인한다.

그래서 이 부분이 위에서 언급 했던, csrf를 disable 했던 이유가 된다. 단순히 문자열을 전송하기에
쿠키 공격에 대한 방어를 disable 했던 것이다.

[JSON Web Tokens - jwt.io](https://www.jwt.io/) <br>
이 사이트에서 어떤 식으로 암호화가 되어지는지 알 수 있다. (signiture 부분이 중요)
[Inpa Dev - JWT 토큰 인증 이란?](https://inpa.tistory.com/entry/WEB-%F0%9F%93%9A-JWTjson-web-token-%EB%9E%80-%F0%9F%92%AF-%EC%A0%95%EB%A6%AC)<br>
이 사이트에서는 JWT 토큰 인증에 대한 설명을 볼 수 있다. 이 곳에서 Cookie 인증과 Session 인증의
문제점에 대해서 알 수 있다. Token 방식 역시 Token 탈취의 경우 대처하기 힘들다는 단점이 있다.

### 여기서 생기는 궁금증?
JWT를 사용하는 방식은 사용자가 로그인 된 이후, 다른 페이지를 이동할 때, 로그인이 되어있었음을 증명하는
부분인 것이다. 그러면 최초 로그인 시에는? ID, PW를 입력해야 할 텐데 이것의 보안은 지켜지지 않는 것
아닌가 하는 의문이 들었다.

이 의문은 HTTPS의 존재로 인해 안전하게 전송 할 수 있다. ID, PW를 입력 해서 서버로 보낼 때, HTTPS에서
이미 PW를 암호화 해서 전송 하게 된다. 그렇기에 DB에서도 사용자의 본 PW를 저장 해서 가지고 있는 것이
아닌, 암호화된 PW만을 가지고 있게 되는 것이다. 그래서 이 암호화된 PW를 비교해서 맞다면 JWT를 만들어
이 토큰을 반환 해주고, 앞으로의 인증에서는 이 Token으로 대체 되어진다.

근데 결국 이 Token이 해커에게 탈취 되었을 때, 문제가 생기는 것인데, 그렇기에 단순히 Aceess Token만 전송
하는 것이 아닌, Refresh Token을 전송하게 된다. 그러면 다시금 생기는 궁금증은 이 Refresh Token이
탈취 당한다면?

다음의 3가지 방식을 통해 이 문제를 해결한다.
1. HttpOnly Cookie를 사용하여, JS code로 읽을 수 없는 쿠키를 생성한다.
2. Refresh Token을 한번 사용하면 폐기 하고, 새로운 RT를 발급 해준다. 누군가 이 토큰을 다시
사용 한다면, 해킹으로 간주하고 해당 유저의 Access Token과 Refresh Token을 삭제 해버린다.
3. Redis 인메모리 기능을 사용 하는 것으로 Refresh Token만은 저장 해두는 방식을 사용한다.

### 세션-쿠키, JWT 무엇이 더 좋은가?
앞서 이야기들만 보면 세션-쿠키는 CSRF에 더불어 보안성이 낮아 보일 수 있지만 오히려 CSRF 토큰,
SameSite 쿠키 설정, HttpOnly Cookie 등으로 충분히 안전하게 작동 시킬 수 있으며, 구현 면에서
단순하고, 금융쪽 시스템에서는 오히려 서버에게 권한 전권을 줄 수 있어 더 좋은 선택지가 된다.
JWT의 경우는 보안 자체 보다는 **다중서버 효율성**, **모바일 앱과의 호환성** 측면에서 장점으로
작동할 수 있다.

### 그러면 왜 이번 프로젝트에서는 JWT를 해볼까?
이 프로젝트에서 해보려 했던 것들이 몇가지 있었다.
1. REST 개발 원칙 준수
2. 실무 개발 경험
3. Redis 캐싱 사용 해보기

등의 몇가지 조건이 있었다. REST 원칙 중 하나인 Stateless를 만족 시키기 위해서 서버가 세션을
기억하면 안되는 조건이 있었다. 또한 Refresh Token을 관리하는 것이 Redis를 사용 하는 가장
대표적인 방식이기에 이것을 경험해보고자, JWT를 직접 구현 해보려 했다.

## 이번 주차 회고
* 하나를 알면 그 꼬리식으로 궁금해지는 부분이 생기는 것 같다.
* 특히 JWT 하나에서만 파생 되어지는 궁금증, 보안적 문제를 신경 쓰게 되는 것 같다.

## 다음 주 목표
* 아직 미정
