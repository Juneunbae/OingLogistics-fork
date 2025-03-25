# OingLogistics

![{F0676DB8-BDC7-4B11-8FA6-BD27D6A7F8AB}](https://github.com/user-attachments/assets/a9d24437-bf04-4b2e-83d2-50d9712e5608)


OingLogistics는 MSA(Microservices Architecture) 기반의 B2B 물류 관리 플랫폼으로,
다양한 기업 간의 물류 흐름을 효율적으로 처리하고 모듈화된 아키텍처를 통해 유연한 확장성과 유지보수성을 지향합니다.
도메인 주도 설계(DDD)를 적용하여 각 비즈니스 도메인을 명확히 분리하고,
서비스 간 통신은 FeignClient와 RabbitMQ를 혼합 활용하여 상황에 따라 최적화된 방식으로 데이터 흐름을 처리하고 있습니다.


## :walking: 프로젝트 소개

### 서비스 목표

- **B2B 환경에 최적화된 주문-배송-재고 관리 시스템 구축**  
  - 다양한 기업 고객을 대상으로 주문 접수, 물류 허브 이동, 배송 추적, 재고 모니터링 등 전 과정을 통합 관리할 수 있는 플랫폼을 제공합니다.

- **실시간 알림 자동화**
	- 배송 시작 및 도착, 배송 담당자 배정 등 주요 이벤트 발생 시 Slack 연동을 통한 실시간 알림 기능을 제공하여, 이벤트 추적과 운영 효율성을 높였습니다.

- **확장 가능하고 유연한 구조**
	- 도메인별 마이크로서비스 아키텍처를 기반으로 설계하여, 새로운 기능이나 비즈니스 요구사항에 대한 빠른 대응과 유연한 확장을 가능하게 하였습니다.

### 기술적 목표

- **서비스 간 유연한 통신 전략 구현**
	- 요청-응답 기반 통신은 FeignClient, 비동기 이벤트 기반 처리는 RabbitMQ를 활용하여 상황에 따라 최적의 방식으로 서비스 간 통신을 구성하였습니다.

- **역할 기반 권한 처리 및 접근 제어**
  - 인증(Authentication)은 API Gateway에서 처리하고, 인가(Authorization)는 각 마이크로서비스 내부에서 처리하도록 분리 설계하였습니다.
  - 사용자 요청 시 Http Header와 Redis에 저장된 로그인 정보를 비교하여 권한 유효성을 검증합니다.
  - Spring MVC의 Interceptor의 preHandle() 단계를 활용해 Controller 진입 전 권한 확인을 수행합니다.
  - API별로 사용자 역할이 상이할 경우, **Service 레이어에서 추가적인 역할 검증** 수행하여 보안성을 강화하였습니다.

- **캐싱 및 성능 최적화**
	- 사용자 로그인 정보 및 인증 관련 데이터는 Redis에 캐싱하여 분산 환경에서도 안정적인 세션 관리와 빠른 인증 처리를 가능하게 하였습니다.
	- **조회 빈도가 높은 정적 데이터(예: 업체 정보, 허브 목록 등)**는 Caffeine Cache를 이용한 로컬 캐싱으로 처리하여 응답 속도와 처리 성능을 크게 향상시켰습니다.
	- 두 캐시 전략을 역할 및 데이터 특성에 따라 분리 적용함으로써, 서비스의 응답성, 확장성, 효율성을 모두 고려한 최적의 캐싱 구조를 구성하였습니다.

- **DB 성능 최적화**
	- QueryDSL을 활용한 동적 쿼리를 통해 복잡한 조건 검색에서도 안정적인 성능을 유지하였습니다.
	- 동일 Aggregate에 속한 엔티티 간 조인 시, N+1 문제를 방지하기 위해 IN 절 및 LEFT JOIN 전략을 활용하였습니다.

- **코드 재사용성과 유지보수성 향상**  
  - 여러 명의 백엔드 개발자가 협업할 때, **공통 유틸리티 및 서비스 계층을 적극 활용하여 중복 코드 최소화** 에 초점을 두고자 합니다.
  - SOLID 원칙과 DRY(Don’t Repeat Yourself) 원칙을 기반으로 객체지향적인 설계를 지향하였습니다.
  - 각 기능은 **단일 책임 원칙(Single Responsibility Principle)**에 따라 모듈화하여 유지보수성을 높였습니다.
  - **코드 컨벤션 및 패턴을 통일**하여 일관성 있는 코드베이스를 유지하였습니다.


## :memo: 개발 노션 및 산출물

[프로젝트 설계 및 구현 산출물](https://github.com/5ingMaryho/OingLogistics/wiki)

## :construction_worker: 팀원 역할 분담

| 김기훈 | 이예본 | 이지언 | 이하은 | 전은배 |
|--------|--------|--------|--------|--------|
| ![김기훈](https://github.com/user-attachments/assets/84fb1bd5-32f5-4cce-bc65-3f6daf89c144) | ![이예본](https://github.com/user-attachments/assets/24347135-3c55-409f-af30-81bf898b2c6e) | ![이지언(팀장)](https://github.com/user-attachments/assets/bf8f40c5-89ea-4945-a3eb-c690fb62c145) | ![이하은](https://github.com/user-attachments/assets/220ea7de-5ba8-4d4c-841b-cac92aa5a648) | ![전은배](https://github.com/user-attachments/assets/e418f56b-5e91-4046-9f79-a6482f0237e7)
| (사용자, 슬랙, Gateway) | (멀티 모듈, 업체, 상품) | (허브, infra 관리, Docker) | (배송, 담당자 관리, Config) | (주문, DB관리, 캐싱) |
| [GitHub](https://github.com/oneul0) | [GitHub](https://github.com/ybon1107) | [GitHub](https://github.com/Leejieon) | [GitHub](https://github.com/haisley77) | [GitHub](https://github.com/Juneunbae) |


## :calendar: 개발 기간

2025.03.10 - 2025.03.25 (2주)

## :hammer_and_wrench: 사용 기술

### 🛠 버전


| 분류               | 상세                                      |
|--------------------|-------------------------------------------|
| **IDE**            | IntelliJ IDEA                             |
| **Language**       | Java 17                                   |
| **Framework**      | Spring Boot 3.4.3                         |
| **Build Tool**     | Gradle 8.1                                |
| **Database**       | PostgreSQL 17                             |
| **In-Memory DB**   | Redis                                     |
| **Local Cache**    | Caffeine                                  |
| **Message Queue**  | RabbitMQ                                  |
| **API 통신**       | REST API, FeignClient                     |
| **컨테이너화**     | Docker                                    |


### 🛠 주요 기술 스택

| 분류               | 상세                                      |
|--------------------|-------------------------------------------|
| **Backend**            | <img src="https://img.shields.io/badge/java-007396?style=flat-square&logo=java&logoColor=white"/> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=SpringBoot&logoColor=white"/> <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=flat-square&logo=springsecurity&logoColor=white"/> <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=flat-square&logo=rabbitmq&logoColor=white"/>                           |
| **Database**       | <img src="https://img.shields.io/badge/postgresql-4169E1?style=flat-square&logo=postgresql&logoColor=white"/> <img src="https://img.shields.io/badge/redis-FF4438?style=flat-square&logo=redis&logoColor=white">                                  |
| **Version & Issue**      | <img src="https://img.shields.io/badge/git-F05032?style=flat-square&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=flat-square&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=flat-square&logo=notion&logoColor=white">                       |
| **Infra**     | <img src="https://img.shields.io/badge/docker-2496ED?style=flat-square&logo=docker&logoColor=white">                              |



## :building_construction: 아키텍처

![Image](https://github.com/user-attachments/assets/fe26d45d-a856-4ee3-a670-9c4f9814ce41)

## :memo: ERD

![Image](https://github.com/user-attachments/assets/5d41c648-9bf0-4d0c-8e56-6109679b2122)


## :movie_camera: 기능

### :memo: 시퀀스 다이어그램

![Image](https://github.com/user-attachments/assets/faeb0ffd-998b-4249-947a-7607930f91f2)

### :desktop_computer: 프로젝트 주요 기능

<details>
<summary>업체 (delivery-service)</summary>

 ### Company (업체 관리)

- **CompanyAdminController (/admin/v1/companies)**
    - 새로운 업체 생성
    - 업체 목록 전체 조회
    - 특정 업체 상세 조회
    - 특정 업체 정보 수정
    - 특정 업체 삭제
- **CompanyController (/api/v1/companies)**
    - 새로운 업체 생성
    - 업체 목록 전체 조회
    - 특정 업체 상세 조회
    - 특정 업체 정보 수정
    - 특정 업체 삭제
- **CompanyFeignClientController (/company-service/companies)**
    - 특정 업체 상세 조회
      
</details>
<details>
<summary>배송 (delivery-service)</summary>

 ### Delivery (배송 관리)

- **DeliveryAdminController (/admin/v1/deliveries)**
    - 새로운 배송 생성 (테스트 용도, 메시지큐 도입으로 사용 중단)
    - 특정 배송 정보 수정
    - 특정 배송 상태 수정
    - 특정 배송 삭제
    - 특정 배송 상세 조회
    - 배송 목록 전체 조회
    - 특정 배송 경로 상세 조회
    - 특정 배송의 경로 목록 조회
    - 특정 배송 경로 상태 수정
- **DeliveryController (/api/v1/deliveries)**
    - 특정 배송 정보 수정
    - 특정 배송 상태 수정
    - 특정 배송 삭제
    - 특정 배송 상세 조회
    - 배송 목록 전체 조회
    - 특정 배송 경로 상세 조회
    - 특정 배송의 경로 목록 조회
    - 특정 배송 경로 상태 수정
- **DeliveryManagerController (/api/v1/deliveries/managers)**
    - 특정 배송 담당자 상세 조회
    - 특정 배송 담당자 전체 조회
- **DeliveryManagerAdminController (/admin/v1/deliveries/managers)**
    - 특정 배송 담당자 상세 조회
    - 특정 배송 담당자 전체 조회
      
</details>
<details>
<summary>허브 (delivery-service)</summary>

 ### Hub (허브 관리)

- **HubAdminController (/admin/v1/hubs)**
    - 특정 허브 상세 조회
    - 허브 목록 전체 조회
    - 새로운 허브 생성
    - 특정 허브 정보 수정
    - 특정 허브 삭제
- **HubController (/api/v1/hubs)**
    - 허브 목록 전체 조회
    - 특정 허브 상세 조회
- **HubFeignClientController (/hub-service)**
    - managerId로 허브 정보 조회
    - 최적의 허브 경로 조회
    - 특정 허브 상세 조회
- **HubRouteAdminController (/admin/v1/hub-routes)**
    - 새로운 허브 경로 생성
    - 특정 허브 경로 상세 조회
    - 허브 경로 목록 전체 조회
    - 특정 허브 경로 정보 수정
    - 특정 허브 경로 삭제
      
</details>
<details>
<summary>주문 (delivery-service)</summary>

 ### Order (주문 관리)

- **OrderAdminController (/admin/v1/orders)**
    - 주문 목록 전체 조회
    - 새로운 주문 생성
    - 특정 주문 상세 조회
    - 특정 주문 정보 수정
    - 특정 주문 삭제
    - 특정 주문의 상세 주문 삭제
- **OrderController (/api/v1/orders)**
    - 주문 목록 전체 조회
    - 특정 주문 상세 조회
    - 새로운 주문 생성
    - 특정 주문 정보 수정
    - 특정 주문 삭제
    - 특정 주문의 상세 주문 삭제
- **OrderFeignClientController (/order-service/orders)**
    - 특정 주문 조회
      
</details>
<details>
<summary>상품 (delivery-service)</summary>

 ### Product (상품 관리)

- **ProductAdminController (/admin/v1/products)**
    - 새로운 상품 등록
    - 상품 목록 전체 조회
    - 특정 상품 상세 조회
    - 특정 상품 정보 수정
    - 특정 상품 삭제
- **ProductController (/api/v1/products)**
    - 새로운 상품 등록
    - 상품 목록 전체 조회
    - 특정 상품 상세 조회
    - 특정 상품 정보 수정
    - 특정 상품 삭제
- **ProductFeignClientController (/product-service/products)**
    - 특정 상품 상세 조회
      
</details>
<details>
<summary>알림 (delivery-service)</summary>

 ### Slack (슬랙 메시지 관리)

- **SlackAdminController (/admin/v1/slack-messages)**
    - 슬랙 메시지 목록 전체 조회
    - 특정 슬랙 메시지 상세 조회
    - 새로운 슬랙 메시지 생성
    - 특정 슬랙 메시지 수정
    - 특정 슬랙 메시지 삭제
- **SlackController (/api/v1/slack-messages)**
    - 새로운 슬랙 메시지 생성 (발송)
      
</details>
<details>
<summary>사용자 (delivery-service)</summary>

 ### User (사용자 관리)

- **UserAdminController (/admin/v1/users)**
    - 사용자 회원가입
    - 사용자 로그인
    - 사용자 로그아웃
    - 새로운 사용자 생성
    - 특정 사용자 조회
    - 사용자 목록 전체 조회
    - 특정 사용자 정보 수정
    - 특정 사용자에게 권한 부여
    - 특정 사용자 권한 업데이트
    - 특정 사용자 권한 삭제
    - 특정 사용자 삭제
    - 슬랙 인증 코드 요청
    - 슬랙 인증 코드 확인
- **UserController (/api/v1/users)**
    - 사용자 회원가입
    - 사용자 로그인
    - 사용자 로그아웃
    - 특정 사용자 조회
    - 슬랙 인증 코드 요청
    - 슬랙 인증 코드 확인
- **UserFeignClientController (/user-service/users)**
    - 역할별 사용자 목록 조회
    - 배송 서비스 요청으로 역할별 사용자 맵 조회
    - 사용자 ID로 역할 조회
    - 사용자 ID로 슬랙 ID 조회
    - 사용자 ID로 사용자 이름 조회
      
</details>

## :computer: 트러블슈팅

1. [Multi‐Stage Build 를 적용한 도커 이미지 경량화](https://github.com/5ingMaryho/OingLogistics/wiki/%F0%9F%90%B3Multi%E2%80%90Stage-Build-%EB%A5%BC-%EC%A0%81%EC%9A%A9%ED%95%9C-%EB%8F%84%EC%BB%A4-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B2%BD%EB%9F%89%ED%99%94)

2. [Interceptor와 Annotation을 활용한 각 서비스 API 별 권한 검증](https://github.com/5ingMaryho/OingLogistics/wiki/%F0%9F%91%A5Interceptor%EC%99%80-Annotation%EC%9D%84-%ED%99%9C%EC%9A%A9%ED%95%9C-%EA%B0%81-%EC%84%9C%EB%B9%84%EC%8A%A4-API-%EB%B3%84-%EA%B6%8C%ED%95%9C-%EA%B2%80%EC%A6%9D)

3. [멀티 모듈 구조 적용](https://github.com/5ingMaryho/OingLogistics/wiki/%F0%9F%A7%A9%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88-%EA%B5%AC%EC%A1%B0-%EC%A0%81%EC%9A%A9)

4. [동시성 제어와 이벤트 리스너 트랜잭션 관리](https://github.com/5ingMaryho/OingLogistics/wiki/%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%A0%9C%EC%96%B4%EC%99%80-%EB%B6%84%EC%82%B0%EB%9D%BD,-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EB%B6%84%EB%A6%AC)

## :package: 프로젝트 구동 방법

### 1. Clone Project

```
git clone https://github.com/5ingMaryho/OingLogistics.git
```

### 2. Change path to /OingLogistics & execute docker-compose.yml file

```
docker compose up -d
```


## :memo: 프로젝트 회고

### 프로젝트 개선점 및 고도화 계획
- 주문 - 상품 동시성 제어
- 분산 환경에서의 데이터 정합성과 부하를 최소화하는 방안을 탐색

### 협업 시 우리 팀이 잘한 점
- 문제나 궁금한 점을 바로 공유하고 함께 해결 방법을 고민하며 함께 성장하는 팀 분위기를 만들었습니다.
- PR 규칙을 정하고 알고 있는 내용, 배운 내용, 고민사항, 설계를 적극적으로 공유하여 팀 전체의 개발 역량을 향상시켰습니다.

### 협업 시 아쉽거나 부족했던 부분들
- 트러블 슈팅 기록과 오류 정리를 체계화해 유사한 문제가 발생했을 때 원인 파악과 해결 시간을 단축하고자 합니다.
- API 구현 및 새로운 기술에 대한 러닝커브로 예상보다 많은 시간이 소요되어 테스트 코드 작성이나 개발 진행이 지연되었습니다. 깃허브 이슈 관리를 통해, 이런 상황을 최소화하고자 노력했습니다.


