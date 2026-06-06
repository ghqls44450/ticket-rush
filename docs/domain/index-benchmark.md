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

로컬 `mysql` 클라이언트가 없으면 DBeaver CSV import로 동일 데이터를 적재한다.

## 기록 형식

### 측정 1

- 데이터 규모: `seat` 250건
- 적재 방식: `LOAD DATA LOCAL INFILE`
- 참조 데이터 준비 명령:
  - `setup-reference-data.sql` 실행
- 실행 명령:
  - `./scripts/data-load/generate-seat-data.sh ./scripts/data-load/output/seat-data-250.csv 250 1 50`
  - `seat` 테이블 CSV import
- EXPLAIN 결과 요약:
  - 회차별 좌석 상태 조회: `key=idx_schedule_status`, `type=ref`, `rows=48`
  - 공연별 회차 조회: `key=idx_performance_start`, `type=ref`, `rows=3`
  - 만료 대상 좌석 조회: `key=idx_status_held`, `type=range`, `rows=12`
- 실행 시간:
  - CSV 생성 시간: 약 0.04초
  - CSV 적재 시간: 약 1.19초
- 해석:
  - 회차별 좌석 상태 조회는 `idx_schedule_status`를 사용했다.
  - 공연별 회차 조회는 `idx_performance_start`를 사용했다.
  - 만료 대상 좌석 조회는 `idx_status_held`를 사용했다.
  - 250건 기준에서는 세 쿼리 모두 기대한 인덱스를 사용했다.
  - 다음 측정에서는 참조 `schedule` 수를 함께 늘려 1만 건 이상으로 확장한다.

### 측정 2

- 데이터 규모: `seat` 10000건, `schedule` 200건
- 적재 방식: DBeaver CSV import
- 참조 데이터 준비 명령:
  - `setup-reference-data.sql` 실행
- 실행 명령:
  - `./scripts/data-load/generate-seat-data.sh ./scripts/data-load/output/seat-data-10k.csv 10000 1 50`
  - `seat` 테이블 CSV import
- EXPLAIN 결과 요약:
  - 회차별 좌석 상태 조회: `key=idx_schedule_status`, `type=ref`, `rows=48`
  - 공연별 회차 조회: `key=idx_performance_start`, `type=ref`, `rows=150`
  - 만료 대상 좌석 조회: `key=idx_status_held`, `type=range`, `rows=500`
- 실행 시간:
  - CSV 생성 시간: 기록 전
  - CSV 적재 시간: 기록 전
- 해석:
  - 회차별 좌석 상태 조회는 `idx_schedule_status`를 계속 사용했다.
  - 공연별 회차 조회는 `idx_performance_start`를 계속 사용했다.
  - 만료 대상 좌석 조회는 `idx_status_held`를 계속 사용했다.
  - 회차별 좌석 상태 조회는 회차당 좌석 수 영향이 커서 전체 데이터 증가에도 `rows` 변화가 제한적이었다.
  - 공연별 회차 조회는 `performance_id=1`에 연결된 회차 수 증가가 `rows=150`으로 반영됐다.
  - 만료 대상 좌석 조회는 `HELD` 범위 검색 특성상 데이터 증가에 따라 `rows=500`으로 증가했다.

### 측정 3

- 데이터 규모: `seat` 100000건, `schedule` 2000건
- 적재 방식: DBeaver CSV import
- 참조 데이터 준비 명령:
  - `SET SESSION cte_max_recursion_depth = 3000`
  - `setup-reference-data.sql` 실행
- 실행 명령:
  - `./scripts/data-load/generate-seat-data.sh ./scripts/data-load/output/seat-data-100k.csv 100000 1 50`
  - `seat` 테이블 CSV import
- EXPLAIN 결과 요약:
  - 회차별 좌석 상태 조회: `key=idx_schedule_status`, `type=ref`, `rows=48`
  - 공연별 회차 조회: `key=idx_performance_start`, `type=ref`, `rows=1500`
  - 만료 대상 좌석 조회: `key=idx_status_held`, `type=range`, `rows=5000`
- 실행 시간:
  - CSV 생성 시간: 약 0.15초
  - CSV 적재 시간: 약 5.28초
- 해석:
  - 회차별 좌석 상태 조회는 10만 건까지 확장해도 `idx_schedule_status`를 계속 사용했다.
  - 회차별 좌석 상태 조회는 특정 회차의 좌석 수 영향이 커서 `rows=48` 수준을 유지했다.
  - 공연별 회차 조회는 `idx_performance_start`를 계속 사용했고 `performance_id=1`에 연결된 회차 수 증가가 `rows=1500`으로 반영됐다.
  - 만료 대상 좌석 조회는 `idx_status_held`를 계속 사용했고 `HELD` 범위 검색 특성상 `rows=5000`까지 증가했다.
  - 10만 건 기준에서도 세 쿼리 모두 기대한 인덱스를 유지했다.
