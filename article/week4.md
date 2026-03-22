# 4주차

## 목표
1. dbeaver 사용해보기
2. swagger 적용해보기
3. docker 적용하기
4. 미니 프로젝트 주제 정하기
5. 로그인 기능 추가


## Docker 적용하기
일단 필자는 Docker 이미지를 사용해본 적은 있지만 Docker 설정은 처음 해본다.

dockerfile을 만드려 찾아볼 때 Spring Boot는 더 쉬운 방법이 있다는 것을 알게 되었다.

바로 bootBuildImage를 사용 하는 것이다. 다음은 Gemini를 통해 두 방식의 차이를 정리 해보았다.
### Spring Boot Docker 빌드 방식 비교

| 구분 | Dockerfile (수동 방식) | bootBuildImage (자동 방식) |
| :--- | :--- | :--- |
| **개념** | 개발자가 직접 빌드 과정을 코딩함 | 스프링이 최적화된 빌더를 사용하여 자동 생성 |
| **제어권** | **매우 높음** (OS 패키지 설치 등 자유로움) | **보통** (표준 설정 위주, 커스텀은 설정 필요) |
| **난이도** | **중간** (Docker 문법 숙달 필요) | **낮음** (명령어 한 줄로 빌드) |
| **최적화** | 직접 설정 (Multi-stage Build 등) | **기본 적용** (레이어 분리 및 용량 최적화) |
| **보안** | 베이스 이미지 보안 패치 직접 관리 | 빌더 업데이트 시 **보안 패치 자동 적용** |
| **추천 환경** | 특수한 OS 라이브러리/도구가 필요한 경우 | 클라우드 네이티브 배포 및 표준 스프링 앱 |

---
Spring boot에서는 bootBuildImage를 사용하는 것이 최적화가 더 잘되어 있다 하기에 dockerfile 생성 대신에 bootBuildImage 방식을 사용하려 한다.

하지만 이렇게 설정하더라도, 세부 설정사항은 docker-compose.yml 파일에 정의를 해놔야 한다. 다음 내용은 docker-compose의 구성 내용이다.
```dockerfile
services:
  db:
    # DB 이미지를 무엇을 사용 할지 설정 
    image: postgres:15-alpine       
    # Container 이미지 이름을 설정
    container_name: postgres-db     
    environment:
      - POSTGRES_USER=${DB_ID}
      - POSTGRES_PASSWORD=${DB_PW} 
      - POSTGRES_DB=miniproj        
    # DB의 포트 번호 설정
    ports:
      - "5432:5432"                 
    volumes:
    # DB 데이터가 저장될 위치를 정해준다. (미정시 down하면 내용이 휘발됨)
      - ./postgres_data:/var/lib/postgresql/data    
    # DB가 정상적으로 켜졌는지 확인하고 실행
    healthcheck:    
      test: [ "CMD-SHELL", "pg_isready -U postgres -d miniproj" ]
      interval: 5s
      timeout: 5s
      retries: 5
  app:
  # bootBuildImage로 만든 이미지 이름 입력
    image: mini-proj:latest     
    container_name: spring-app  
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      # 1. DB 주소를 컨테이너 서비스 이름인 'db'로 덮어씁니다.
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/miniproj
      - SPRING_DATASOURCE_USERNAME=${DB_ID}
        - SPRING_DATASOURCE_PASSWORD=${DB_PW}
      # 2. Flyway가 Docker 안에서도 잘 작동하도록 활성화 (이미 되어있지만 명시적 확인)
      - SPRING_FLYWAY_ENABLED=true
      # 3. Hibernate 설정 (기존 설정 유지)
      - SPRING_JPA_HIBERNATE_DDL_AUTO=none
```
그리고 하단의 명령을 통해 서버를 on/off 할 수 있다.
```shell
#Docker 이미지를 자동으로 생성 및 업데이트
./gradlew bootBuildImage # --imageName=(이미지 이름)

#Docker 실행
docker compose up -d

#Docker가 잘 실행 되고 있는지 확인
docker ps

#Docker 종료
docker compose down
```

## swagger 사용하기
Swagger를 사용하기 위해서 다음 의존성을 gradle에 추가한다.
```text
dependencies {
    // Spring Boot 4.x 전용 springdoc-openapi v3.0.0 이상 사용
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0'
}
```
swagger를 실행 했을 때 모습이다.
<img width="1255" height="1027" alt="image" src="https://github.com/user-attachments/assets/b6547dcb-c58a-4dc8-845b-b658e8e516bc" />

## DBeaver 사용해보기
DBeaver를 사용하면 MySQL, Oracle, Postgres 등등 데이터베이스들을 이것 하나로 관리할 수 있다고 한다.
Docker를 통해 실행한 Swagger로 Post 명령을 내리고 그 적용 사항을 DBeaver에서 확인을 했다.

## 이번 주차 회고
* 아직까지도 많이 바쁜주긴 한 것 같다.
* 03월 22일 기준으로 아직 안한게 남아있다.

## 다음 주 목표
