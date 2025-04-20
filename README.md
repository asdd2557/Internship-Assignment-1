#  Spring Boot JWT 인증 시스템

JWT 기반 사용자 인증 + 역할(Role) 기반 권한 제어 API

- **서버 주소**: `http://52.78.155.54`  
- **데이터 저장소**: 메모리 기반 (DB 미사용)  
- **토큰 만료시간**: 2시간  
- **인증 방식**: Bearer Token

---

##  기술 스택

- Java 17  
- Spring Boot 3.2.5  
- Spring Security  
- JWT  
- SpringDoc Swagger (v2.2.0)

---

##  API 명세

###  회원가입

- **URL**: `POST /signup`

- **Request Body**
```json
{
  "username": "JIN HO",
  "password": "12341234",
  "nickname": "Mentos"
}
```

- **성공 응답**
```json
{
  "username": "JIN HO",
  "nickname": "Mentos",
  "roles": [
    { "role": "USER" }
  ]
}
```

- **실패 응답 (이미 존재하는 유저)**
```json
{
  "error": {
    "code": "USER_ALREADY_EXISTS",
    "message": "이미 가입된 사용자입니다."
  }
}
```

---

###  로그인

- **URL**: `POST /login`

- **Request Body**
```json
{
  "username": "JIN HO",
  "password": "12341234"
}
```

- **성공 응답**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

- **실패 응답 (잘못된 정보)**
```json
{
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다."
  }
}
```

---

###  관리자 권한 부여

- **URL**: `PATCH /admin/users/{userId}/roles`

- **Headers**
```
Authorization: Bearer {access_token}
```

- **성공 응답**
```json
{
  "username": "JIN HO",
  "nickname": "Mentos",
  "roles": [
    { "role": "ADMIN" }
  ]
}
```

- **실패 응답 (권한 없음)**
```json
{
  "error": {
    "code": "ACCESS_DENIED",
    "message": "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."
  }
}
```

- **실패 응답 (유효하지 않은 토큰)**
```json
{
  "error": {
    "code": "INVALID_TOKEN",
    "message": "유효하지 않은 인증 토큰입니다."
  }
}
```

---

##  Swagger UI

- 접속 경로: [http://0.0.0.0:8080/swagger-ui.html](http://0.0.0.0:8080/swagger-ui.html)

---

##  테스트 가이드

1. `/signup` 으로 회원가입  
2. `/login` 으로 토큰 발급  
3. Authorization 헤더에 `Bearer {token}` 추가 후 인증된 API 호출  
4. `/admin/users/{id}/roles`로 관리자 권한 부여 테스트

---
