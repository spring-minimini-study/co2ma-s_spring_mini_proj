# 3주차

## 목표
실무적 관점의 구조 재구성 및 Flyway를 통한 DB 구조 관리

## 들어가기에 앞서
지금 까지 모든 내용을 메인의 readme.md에 위치 시켰는데, 너무 길어지게 되니
보기에도 좋지 않다고 판단하여 각 주차별로 .md 파일을 생성하여 기록하도록 변경했다.

## Spring Boot 표준 계층 구조
지난 주에는 단순히 Controller - Entity - Repository의 3개 구조로 작성하였지만,
이는 프로젝트가 커질 수록 관리가 어려운 구조가 되게된다.

실무에서 사용하는 표준인 Service와 DTO 구조를 추가해서 작성을 해보겠다.

일반적인 데이터의 흐름은 다음과 같이 된다.

User Request -> Controller -> Service -> Repository -> DB

1. User Request
   * 유저가 특정 URL로 데이터를 보낸다
   * 이 데이터가 최초에 **DTO**에 담겨서 나가게 된다

2. Controller
    * 유저의 요청으로 넘어온 DTO를 전달 받게 된다
    * 데이터의 형식을 검사한 후에 Service에 DTO를 넘긴다

3. Service
    * 이곳에서 핵심 로직을 처리하게 된다
    * 유저의 명령으로 넘어온 DTO를 **Entity**로 변경을 한다.

4. Repository
    * 이 시점에서 실제 데이터 베이스에 접근을 한다
    * 데이터 베이스에서 새로운 정보를 **Entity** 형태로 받는다

5. Service
    * Entity에서 나온 정보를 다시금 재구성한다
    * 새로운 **DTO** 만든다 (민감한 정보 같은것은 제외)

6. Controller
    * Service에서 받은 Response DTO를 변환 한다 (ex: JSON)

7. Server response
    * 최종 결과를 유저에게 반환한다.

## 패키지 구조에 대한 생각
어느 프로젝트를 보면 계층형 구조로 패키지 구조를 controller는 controller 끼리, service는 service 끼리 할 때도 있고,
아니면 기능별로 하나의 패키지에 모아놓은 구조를 볼 수 있다.
무엇이 정답이라고 할 수는 없다고 하지만 두 방식의 장점을 찾아보았다.

|         상황          |     추천 구조      | 이유                                  |
|-------------------|:--------------:|-------------------------------------|
| 학습 단계 /소규모 토이 프로젝트  |  계층별 (Layer)   | 전체적인 스프링의 흐름을 파악하기에 직관적이고 관리가 편함    |
|   규모가 큰 프로젝트 / 실무   | 기능별 (Feature)  | 기능 단위로 코드가 모여있어야 유지보수 생산성이 압도적으로 높음 |

이번 프로젝트 자체는 규모가 크지 않지만, 임시로 사용할 테이블 같은게 있으므로, 기능별 구조를 사용해 보려 한다.

## 파일 수정
```java
// Student.java
@Entity
@Table(name = "student")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder

public class Student {
    @Id
    private String studentId;

    private String name;
    private String phone;
}
```
일단 지난주에 만들었던 Student Entity 파일을 수정을 하였다. 
@Data 어노테이션을 사용하지 않는 방향으로 수정을 해보았다.
### 각 어노테이션의 역할
1. @Getter: 데이터를 조회할 수 있는 getXXX() 메소드를 생성한다.
2. @NoArgsConstructor(access = AccessLevel.PROTECTED): parameter가 없는 기본 생성자를 생성한다.
JPA 내부에서는 DB에서 데이터를 가져와서 객체를 만들때 기본 생성자가 필요하다.
하지만, 빈 객체를 만드는 것을 막기 위해 **JPA의 최소 범위인 PROTECTED**로 열어, 객체 생성을 막는다.
3. @AllArgsConstructor(access = AccessLevel.PROTECTED): 모든 parameter에 대해서 인자를 입력받는
전체 생성자를 만든다. 이것의 목적은 후술할 @Builder를 위해 존재하며, 이 역시 PROTECTED로 접근을 제한한다.
4. @Builder: 기존 자바의 객체 생성 방식인 new는 인자 순서도 햇갈리지만 Builder를 사용하면 무조건
Builder로만 생성할 수 있고 넣고 싶은 데이터만 넣을 수 있다. (넣지 않은 데이터는 JAVA의 default 값)

```java
// StudentRequest.java
public record StudentRequest(
        String studentId,
        String name,
        String phone
) {}

// StudentResponse.java
public record StudentResponse(
        String studentId,
        String name,
        String phone
) {
    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getName(),
                student.getPhone()
        );
    }
}
```
DTO 클래스이다. 여기에서 class 대신에 생소한 record 라는 것이 들어가 있다.
record는 java 16버전에서 추가된 문법으로, 기존 방식으로 DTO를 선언하려 하면
여러개의 어노테이션과, 각 변수 별로 불변성을 위해 private final을 붙여줬어야 했다.
하지만, record를 사용하면 다음이 자동으로 해결된다.
1. 필드가 기본 private final로 생성된다. (불변성 유지)
2. 모든 필드를 입력 받는 생성자를 자동으로 생성한다.
3. 접근자 (getter) 생성
4. equals, hashcode, toString 자동 생성

이러한 특성 때문에 @Data 보다 간단하고 간결하게 사용 할 수 있지만, 이 역시 @Data의 단점을 고스란히
가지고 있기 때문에 DTO class 이외에서는 잘 사용하지 않는다. 그리고 또한 가변성이 필요하거나, 커스텀
로직이 필요하다면 기존 DTO 구현이 다시금 필요해진다.

```java
// StudentService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public StudentResponse save(StudentRequest dto) {
        Student student = Student.builder()
                .studentId(dto.studentId())
                .name(dto.name())
                .phone(dto.phone())
                .build();
        Student saved = studentRepository.save(student);
        return StudentResponse.from(saved);
    }
    //... 이하 생략 ...
```
### 각 어노테이션의 역할
1. @Service: 서비스 레이어 임을 스프링에게 알려준다 (자동으로 bean 으로 등록 한다고 함)
2. @RequiredArgsConstructor: 롬복 기능으로 final이 붙은 필드들을 모아 생성자를 만들어줌
3. @Transactional: DB와 연결되는 핵심 부분, method 가 실행 될때 DB연결을 시작하고, 자동 커밋까지 진행한다
readOnly 명령으로 조회만 할것임을 명시한다. (save 부분만 따로 readOnly를 빼고 선언)

이 방식들로 일단, DTO 구조와 Service 구조까지 나눠서 추가를 했다.

## DB 구조 수정
일단 JAVA에서는 작성 할 때 camelCase로 작성하는 것이 원칙이다, 그리고 JPA 에서 camelCase인
db 속성을 발견하면 자동으로 snakeCase로 변경하여 db 테이블에서 해당 내용을 찾는다.

이 특성을 반영하기 위해 java 코드들은 전부 camelCase화 하였고, flyway의 db attribute들은 전부
snakeCase를 유지 하였다.

```text
# build.gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-flyway' // module dependency
    runtimeOnly "org.flywaydb:flyway-database-postgresql"
}
```
spring boot 4.0 버전 이후 부터는 저 2개의 의존성을 추가해야한다.

```properties
# application.properties
// flyway에서 table을 관리하게 하기 위해 auto 모드를 none으로 설정
spring.jpa.hibernate.ddl-auto=none

spring.flyway.enabled=true
// true로 뒀을 경우 기존 테이블 구조를 유지하면서 만들게 함.
spring.flyway.baseline-on-migrate=false
```
properties를 설정해준다.

## 이번 주차 회고
* 시간이 바쁘다는 핑계로 진행이 너무 늦어진점이 아쉬웠다.

## 다음 주 목표
1. dbeaver 사용해보기
2. swagger 학습 및 적용해보기
3. docker 적용하기
4. 미니 프로젝트 주제 정하기