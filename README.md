Spring Framework 기반 가계부 REST API 입니다.

# 기능 목록
- 회원가입
- 로그인 로그아웃(JWT)
- 가계부 생성
- 가계부 내역 작성 (수입, 지출, 금액, 메모, 날짜)
- 가계부 내역 수정 (금액, 메모)
- 가계부 내역 삭제(SOFT-DELETE)
- 가계부 내역 복구
- 내 가계부 목록 조회
- 내 가계부 중 특정 가계부에 대한 내역 조회(동적 쿼리 페이징)
  - 수입별, 지출별
  - 특정 기간별, 특정 날짜별
  - 삭제된 상태, 삭제되지 않은 상태


# ERD
<img width="907" alt="image" src="https://user-images.githubusercontent.com/62292492/192255313-96331b61-7a13-4ed9-8f5c-dcfb99537210.png">

# Docker
도커 환경에서 실행 가능합니다.

프로젝트 루트에서 다음 명령어를 입력하면 빌드 후 도커 이미지를 생성합니다.
```
sh dockerize.sh
```

다음, 다음 명령어를 입력하면 docker-compose를 활용해 mysql, mysql-php, redis, application을 실행 시킵니다.
```
docker-compose up
```

### 실행 환경 지정
docker-compose.yml의 SPRING_ACTIVE_PROFILE을 원하는 실행환경으로 변경할 수 있습니다.

<img width="278" alt="image" src="https://user-images.githubusercontent.com/62292492/192256901-57c5e8ca-591f-48ce-abb1-86deb8c15aaa.png">

# 구현 내용

## API 명세
### 회원가입
```
POST /api/users/signin
```
- Request
```json
"email": "pjh612@gmail.com",
"password": "test1234"
```
- Response

  성공
  ```json
  "data": {
      "id": 1,
  }
  ```
  
  실패(이메일 중복) - 400
  ```json
  {
      "code": "V003",
      "message": "Data integrity violation",
      "dateTime": "2022-09-26T10:58:43.945314299"

  }
  ```
  실패(잘못된 요청 입력 값)- 400
  ```json
  {
      "code": "V001",
      "message": "Validation error",
      "dateTime": "2022-09-26T11:01:13.330091424"
  }
  ```

### 로그인
```
POST /api/users/signin
```
- Request
  ```json
  "email": "pjh612@gmail.com",
  "password": "test1234"
  ```

- Response

  성공
  ```json
  "data": {
      "id": 1,
      "email": "pjh612@gmail.com",
      "role":  "USER"
  }
  ```

  실패(이메일 또는 비밀번호 불일치) - 400
  ```json
  {
      "code": "A001",
      "message": "Authentication failed",
      "dateTime": "2022-09-26T11:02:11.328203048"
  }
  ```
---

### 로그아웃
```
DELETE /api/users/signout
```
- Request
  ```
  empty body
  ```

- Response

  성공
  ```json
  "data": "signed out"
  ```

  실패(로그인되지 않음) - 401
  ```
  empty body
  ```
---

### 가계부 생성
```
POST /api/ledgers
```
- Request
  ```json
  {
    "name": "새 가계부"
  }
  ```

- Response

  성공
  ```json
  "data": {
    "id": 1
  }
  ```

---

### 내 가계부 목록 조회
```
GET /api/ledgers
```
- Request
  ```
    empty body
  ```

- Response

  성공
  ```json
  "data": [
      {
        "id": 1
        "name": "가계부 A"
      }, 
      {
        "id": 2
        "name": "가계부 B"
      },
    ]
  ```
  
---

### 가계부 내역 작성
```
POST /api/ledgers/records
```
- Request
  ```json
    "amount": 2000,
    "memo": "memo",
    "datetime": "2022-09-26T12:00:00"
    "type": "EXPENSE"
  ```

- Response

  성공
  ```json
  "data": {
    "id": 1
  }
  ```
---

### 내 가계부 중 특정 가계부에 대한 내역 조회(동적 쿼리 페이징)
```
GET /api/ledgers/{ledgerId}/records
```
- Request
  ```json
    "isRemoved": false, (Optional)
    "type": "EXPENSE", (Optional)
    "startAt": "2022-09-26T00:00:00", (Optional)
    "endAt": "2022-09-26T23:59:59" (Optional)
  ```

- Response

  성공
  ```json
   {
      "data": {
          "content": [
              {
                  "recordId": 9,
                  "amount": 2800,
                  "memo": "memoD",
                  "datetime": "2022-09-26T12:30:00",
                  "type": "EXPENSE",
                  "isRemoved": false
              }
          ],
          "pageable": {
              "sort": {
                  "empty": true,
                  "unsorted": true,
                  "sorted": false
              },
              "offset": 0,
              "pageNumber": 0,
              "pageSize": 20,
              "paged": true,
              "unpaged": false
          },
          "last": true,
          "totalPages": 1,
          "totalElements": 1,
          "size": 20,
          "number": 0,
          "sort": {
              "empty": true,
              "unsorted": true,
              "sorted": false
        },
        "first": true,
        "numberOfElements": 1,
        "empty": false
    }
  }
  ```
  
---

### 가계부 내역 상세 조회
```
GET /api/ledgers/records/{recordId}
```

- Request
  ```
  empty body
  ```

- Response

  성공
  
  ```json
  {
    "data": {
        "recordId": 9,
        "amount": 2800,
        "memo": "memoD",
        "datetime": "2022-09-26T12:30:00",
        "type": "EXPENSE",
        "isRemoved": false
    }
  }
  ```

---

### 가계부 내역 수정
```
PATCH /api/ledgers/records/{recordId}
```

- Request
  ```json
  {
    "amount": 5000,
    "memo": "updated memo"
  }
  ```

- Response

  성공
  
  ```json
  {
    "data": "updated"
  }
  ```
  
  실패(amount가 음수이거나 메모 글자 수 제한을 위반했을 때) - 400
   ```json
  {
    "code": "V002",
    "message": "Validation error",
    "dateTime": "2022-09-26T12:48:21.22186546"
  }
  ```
---
### 가계부 내역 삭제
```
DELETE /api/ledgers/records/{recordId}
```

- Request
  ```
  empty body
  ```

- Response

  성공
  
  ```json
  {
    "data": "removed"
  }
  ```
  
  실패(레코드를 찾을 수 없을 때 또는 이미 삭제된 레코드일 때) - 404
   ```json
  {
    "code": "L002",
    "message": "Not found data",
    "dateTime": "2022-09-26T12:55:10.806205802"
  }
  ```
---

### 가계부 내역 복원
```
POST /api/ledgers/records/{recordId}
```

- Request
  ```
  empty body
  ```

- Response

  성공
  
  ```json
  {
    "data": "restored"
  }
  ```
  
  실패(레코드를 찾을 수 없을 때, 또는 삭제되지 않은 레코드일 때) - 404
   ```json
  {
    "code": "L002",
    "message": "Not found data",
    "dateTime": "2022-09-26T12:55:10.806205802"
  }
  ```

## 컨벤션
### Git

**todo list**
수행해야할 구현, 리팩토링, 설정 사항들을 Git Issue에 작성합니다.
어떤 작업인지 설명 및 대략적인 방향성을 적습니다.

**브랜치명**
{작업 유형}/{이슈번호}
ex) feat/1, refactor/11

**브랜치 유형**

- 작업브랜치
  feat : 기능 작업
  refactor: 리팩토링 작업
  fix: 버그 수정 작업
  setting: 설정 작업

- 구현 완료 브랜치
  
  모든 작업 브랜치는 develop 브랜치로 merge되며 모든 검수가 완료되면 main 브랜치로 배포합니다.

- 배포 브랜치
  
  main : 최종 배포 브랜치

### Code Convention
- IDE code style 컨벤션 
  
  [네이버 캠퍼스 핵데이 Java 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/) 사용 

- 네이밍 컨벤션
  
  - DTO 
  
    Client->Controller {행위}{대상}WebRequest (ex : CreateLedgerWebRequest)
    Controller->Service {행위}{대상}Request (ex: CreateLedgerRequest)
  
  - 에러코드
  
    코드명은 대상을 식별할 수 있는 영어 대문자 한글자 + 순차적인 3자리 숫자를 사용합니다. (ex: 유저 데이터 를 찾을수 없는 예외는 User의 U와 3자리 숫자 001을 합쳐 U001로 정의합니다.)
  
- Validation  
  
  입력 값에 대한 검증은 Controller에서 BeanValidation으로 진행하고 최종적으로 (Repository)Entity에서 진행합니다.
  
- 테스트 코드
  - 접근 제한자: 편의용 메서드는 private 나머지는 접근 제한자를 붙이지 않도록 합니다.
  
  - @DisplayName: 어떤 테스트인지 식별할 수 있도록 한글로 작성합니다.
    
  - 테스트 메서드 명: 어떤 테스트인지 식별할 수 있도록 명명하고, 성공 실패 여부에 따라 Success 또는 Fail을 맨 뒤에 붙여줍니다.
  
  

## 구현 상세 
 ### - 레이어간 DTO 의존성
기존에는 Client - Controller간 사용되는 요청 DTO를 Controller - Service간 요청 DTO로 사용하고 있었습니다.
이렇게되면 각 레이어간의 강한 결합을 유발하며 API 요청 스펙이 변경되면 서비스 레이어로의 요청 스펙도 동시에 변경되야하는 문제점이 있습니다.

또한 controller에서는 단일 내부 Service만 이용하는 것이아닌 외부 API를 요청하는 등 입력값을 여러 전처리 작업 이후 Service Layer로 넘겨줄 수 있기 때문에 이상적으로는 레이어간 DTO는 분리가 되어야합니다.

그렇기 때문에 Controller로 들어오는 요청 DTO는 XXXWebRequest, Service로 들어오는 요청 DTO는 XXXRequest로 규칙을 정하고, 패키지간 의존성도 고려해 각 레이어마다 dto패키지(request, response)를 만들고 분리했습니다.

![image](https://user-images.githubusercontent.com/62292492/192295910-9544196d-84a1-4c39-b4d0-d4e5aa4bd113.png)
![image](https://user-images.githubusercontent.com/62292492/192295970-20f23a3b-5312-432e-9681-6feeb738ded8.png)



### - 가계부내역 삭제/복원 HTTP Method는 어떤 것으로?
가계부 내역 삭제 API에 대한 HTTP Method는 Patch, Delete 중 무엇을 사용할지 고려를 했고, Delete를 선택했습니다.

삭제된 데이터 복원을 위한 soft-delete 방식으로 구현을 해야 했으며 이를 위해 LedgerRecord Entity에 isRemoved라는 플래그 컬럼을 두었고, 삭제가 된다면 isRemoved만 true로 "변경" 하니까 Patch Method를 쓸 수도 있겠지만 사용자 입장에서는 API 내부 동작을 몰라도 "삭제" 라는 기능을 수행한다는 것을 알 수 있도록 DELETE를 선택했습니다.
복원도 마찬가지로 PATCH를 사용할 수 있겠지만 복원은 데이터 "재생성" 으로 바라봐야 하므로 POST를 선택했습니다.


### - ErrorCode를 예외 응답
ErrorCode를 Enum으로 정의해 애플리케이션에서 발생할 수 있는 예외들을 식별할 수 있는 Code를 정의하고 대략적인 예외에 대한 설명을 message로 정의 했습니다.

이것은 보안상 모든 파라미터에 대해 어떤 문제가 발생했는지를 보여준다면 좋지 않다고 판단되었고, 대략적인 message와 개발자끼리만 어떤 오류인지 식별할 수 있는 code를 사용하는 방식으로 응답합니다.
조금 더 디테일한 예외에 대한 정보는 에러코드에 정의되어있는 message가 아닌 예외 객체의 message에 담고, 로그를 남깁니다.

ex) API 응답으로는 U001이라는 code와 Not found Data 라는 메시지를 받게됩니다.
![image](https://user-images.githubusercontent.com/62292492/192288408-e4d348d1-326e-4df5-8c55-b7a185121629.png)

로그에는 어떤 값에 대해 찾지못했는지 조금 더 디테일한 내용을 남깁니다.
![image](https://user-images.githubusercontent.com/62292492/192288357-4d70eac8-7ae7-4cb3-b1ce-ecd9c582becc.png)


**추상화된 ErrorModel을 구현하는 ErrorCode**

다음은 ErrorModel이라는 인터페이스와 예외 발생 시 응답 객체인 ErrorResponse 입니다. ErrorResponse는 제네릭을 통해 타입을 ErrorModel 인터페이스로 제한하고 있으며, ErrorCode는 ErrorModel을 구현하고 있습니다.

  ![image](https://user-images.githubusercontent.com/62292492/192289628-30e4e54b-47b3-45b6-aedc-63d19f0823d9.png)

  ![image](https://user-images.githubusercontent.com/62292492/192290151-f4ae4630-ddf8-4081-9c13-f70b6643c2a8.png)

  ![image](https://user-images.githubusercontent.com/62292492/192290543-8089c523-ac91-44cf-907d-900968bcce2b.png)


ErrorCode는 여러 관심사에 따라 분리될 수 있다고 판단하여 같은 형식을 메서드 구현을 강제하는 ErrorModel 인터페이스를 구현하도록 했고, ErrorResponse는 ErrorCode를 사용하는 것이아닌 인터페이스인 ErrorModel을 받음으로 써 유연하게 사용 가능하도록 했습니다.


### - QueryDsl을 사용한 쿼리 최적화 및 동적 쿼리
우선 유저-가계부-가계부 내역의 연관관계에서 가계부 내역을 누가 썼는지 알고자하면 
1. 가계부내역이 어떤 가계부에 포함되어있는지 찾아야하고
2. 가계부가 어떤 유저의 소유인지 찾아야합니다.

그런데, 가계부 내역을 조회할 때, 본인이 작성한 내역만 조회할 수 있어야합니다.

앞서 언급했던 연관관계의 구조 때문에 가계부 내역이 본인 소유인지 확인하려면 가계부 내역을 조회하고, 어떤 가계부에 포함되어있는지 확인하고, 그 가계부가 어떤 유저의 소유인지 알아야 하기 때문에
가계부 내역, 가계부, 유저를 모두 조회해야하므로 3번의 쿼리가 필요합니다.

물론 가계부 내역과 가계부는 직접적으로 연관되어 있기 때문에 fetch join을 이용해서 한번에 가계부, 가계부 내역을 조회하고 그 다음 User를 조회할 수 있지만, 그래도 2번의 쿼리가 필요합니다.

이런 JPQL에서는 직접 연관이 되지 않은 테이블에 대한 조인이 되지 않기 때문에 불필요한 쿼리를 줄이기 위해서 QueryDsl을 사용해 해결했습니다.

또한 이런 문제 외에도 동적 쿼리 구현을 위해 QueryDsl을 사용했습니다.
가계부 내역 조회는 API 사용의 범용성을 위해 동적쿼리를 작성하기로 했습니다. 다음과 같은 쿼리를 지원합니다.
- 가계부별
- 타입별(수입, 지출)
- 삭제된 내역/복원된 내역별
- 기간별(월별, 일별, 시간별)

해당 조회 API의 각각의 파라미터는 파라미터가 입력되지 않는다면 WHERE절에서 조건을 제외 조회하도록 구현했습니다.

