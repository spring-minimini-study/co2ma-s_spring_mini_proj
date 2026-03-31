# 5주차

## 목표
1. member라는 이름의 db table 만들기
2. 로그인 관련 된 Entity, Service, DTO, Repository, Controller 구현하기
3. Post 명령으로 회원가입(db에 저장), 로그인(db response) 확인 하기

## 들어가기에 앞서
기존에는 프로젝트 주제를 정하고 바로 개발을 들어가려 했으나, 무엇부터 만들어야 할지 감을
잡지 못하여, 프로젝트 진행은 뒤로 미루고 일단 가장 기본적이면서도 중요한 로그인 관련 기능을
우선적으로 구현하고 그 이후에 프로젝트를 적용 하려 생각중임.

## member Table 만들기
이전에 db의 변경 과정을 관리하기 위해 flyway 의존성을 추가 했었다. 이번에는 새롭게 추가할
member Table을 만들어 보려 한다.

db/migration 폴더에 `V2_member.sql` 이라는 이름의 새로운 쿼리를 작성할 파일을 만들어 준다.
```postgresql
CREATE TABLE member (
    id          BIGSERIAL,
    login_id    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    role        VARCHAR(50) NOT NULL,
    CONSTRAINT  pk_member PRIMARY KEY (id)
);
```
다음과 같은 데이터들이 들어가게 된다. 기본적으로 모든 데이터는 Null을 허용하지 않는다.
* id: 자동으로 생성되는 id 값으로 index 접근이 용이하게 하였다.
* login_id: 아이디 값이 들어가는 부분으로 중복을 허용하지 않는다.
* password: 패스워드 값이 들어가는 부분으로 암호화 된 패스워드가 저장이 된다.
* role: 역할 값이 들어가며 enum으로 관리 되어진다.

이전 문서에서도 언급 했듯이 sql은 snakecase를 사용하기 때문에 login_id 로 이름을 정해준다.

이후 실행시, 테이블이 생성 되며 버전 2로서 관리 되어진다.

## Entity
이전과 다르게 코드 모든것을 해석하는 것이 아닌 내가 느낀 새로운 부분만 적고자 한다.
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
private Long id;

// 생략

@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Role role; 
```
Entity 코드에서 이번에 볼 부분은 두 부분이다.

@GeneratedValue 어노테이션을 사용하면 DB에서 테이블에 데이터가 추가 될 때, 자동으로 카운트를
증가시켜 id 값을 매핑 시킬 수 있다.

@Enumerated 어노테이션을 사용하면 enum 타입을 사용 할 때 enum이 가진 이름을 DB에 문자열로
저장을 할 수 있다.

## memberRepository
```java
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
```
이번에는 JpaRepository만 입력 하는 것이 아닌 인터페이스 내부에 무언가 추가가 되어있다.
저것 역시 JPA의 기능이다.
* findBy 가 접두사로 붙으면 함수 파라미터로 넘어간 값에 대해서 결과 값을 반환하는 쿼리를 내부적으로 실행한다.
* existsBy 가 접두사로 붙으면 함수 파라미터로 넘어간 값에 대해서 값의 존재 유무에 따라 true/false를 반환 한다.

회원가입, 로그인 기능을 구현하기 위해 중복 값을 거르기 위한 방법이다.

## memberService
```java
// 생략
private final MemberRepository memberRepository;

@Transactional
public void signUp(SignUpRequest request){
    if(memberRepository.existsByLoginId(request.loginId())){
        throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
    }
    if (memberRepository.existsByEmail(request.email())) {
        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
    }
    if (memberRepository.existsByNickname(request.nickname())) {
        throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
    }

    Member member = Member.builder()
            .loginId(request.loginId())
            .password(passwordEncoder.encode(request.password()))
            .nickname(request.nickname())
            .email(request.email())
            .role(Role.USER)
            .build();

    memberRepository.save(member);
}
//생략
```
DTO를 만들고 이 곳에서 그 DTO 데이터를 처리하게 된다.

이 곳에서 repository에서 선언한 existsBy- 함수가 사용 되는 부분이다. 중복 데이터가 없으면
DB에 값을 저장한다.

그리고 여기서 password는 passwordEncoder라는 값을 받고 있는데, 이것은 spring 의존성 중
spring security에 대한 내용으로 다음 내용을 build.gradle에 추가 해준다.
`implementation 'org.springframework.boot:spring-boot-starter-security'`

## memberController
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
        memberService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료 되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = memberService.login(request);
        return ResponseEntity.ok(response);
    }
}
```
상기와 같은 Controller 코드를 작성 해준다.

여기서 @RequestBody는 JSON 객체를 DTO로 변환 시키는 역할, 
@valid 어노테이션은 DTO에서 미리 작성 된 @NotBlank, @Size 같은 규칙에 맞는지 확인하게 한다.

## 결과

## 이번 주차 회고
* 너무 많은 것을 한번에 하려는 것 보다 확실히 알고 넘어가는게 중요할 것 같다.
* 3월 시점에는 AI 강의, 원서 접수, 코딩 테스트 준비 등으로 시간을 너무 소비 했다.
* 4월에는 Spring 수업도 진행 될 것이니, 이에 맞춰서 역량을 강화 해야 할 것 같다.

## 다음 주 목표
* SecurityConfig가 무엇이고 왜 필요한지 정리
* JWT 생성 로직 추가
* 로그인 세션 관리
* 기본적인 로그인 인터페이스 만들기
