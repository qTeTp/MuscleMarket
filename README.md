# 머슬 마켓 **( https://muscle-market.duckdns.org/ )**

## 프로젝트 소개
운동 장비 구매/판매, 운동장 탐색, 커뮤니티 참여가 **각각 분리되어 불편한 운동인**들을 위해 “**한곳에서 운동관련된 정보를 처리할 수 있으면 어떨까?**”라는 질문에서 시작된 서비스입니다.

### 목표
- 중고 운동 용품 거래 플랫폼 제공
- 사용자의 질문에 맞춰 AI 기반 운동 용품 추천
- 운동장 검색 및 지도 표시 (주변 운동 시설 정보 제공)
- 커뮤니티 & 소셜 기능 제공

### 주요 기능

| **운동 용품 중고 거래** | **운동장 검색 / 찾기** |
|:---:|:---:|
| 운동 용품을 채팅을 통해 중고 거래 가능 | 카카오 지도를 통해 주변 운동장을 검색 |
| ![image](https://github.com/user-attachments/assets/8321fc8c-c18c-4bd7-a482-2e9968951390) | ![image](https://github.com/user-attachments/assets/42c60c51-d3a7-4ebe-8b07-7e55dcca5aff) |
| • 채팅을 통해 사용자끼리 소통 가능<br> • 게시글 상태나 거래 상태 관리 가능<br> • 찜 하기: 찜 목록에 추가 가능 | • 장소 상세 정보 제공 (장소명, 주소, 연락처 등)<br> • 번개 모임을 위한 장소 탐색으로도 적격<br> • 사용자 위치 자동 탐지 |
| | |
| **운동 용품 추천** | **소셜 네트워킹** |
| 앨런 AI로부터 운동 용품 추천 받기 | 운동을 주제로 한 커뮤니티 |
| ![image](https://github.com/user-attachments/assets/cdf274ed-6dc0-4344-a8d8-6af6f72d8525) | ![image](https://github.com/user-attachments/assets/6c2a79e1-46f8-4ba8-8980-43dcf25909fd) |
| • 판매글에 존재하는 물품이라면 링크 제공<br> • 사용자 질문에 맞춰 운동 용품 추천 <br> • 최대한 정확한 제품명 제공 | • 운동에 대한 이야기를 할 수 있는 자유로운 커뮤니티<br> • 번개 모임을 가질 수 있는 전용 게시글 작성 가능<br> • 댓글과 답글을 통해 더욱 적극적인 소통 가능 |

---

## 기술 스택

### Frontend
![JavaScript](https://img.shields.io/badge/JavaScript-ES6+-F7DF1E?style=for-the-badge&logo=javascript)
- API를 활용한 비동기 데이터 통신 및 SockJS/Stomp 클라이언트를 이용한 실시간 채팅 로직 구현

![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template-005F0F?style=for-the-badge&logo=thymeleaf)
- Layout Dialect를 활용한 공통 레이아웃(header) 관리 및 SSR 기반 뷰 템플릿 구성

![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-UI-38B2AC?style=for-the-badge&logo=tailwindcss)
- 직관적이고 빠른 반응형 디자인 구축

### Backend
![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
- RESTful API 서버 구축 및 계층형 아키텍처 설계

![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate)
- Spring Data JPA를 활용한 객체 지향적 데이터 조작, Fetch Join을 적용해 N+1 조회 성능 최적화

![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity)
- JWT 기반 Stateless 인증 시스템 구축, OAuth2(구글) 소셜 로그인 및 HttpOnly 쿠키 저장 방식 적용

### Infrastructure & DevOps
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2)
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-PostgreSQL-527FFF?style=for-the-badge&logo=amazonrds)
![AWS S3](https://img.shields.io/badge/AWS%20S3-Storage-569A31?style=for-the-badge&logo=amazons3)
- Amazon Linux 기반 애플리케이션 서버(**EC2**), 데이터 안정성을 위한 관리형 데이터베이스(**RDS/PostgreSQL**), 이미지의 효율적 저장을 위한 객체 스토리지(**S3**) 구성

![Nginx](https://img.shields.io/badge/Nginx-Reverse%20Proxy-009639?style=for-the-badge&logo=nginx)
- 리버스 프록시 구성을 통한 내부 WAS 보호, Certbot을 활용한 SSL 인증서 자동 갱신 및 80 → 443 포트 리다이렉트 처리

![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-CI%2FCD-2088FF?style=for-the-badge&logo=githubactions)
- main 브랜치 Push시 자동 빌드 및 SCP/SSH를 활용한 자동 배포 파이프라인 구축

---
## 폴더 구조
```
muscle_market/
├── scripts/                                     # 배포에 사용되는 스크립트
└── src/main/                                    # 메인 디렉토리
    ├── java/com/example/muscle_market/          # 소스 코드 디렉토리
    │   ├── config/                              # 설정 코드 (보안, 소켓 등)
    │   ├── controller/                          # 컨트롤러 (api, view)
    │   ├── domain/                              # 도메인 (엔티티)
    │   ├── dto/                                 # API를 위한 DTO
    │   ├── enums/                               # 상태를 담는 열거형 클래스
    │   ├── exceptions/                          # 커스텀 예외
    │   ├── repository/                          # JPA 레포지토리
    │   ├── service/                             # 비즈니스 로직 담당 서비스
    │   └── MuscleMarketApplication.java         # 메인 클랫스
    └── resources/                               # 리소스 디렉토리
        ├── static/                              # 정적 파일 디렉토리
        │   ├── css/                             # 스타일 파일
        │   ├── images/                          # 정적 이미지 (기본 이미지 등)
        │   └── js/                              # 정적 소스 코드
        ├── templates/                           # 뷰 템플릿
        └── ...                                  # application property yaml
```
---

## 아키텍처
![Image](https://github.com/user-attachments/assets/d02df3b0-383b-4ca3-aa8c-40edae93f808)

---

## 데이터베이스 설계 (ERD)

<img width="1360" height="803" alt="Image" src="https://github.com/user-attachments/assets/0cb9c0c7-0bd9-4310-add3-3ec9a55dd035" />

---

## 와이어프레임
피그마 "[파일][1]"을  확인해보세요

[1]: https://www.figma.com/design/5jKMFvlcHgTMvSJv9THs63/MuscleMarket?t=rMNHzmHBOggnBNKR-0 

<img width="1944" height="1438" alt="Image" src="https://github.com/user-attachments/assets/bb4c353d-5b4d-4983-8abb-ade6960ea3ef" />
<img width="1778" height="1100" alt="Image" src="https://github.com/user-attachments/assets/f51a472b-6540-48fc-a586-86517f029551" />

---

## 팀 노션 정보

머슬마켓 : https://www.notion.so/oreumi/Muscle-Market-2aeebaa8982b80c2953cfec28e27769d

---

## 팀 정보

| 이름 | 이메일 |
|------|--------|
| 유석원 | youseokwon1667@gmail.com |
| 정인웅 | dlsdnd122@naver.com |
| 김승호 | k355323@gmail.com |