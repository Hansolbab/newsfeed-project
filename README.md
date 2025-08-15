# 📷 Newsfeed Project

> 인스타그램을 벤치마킹하여 음식 레시피 공유에 특화된 소셜 뉴스피드 백엔드 서비스입니다.


## 👥 팀원 소개

| 이름  | 역할                    | GitHub                                    |
|-----|-----------------------|-------------------------------------------|
| 곽지훈 | 핵심 콘텐츠(피드/댓글) 데이터 흐름 관리 시스템 및 접근 제어 로직 설계/구현     | [github.com/Gwakjihun](https://github.com/Gwakjihun) |
| 전재민 | Toggle 기능 이용 사용자 관계 관리 시스템 구축 (팔로우 서비스) 및 콘텐츠 분류 및 관리 시스템 구축 | [github.com/Beforejamni](https://github.com/Beforejamni) |
| 최재혁 | 사용자 활동 지표(좋아요,댓글) 관리 및 통합 검색, 개인화된 정보 조회 시스템 개발  | [github.com/Gemini-kei](https://github.com/Gemini-kei) |
| 최한솔 | Security & JWT Token Auth System 구축, 테스트 진행 후 기록 및 관련 영상 제작 | [github.com/hansolChoi29](https://github.com/hansolChoi29) |


## 🚀 프로젝트 개요

- **사용 기술**: Java 17, Spring Boot 3, MySQL, JPA, Gradle, Postman, Tomcat
- **협업 도구**: GitHub, Notion, Figma, ERDcloud
- **기능**
  - 회원가입 / 로그인 / 로그아웃
  - 비밀번호 변경, 전화번호 변경, 프로필 이미지 변경
  - 게시글 작성, 수정, 삭제
  - 다중 이미지 업로드 및 관리
  - 게시글별 댓글 관리 (작성자/접근권한 기반)
  - 개인 계정 공개 범위에 따른 팔로우 요청/수락/거절/취소
  - 내 댓글 활동 / 내 좋아요 게시글 조회
  - 다른 유저 프로필 조회
  - 게시글, 프로필 공개범위 설정 (전체 공개 / 팔로워에게만 공개 / 나만 보기)

## 목차

1. [🔧 환경 설정 방법](#-환경-설정-방법)
2. [필수 설치](#필수-설치)
3. [MySQL Database 생성](#mysql-database-생성)
4. [application.properties 생성](#applicationproperties-생성)
5. [📝 브랜치 컨벤션](#-브랜치-컨벤션)
6. [📋 커밋 컨벤션](#-커밋-컨벤션)
7. [🙌 협업 규칙](#-협업-규칙)
8. [🎈 API명세서 및 와이어프레임, ERD, 트리구조](#-api명세서-및-와이어프레임-ERD-트리구조)


## 🔧 환경 설정 방법

### 필수 설치

- **Postman**
- **Java** 17
- **Spring Boot** 3.5.4
- **MySQL : 8.0.42**
- **의존성 (dependency):**

  | 분류        | 의존성 이름                 | 설명                        |
    |-----------|------------------------|---------------------------|
  | 🌐 웹      | `Spring Web`           | REST API 개발용              |
  | 🛢 데이터    | `Spring Data JPA`      | JPA ORM 사용                |
  | 🐬 데이터베이스 | `MySQL Driver`         | MySQL 연동용 JDBC 드라이버       |
  | 🔒 인증/보안  | `Spring Security`      | JWT 인증 구현 시 필수            |
  | 💾 파일업로드  | `Spring Boot DevTools` | 개발 시 핫 리로딩 등              |
  | 🔍 검증     | `Validation`           | @Valid, @NotNull 등 유효성 검사 |
  | 🛠️ 개발도구      | `lombok`                | 코드 간소화                    |

### MySQL Database 생성

```sql
CREATE DATABASE hansolbab;
```
### application.properties 생성
📁 경로: src/main/resources/application.properties

⚠️ .gitignore에 등록되어 있어 직접 생성해야 합니다.
src/main/resources/application.properties 확인

🔐 Git 보안 설정
application.properties은 절대 Git에 업로드하지 않습니다.

.gitignore에 민감 정보 추가:

application.properties

.env 등





## 📝 브랜치 컨벤션
- main: 배포 브랜치

- dev: 통합 개발 브랜치

- feature/{기능명}: 기능 단위 브랜치

예시: feature/user-signup, feature/feed-crud

## 📋 커밋 컨벤션

| 이모지 | 타입      | 설명                               |
|--------|-----------|------------------------------------|
| ✨     | feat      | 해당 파일에 새로운 기능이 생김       |
| 🎉     | add       | 없던 파일을 생성함, 초기 세팅          |
| 🐛     | fix       | 버그 수정                          |
| ♻️     | refactor  | 코드 리팩토링                      |
| 🚚     | move      | 파일 옮김/정리                     |
| 🔥     | delete    | 기능/파일 삭제                     |
| ✅     | test      | 테스트 코드 작성                   |
| 🙈     | gitfix    | gitignore 수정                     |
| 🔨     | script    | build.gradle 변경, docker compose 변경 |
| 📝     | chore     | 주석 추가 및 수정, 변수명 및 클래스명 수정 |
| ⚡️     | improve   | 개선                               |

## 🙌 협업 규칙
기능 단위로 PR 작성

코드 리뷰어는 2명 모두 승인 시 merge

일일 커밋 후 PR 리뷰 요청


## 📮 API명세서 및 와이어프레임, ERD, 트리구조

<details>

<summary>API명세서 및 와이어프레임</summary>

- [API 명세 보기](https://www.notion.so/teamsparta/API-Auth-User-Newsfeed-Comment-Like-Follow-2402dc3ef51481efb322e0e4143bb9d3?source=copy_link) </br>
- [와이어프레임](https://www.figma.com/design/9s9IO9PjGv81v5QjRcTJK3/Untitled?node-id=108-1652&t=PAvngIzkgPs9dNGL-1)


</details>
<details>
  <summary>ERD</summary>

<img width="757" height="735" alt="Image" src="https://github.com/Hansolbab/newsfeed-project/issues/86#issue-3325623507" />
</details> 

<details>

  <summary>프로젝트 구조</summary>


```
com.example.newsfeedproject/
├── NewsfeedProjectApplication.java
│
├── auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── signin/
│   │   │   ├── SignInRequestDto.java
│   │   │   └── SignInResponseDto.java
│   │   └── signup/
│   │       └── SignUpRequestDto.java
│   ├── impl/
│   │    ├──  UserDetailsImpl.java
│   │    └── UserDetailsServiceImpl.java
│   └── service/
│       ├── signin/
│       │   └── SignInService.java
│       └── signup/
│           └── SignUpService.java
│
├── category/
│   ├── controller/
│   │   └── CategoryController.java
│   ├── entity/
│   │   ├── Category.java
│   │   └── CategoryEntity.java
│   └── service/
│       └── CategoryService.java
│
├── comment/
│   ├── controller/
│   │   └── CommentsController.java 
│   ├── dto/
│   │   ├── CommentResponseDto.java
│   │   ├── CreateCommentRequestDto.java
│   │   └── UpdateCommentRequestDto.java 
│   ├── entity/
│   │   └── Comments.java
│   ├── repository/
│   │   └── CommentsRepository.java 
│   └── service/
│       └── CommentsService.java 
│
├── common/
│   ├── config/
│   │   ├── AuthConfig.java
│   │   ├── JwtConfig.java
│   │   └── SecurityConfig.java
│   ├── dto/
│   │   ├── PrincipalRequestDto.java
│   │   ├── ReadUsersFeedResponseDto.java
│   │   └── ReadUserSimpleResponseDto.java
│   ├── exception/
│   │   ├── auth
│   │   │   ├── AuthErrorCode.java
│   │   │   └── AuthErrorException.java
│   │   ├── comment
│   │   │   ├── CommentErrorCode.java
│   │   │   └── CommentErrorException.java
│   │   ├── feeds
│   │   │   ├── FeedsErrorCode.java
│   │   │   └── FeedsErrorException.java
│   │   ├── follow
│   │   │   ├── FollowErrorCode.java
│   │   │   └── FollowErrorException.java
│   │   ├── users
│   │   │   ├── UserErrorCode.java
│   │   │   └── UsersErrorException.java
│   │   ├── ErrorExeption.java
│   │   ├── ErrorResponseDto.java
│   │   └── GlbalExceptionHandeler.java
│   ├── filter/
│   │   └── JwtAuthorization.java
│   └── util/
│       └── JwtUtil.java
│
├── feedimg/
│   ├── entity/
│   │   └── FeedImage.java
│   └── repository/
│       └── FeedImgRepository.java
│
├── feeds/
│   ├── controller/
│   │   ├── FeedsController.java
│   │   └── FeedsLikeController.java
│   ├── dto/
│   │   ├── CreateFeedsRequestDto.java
│   │   ├── CreateFeedResponseDto.java
│   │   ├── FeedsResponseDto.java
│   │   ├── ReadFeedsResponseDto.java
│   │   └── UpdateFeedsRequestDto.java
│   ├── entity/
│   │   └── Feeds.java
│   ├── repository/
│   │   └── FeedsRepository.java
│   └── service/
│       ├── FeedsLikeService.java
│       └── FeedsService.java
│
├── follow/
│   ├── entity/
│   │   └── FollowsController.java
│   ├── dto/
│   │   ├── FollowResponseDto.java
│   │   └── ReadFollowUsersDto.java
│   ├── entity/
│   │   ├── Follows.java
│   │   └── FollowsStatus.java
│   ├── repository/
│   │   └── FollowsRepository.java
│   └── service/
│       └── FollowService.java
│
├── likes/
│   ├── entity/
│   │   └── Likes.java
│   └── repository/
│       └── LikesRepository.java
│
├── myinfo/
│   ├── controller/
│   │   ├── MyInfoController.java
│   │   └── MyInfoModifyController.java
│   ├── dto/
│   │   ├── ResetPasswordRequestDto.java
│   │   ├── UpdateProfileImageRequestDto.java
│   │   ├── UpdatePhoneNumberRequestDto.java
│   │   └── WithdrawAccountRequestDto.java
│   └── service/
│       ├── MyInfoService.java
│       ├── ProfileImageService.java
│       ├── ResetPasswordService.java
│       ├── UpdatePhoneNumberService.java
│       └── WithdrawAccountService.java
│
├── requestfollow/
│   ├── controller/
│   │   └── RequestFollowController.java
│   ├── dto/
│   │   ├── ReadMyRequestResponseDto.java
│   │   ├── ReadRequestFollowUsersDto.java
│   │   └── RequestFollowResponseDto.java
│   ├── entity/
│   │   └── RequestFollows.java
│   ├── repository/
│   │   └── RequestFollowRepository.java
│   └── service/
│       └── RequestFollowService.java
│
└── users/
    ├── controller/
    │   └── UsersController.java
    ├── dto/
    │   └── LikesInfoDto.java
    ├── entity/
    │   ├── AccessAble.java
    │   └── Users.java
    ├── repository/
    │   └── UsersRepository.java
    └── service/
        └── UsersService.java

```
</details>