# 인덱스 성능 측정

## 목표

대용량 데이터 환경에서 주요 조회 쿼리의 실행 계획과 응답 시간을 확인한다. 인덱스 설계 의도가 실제 조회 패턴에 맞는지 EXPLAIN과 실행 시간으로 검증한다.

## 측정 대상 인덱스

- `seat(schedule_id, status)`
- `schedule(performance_id, start_time)`
- `seat(status, held_at)`

## 데이터 규모 기준

- 1차: 1만 건
- 2차: 10만 건
- 3차: 100만 건 이상
- 로컬 자원 상황에 따라 단계적으로 확장한다.

## 적재 방식 후보

- 단건 INSERT
- Bulk INSERT
- `LOAD DATA INFILE`

## 주요 조회 쿼리

### 1. 회차별 좌석 상태 조회

```sql
SELECT id, schedule_id, seat_number, status, price
FROM seat
WHERE schedule_id = ? AND status = ?
ORDER BY seat_number ASC, id ASC;
```

### 2. 공연별 회차 조회

```sql
SELECT id, performance_id, start_time, end_time, status
FROM schedule
WHERE performance_id = ?
ORDER BY start_time ASC;
```

### 3. 만료 대상 좌석 조회

```sql
SELECT id, schedule_id, seat_number, status, price
FROM seat
WHERE status = 'HELD'
  AND held_at < ?
ORDER BY held_at ASC, id ASC;
```

## 측정 순서

1. 더미 데이터 규모와 적재 방식을 고정한다.
2. `setup-reference-data.sql`로 참조 데이터를 준비한다.
3. 적재 명령과 소요 시간을 기록한다.
4. 주요 조회 쿼리의 EXPLAIN 결과를 기록한다.
5. 인덱스 유지 상태와 비교 기준을 함께 남긴다.

## 실행 기준

### 참조 데이터 준비

```sql
SOURCE scripts/data-load/setup-reference-data.sql;
```

### CSV 생성

```bash
./scripts/data-load/generate-seat-data.sh \
  ./scripts/data-load/output/seat-data-10k.csv \
  10000 \
  1 \
  50
```

### CSV 적재

```bash
./scripts/data-load/load-seat-data.sh \
  ./scripts/data-load/output/seat-data-10k.csv
```

## 기록 형식

### 측정 1

- 데이터 규모:
- 적재 방식:
- 참조 데이터 준비 명령:
- 실행 명령:
- EXPLAIN 결과 요약:
- 실행 시간:
- 해석:

### 측정 2

- 데이터 규모:
- 적재 방식:
- 참조 데이터 준비 명령:
- 실행 명령:
- EXPLAIN 결과 요약:
- 실행 시간:
- 해석:
