# 9주차

## 현재 진행 사항
* backend 구축 중
* 흐름에 대한 제대로 된 이해
* form 등록 api

## RESTapi의 레이어드 아키텍처 구조
<img width="745" height="655" alt="image" src="https://github.com/user-attachments/assets/2c30b147-d2e5-4826-9fea-dfc58bde7e0c" />

### 장점 요약
1. 역할 분리 (controller -> 요청/응답, service -> 비즈니스 로직 (DB 저장 전 전처리 느낌), Repository -> DB 접근, entity class 사용)
2. 유지 보수의 이점
3. 단위 테스트 이점

## Swagger 테스트
<img width="831" height="435" alt="image" src="https://github.com/user-attachments/assets/7c44c511-6c11-4bf6-bd53-25ccbdd02cd6" />

<img width="553" height="325" alt="image" src="https://github.com/user-attachments/assets/50c2f3fa-4030-41cb-b459-f440c611c506" />

## 추후 진행할 내용
* kafka를 통한 메시지 큐 추가
* frontend와 연결
* 단위 테스트 추가
