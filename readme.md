# Spring Boot 미니 프로젝트

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

---
## 2주차 (JPA를 활용하여 자바 객체를 DB 테이블로 저장 및 엔터티 설계, 데이터의 get, post 학습)

### 시작하기 전에
DB 의존성을 추가하면 기존과 다르게 실행이 안될 것이다. DB를 추가하는 시점에서 컴파일러는 동시에 DB의 정보를 요청하게 되는데,
이때 properties 에 해당 내용이 존재 하지 않는다면 설정 누락으로 인식하고 에러를 발생 시키게 된다.
다음의 내용을 properties에 추가 해야한다.

```text
spring.application.name=mini-proj

spring.datasource.url=jdbc:postgresql://localhost:5432/(프로젝트명)
spring.datasource.username=(postgres 아이디)
spring.datasource.password=(postgres 비밀번호)
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### 각각의 역할 소개
- spring.datasource.url: DB 주소, localhost:5432는 내 컴퓨터의 5432 포트에서 실행 중인 DB에 접속하겠다는 뜻입니다.
- spring.datasource.username: DB 접속 아이디
- spring.datasource.password: DB 접속 비밀번호
- spring.datasource.driver-class-name: DB 연결 드라이버, 자바 프로그램과 PostgreSQL이 소통할 수 있게 해주는 역할 
- spring.jpa.hibernate.ddl-auto: 테이블 자동 생성 및 변경된 부분만 자동으로 반영 (실무에서는 update 대신 none, validate를 사용)
- spring.jpa.show-sql: 로그 출력 여부 (true: 실행되는 모든 SQL 쿼리를 콘솔창에 보여줌)
- spring.jpa.properties.hibernate.format_sql: 로그 가독성 SQL 문을 한 줄로 쭉 쓰지 않고, 보기 좋게 정렬해서 출력

상기의 내용을 추가하면 정상적인 실행이 가능 해진다.

### 목표) STUDENT 테이블을 만들고 GET, POST를 처리하는 코드를 작성한다.
프로젝트를 진행할 파일 구조는 다음과 같다. 
```text
mini_proj
ㄴ controller
    ㄴ StudentController.java
ㄴ entity
    ㄴ Student.java
ㄴ repository
    ㄴ StudentRepository.java (Interface)
```

### Student.java
우선 Student의 정보를 담을 테이블을 만들어야 한다. 이때 jpa 의존성의 역할이 나타나게 된다.

```java
@Entity
@Table(name = "student")
@Data
public class Student {

    @Id
    private String student_id;

    private String name;
    private String phone;

    public Student() {}
}
```

#### 각 어노테이션의 역할
1. @Entity : 현재 java 프로젝트를 하나의 엔터티로 설정한다. 앞선 properties에서 선언한 `spring.jpa.hibernate.ddl-auto=update` 가 자동으로 DB 테이블을 만들어 준다. (단 update 태그가 붙은 이 테이블은 테이블 구조가 변경 되면 DB 구조도 같이 변경 되는데 이 때문에 오류가 생길 확률이 높음)
2. @Table : 생성된 엔터티의 테이블 이름을 정해준다.
3. @Data : Data 어노테이션을 사용하면 getter(), setter() 에 대해서 자동으로 생성해 준다.
4. @Id: 주식별자(Primary Key)를 설정 해준다. (당연히 not null 이다)

상기의 코드는 다음의 쿼리와 같은 역할을 한다.
```sql
CREATE TABLE student (
    student_id VARCHAR(255) NOT NULL,   -- PK (학번)
    name VARCHAR(255),                  -- 이름
    phone VARCHAR(255),                 -- 전화번호
    
    PRIMARY KEY (student_id)            -- student_id를 기본키로 지정
);
```

### StudentRepository.java
다음은 repository 코드를 작성해 준다
```java
@Repository
public interface StudentRepository extends JpaRepository<Student, String> { }
```

이 코드를 작성하면 기존 java에서는 interface 임으로 implements로 해당 내용을 구현해야 하지만, 자동으로 JPA에서 구현체를 만들어서 제공하게 된다.

다음과 같은 함수를 제공한다.

| 메서드             | 실제 실행되는 SQL (JPA의 역할)                      | 비고                  |
|:----------------|:-------------------------------------------|:--------------------|
| save(student)   | INSERT INTO student ... 또는 UPDATE ...      | 객체의 상태를 보고 저장/수정 결정 |
| findById(id)    | SELECT * FROM student WHERE student_id = ? | PK를 이용한 단건 조회       |
| findAll()       | SELECT * FROM student                      | 전체 목록 조회            |
| delete(student) | DELETE FROM student WHERE ...              | 데이터 삭제              |
그리고 JpaRepository의 generic 안에 제공 되어있는 <Student, String> 타입은 처음에 온 것은 관리할 테이블의 이름을 의미하고, 두 번째로 오는 데이터는 Primary Key의 데이터 타입을 명시한다.

### StudentController.java
다음은 코드의 핵심인 Controller의 코드이다.
```java
@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public Student add(@RequestBody Student student){
        return studentRepository.save(student);
    }

    @GetMapping
    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    @GetMapping("/search")
    public Student getOne(@RequestParam String student_id){
        return studentRepository.findById(student_id).orElse(null);
    }

}
```
#### 각 어노테이션의 역할
1. @RestController : @Controller와 @ResponseBody가 합쳐진 어노테이션으로, 해당 class를 spring 관리하의 response 처리 담당으로 사용 되어지고, Student와 같은 Java 객체를 랜더링 하는 것이 아닌 JSON 데이터로 변환 및 전달하는 역할을 한다. 또한 자동으로 singleton 패턴으로 작동한다.
2. @RequestMapping : 해당 클래스에서 선언 된 메소드들은 호출 할 때 뒤에 `/students`가 기본적으로 붙어서 호출 된다는 것을 명시해준다.
3. @CrossOrigin : CORS 문제를 해결 하기 위해 서버가 브라우저에게 접속을 허락 해주는 역할을 한다. (해당 도메인을 입력 해주는 것이 맞지만, 연습 프로젝트임을 감안하여 모든 접속을 허용해준다.)
4. @PostMapping : 해당 메서드는 Post 명령을 처리 함을 명시한다.
5. @GetMapping : 해당 메서드는 Get 명령을 처리 함을 명시한다.
6. @RequestBody : 본문 안의 데이터를 받아서 자동으로 객체 타입으로 변환 시켜주는 역할을 한다.
7. @RequestParam : url 주소 뒤에 붙어 오는 데이터를 받아 파라미터로 전달한다.

#### + CORS 문제란?
브라우저는 동일 출처 원칙(SOP) 라는 규칙을 따른다. 이 규칙에 의거하면 A 서버에 호출하는 것은 A 사이트 여야만 하는 원칙이여야 하기에, 자동으로 접속이 차단 되는 것이다.

### 테스트 결과
Gemini를 통해 테스트 사이트를 생성 Get, Post 명령을 테스트 해보고 그에 따른 결과 이다.
<img width="456" height="600" alt="image" src="https://github.com/user-attachments/assets/5ff809ba-ae96-4f3b-8c4a-784553282d3a" />





