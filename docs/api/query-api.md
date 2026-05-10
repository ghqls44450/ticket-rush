# 조회 API 스펙

현재 구현된 조회 API의 요청과 응답 형식을 정리한다.

## 공통 응답 형식

### 성공 응답

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

### 실패 응답

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "COMMON_INVALID_INPUT",
    "message": "잘못된 요청입니다."
  }
}
```

## 공연 목록 조회

```http
GET /api/v1/performances
```

요청 파라미터는 없다.

응답 예시:

```json
{
  "success": true,
  "data": [
    {
      "id": 2,
      "title": "두번째 공연",
      "venue": "체조경기장",
      "totalSeats": 1000
    },
    {
      "id": 1,
      "title": "테스트 공연",
      "venue": "올림픽홀",
      "totalSeats": 500
    }
  ],
  "error": null
}
```

## 공연 단건 조회

```http
GET /api/v1/performances/{performanceId}
```

Path variable:

| 이름 | 타입 | 설명 |
|---|---|---|
| `performanceId` | `Long` | 공연 ID |

응답 예시:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "테스트 공연",
    "venue": "올림픽홀",
    "totalSeats": 500
  },
  "error": null
}
```

존재하지 않는 공연 ID를 요청하면 `PERFORMANCE_NOT_FOUND` 에러를 반환한다.

## 공연 회차 조회

```http
GET /api/v1/performances/{performanceId}/schedules
```

Path variable:

| 이름 | 타입 | 설명 |
|---|---|---|
| `performanceId` | `Long` | 공연 ID |

응답 예시:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "performanceId": 1,
      "startTime": "2026-05-10T19:00:00",
      "endTime": "2026-05-10T21:00:00",
      "status": "OPEN"
    },
    {
      "id": 2,
      "performanceId": 1,
      "startTime": "2026-05-11T19:00:00",
      "endTime": "2026-05-11T21:00:00",
      "status": "OPEN"
    }
  ],
  "error": null
}
```

## 회차 좌석 조회

```http
GET /api/v1/schedules/{scheduleId}/seats
```

Path variable:

| 이름 | 타입 | 설명 |
|---|---|---|
| `scheduleId` | `Long` | 회차 ID |

Query parameter:

| 이름 | 필수 여부 | 설명 |
|---|---|---|
| `status` | 선택 | 좌석 상태 필터 |

`status` 허용값:

- `AVAILABLE`
- `HELD`
- `CONFIRMED`
- `CANCELLED`

응답 예시:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "scheduleId": 1,
      "seatNumber": "A1",
      "status": "AVAILABLE",
      "price": 50000
    },
    {
      "id": 2,
      "scheduleId": 1,
      "seatNumber": "A2",
      "status": "HELD",
      "price": 50000
    }
  ],
  "error": null
}
```

상태를 지정하면 해당 상태의 좌석만 조회한다.

```http
GET /api/v1/schedules/{scheduleId}/seats?status=AVAILABLE
```

## 에러 응답

| 상황 | HTTP Status | Error Code | Message |
|---|---:|---|---|
| 잘못된 path variable 타입 | 400 | `COMMON_INVALID_INPUT` | 잘못된 요청입니다. |
| 존재하지 않는 공연 | 404 | `PERFORMANCE_NOT_FOUND` | 공연을 찾을 수 없습니다. |
| 허용하지 않는 좌석 status | 400 | `INVALID_SEAT_STATUS` | 허용하지 않는 좌석 상태입니다. |
| 빈 좌석 status | 400 | `INVALID_SEAT_STATUS` | 허용하지 않는 좌석 상태입니다. |
