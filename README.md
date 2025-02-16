e-commerce project, msa oriented project

# 프로젝트 소개
- MSA를 지향하는 Spring Boot 기반 E-Commerce 프로젝트
- 사용자는 이메을을 통한 회원가입을 진행 및 정보 확인
- 역할에 따라 상품 등록 및 구매, 상품 정보 확인, 장바구니 등록 및 조회 경험
- 해당 프로젝트는 동시다발적으로 들어오는 주문 요청을 적절히 제어하고, Redis Caching을 통해 재고 처리 시스템을 개발을 목표

# 사용 기술
- Java (JDK 21), Kotlin, Spring Boot 3.2.8, Security 6.3.3, Cloud 2023.0.1, Data JPA 3.3.3, Data Redis 3.3.3
- MySQL 8.0, Redis 7.2.5
- JWT 0.11.5
- Netflix Eureka

# 프로젝트 아키텍처
### 서비스 아키텍처
![제목 없는 프레젠테이션](https://github.com/user-attachments/assets/4490d2ac-97e3-4b71-9ffc-7431f73ac54e)
 - 설명 : 마아크로 서비스 구성된 서버, 각 독립적인 API서비스로 정의, HTTP API 통신을 통해 마이크로 서비스 간 통신
 - redis를 통한 장바구니 및 재고 데이터 처리
 - kafka 이벤트 발행으로 주문-결제 프로세스 비동기 통신

#### 시큐리티 아키텍처
#### 메일 인증 프로세스
```mermaid
sequenceDiagram
    actor User 
    participant Application
    participant MailgunClient
    participant Mailgun

    User->>Application: 회원가입 요청
    Application->>Application: 사용자 정보 처리
    Application->>MailgunClient: 인증 이메일 발송 요청
    MailgunClient->>Mailgun: 이메일 발송
    Mailgun-->>MailgunClient: 발송 완료
    MailgunClient-->>Application: 발송 결과
    Application-->>User: 회원가입 완료 응답

    User->>Application: 이메일 인증 요청
    Application->>Application: 인증 처리
    Application-->>User: 인증 완료 응답
```
#### 결제 프로세스
#### 반품 프로세스

# 주요 기능
- **Netflix Eureka**를 이용한 Client-side Discovery 방식의 Service Discovery 구현
  - 마이크로서비스 간의 서비스 등록 및 발견, 서비스별로 개별적인 로드 밸런싱
- **API Gateway**를 통해 클라이언트 요청 라우팅, 인증/인가 및 보안처리
  - 클라이언트의 요청을 적절한 마이크로서비스로 분산
  - Spring Webflux Security, JWT AccessToken/RefreshToken을 통한 요청 인증/인가
- **OpenFeign**을 통한 마이크로서비스 간 통신 구현
  - 서비스 간의 통신이 빈번한 MSA 환경에서 통신을 간결하하고 효율적으로 처리
- **외부 API**을 사용하여 **이메일 인증**을 통한 회원가입
  - Mailgun과 연동하여 회원가입 시 이메일 인증 링크 발송
  - Feign Client 사용하여 Maligun 서버와 통신
- **Redis** 활용하여 장바구니 데이터를 관리
  - Redis를 통해 저장된 장바구니 데이터를 조회하고 최신 상태로 업데이트
- ~~KafKa를 사용한 비동기 이벤트 처리~~
  - ~~비동기식으로 주문 생성, 결제 성공 이벤트를 발행하고, 장애 시 데이터 유실에 대한 안전 보장~~
- ~~Redis 분산락 사용하여 재고 동시성 이슈 관리~~

<br>

- 성능 개선 및 트러블슈팅
- 기술적 의사 결정
  - Feign Client 선택 이유 -> 간결하고 직관적인 사용법 + Spring Cloud와의 통합 용이성
  - 장바구니 redis에 담은 이유
- api 명세 : 포스트맨 api 명세 혹은 notion
- erd : 계속해서 바뀌기 때문에 아래로 
