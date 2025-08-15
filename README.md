# 📷 Newsfeed Project

> 인스타그램을 클론한 뉴스피드 중심의 음식 레시피 소셜 네트워크 백엔드 서비스입니다.

---

## 👥 팀원 소개

| 이름  | 역할            | GitHub                                    |
|-----|---------------|-------------------------------------------|
| 곽지훈 | 게시글, 댓글, 카테고리 | [github.com/Gwakjihun](https://github.com/Gwakjihun) |
| 전재민 | 프로필, 내 활동 조회  | [github.com/Beforejamni](https://github.com/Beforejamni) |
| 최재혁 | 유저, 검색, 팔로우   | [github.com/Gemini-kei](https://github.com/Gemini-kei) |
| 최한솔 | 인증, 인가, auth  | [github.com/hansolChoi29](https://github.com/hansolChoi29) |


---

## 🚀 프로젝트 개요

- **사용 기술**: Java 17, Spring Boot 3, MySQL, JPA, Gradle, Postman, Tomcat
- **협업 도구**: GitHub, Notion, Figma, ERDcloud
- **기능**
    - 회원가입 / 로그인 / 로그아웃
    - 게시글 작성, 수정, 삭제
    - 이미지 업로드
    - 댓글 작성, 수정, 삭제
    - 팔로우 / 언팔로우
    - 마이페이지 및 내 활동 조회
    - 다른 유저 프로필 조회

---

## 🧱 프로젝트 구조

```
com.example.newsfeedproject
├── auth                      # 인증/인가
├── myinfo                    # 마이페이지 도메인
├── users                     # 유저 도메인
├── feeds                     # 게시글 도메인
├── category                  # 카테고리 도메인
└── NewsfeedApplication.java

```


---

## 🛠️ 환경 설정 방법 (로컬 개발용)

### 1️⃣ 필수 설치

- Postman- **Java** 17
- **Spring Boot** 3.5.4
- **MySQL : 8.0.42**
- **의존성 (dependency):**

  | 분류         | 의존성 이름                           | 설명                           |
  | ---------   | ---------------------------------- | --------------------------    |
  | 🌐 웹        | `Spring Web`                       | REST API 개발용                |
  | 🛢 데이터     | `Spring Data JPA`                  | JPA ORM 사용                   |
  | 🐬 데이터베이스 | `MySQL Driver`                     | MySQL 연동용 JDBC 드라이버        |
  | 🔒 인증/보안   | `Spring Security`                  | JWT 인증 구현 시 필수             |
  | 💾 파일업로드   | `Spring Boot DevTools` (선택)       | 개발 시 핫 리로딩 등              |
  | 🔍 검증       | `Validation`                       | @Valid, @NotNull 등 유효성 검사  |
  |    ++++      |  lombok, thymeleaf                |  

### 2️⃣ MySQL Database 생성

```sql
CREATE DATABASE hansolbab;
```
### 3️⃣ application.properties 생성
📁 경로: src/main/resources/application.properties

⚠️ .gitignore에 등록되어 있어 직접 생성해야 합니다.
src/main/resources/application.properties 확인

🔐 Git 보안 설정
application.properties은 절대 Git에 업로드하지 않습니다.

.gitignore에 민감 정보 추가:

application.properties

.env 등

---

### 📮 API 명세


-> [API 명세 보기](https://www.notion.so/teamsparta/API-Auth-User-Newsfeed-Comment-Like-Follow-2402dc3ef51481efb322e0e4143bb9d3?source=copy_link)


### 📝 브랜치 전략
- main: 배포 브랜치

- dev: 통합 개발 브랜치

- feature/{기능명}: 기능 단위 브랜치

예시: feature/user-signup, feature/feed-crud

### 📄 커밋 컨벤션

✨ feat &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;해당 파일에 새로운 기능이 생김</br>
🎉 add  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;없던 파일을 생성함, 초기 세팅</br>
🐛 fix	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 버그 수정</br>
♻️ refactor &nbsp;&nbsp; 코드 리팩토링</br>
🚚 move	 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 파일 옮김/정리</br>
🔥 delete &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 기능/파일 삭제</br>
✅ test	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 테스트 코드 작성 (🧪)</br>
🙈 gitfix &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; gitignore 수정</br>
🔨 script &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; build.gradle 변경, docker compose 변경</br>
📝 chore &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 주석 추가 및 수정, 변수명 및 클래스명 수정</br>
⚡️ improve &nbsp; 개선</br>


### 🗂️ ERD 및 와이어프레임
-> [ERD 보기](https://github.com/Gwakjihun/newsfeed-project/issues/1#issue-3296282885)

-> [와이어프레임 보기](https://www.figma.com/design/9s9IO9PjGv81v5QjRcTJK3/Untitled?node-id=108-1652&p=f&t=VUZgk8B7Ab3JiaL6-0)

이미지 파일 업로드 관련 경로는 추후 Nginx 또는 AWS S3로 확장 예정

### 🙌 협업 규칙
기능 단위로 PR 작성

코드 리뷰어는 2명 모두 승인 시 merge

일일 커밋 후 PR 리뷰 요청
