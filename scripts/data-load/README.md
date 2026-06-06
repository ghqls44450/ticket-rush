# data-load

## 목적

대용량 좌석 데이터를 생성하고 적재 방식별 시간을 비교하기 위한 스크립트 경로다.

## 이번 이슈에서 다룰 범위

- 더미 데이터 생성 방식 정리
- 적재 방식 비교
- 인덱스 전후 조회 성능 측정
- 재실행 가능한 명령 기준 정리

## 적재 방식 후보

- 단건 INSERT
- Bulk INSERT
- `LOAD DATA INFILE`

## 데이터 규모 기준

- 1차: 1만 건
- 2차: 10만 건
- 3차: 100만 건 이상
- 로컬 자원 상황에 따라 단계적으로 확장한다.

## 측정 순서

1. 소규모 데이터로 생성 방식과 적재 경로를 확인한다.
2. 주요 조회 쿼리의 EXPLAIN 결과를 기록한다.
3. 데이터 규모를 늘리면서 적재 시간과 조회 시간을 함께 기록한다.

## 예정 파일

- `generate-seat-data.sh`: 좌석 데이터 생성
- `load-seat-data.*`: 좌석 데이터 적재
- `queries/*`: EXPLAIN 및 측정용 쿼리

## 생성 스크립트

`generate-seat-data.sh`는 좌석 더미 데이터를 CSV로 생성한다. 세부 실행 방법과 인자 예시는 스크립트 파일 상단 주석을 기준으로 확인한다.

### 출력 형식

- CSV
- 컬럼 순서: `schedule_id,seat_number,status,price,held_at`
- `AVAILABLE` 좌석은 `held_at`를 비운다.

## 적재 스크립트

`load-seat-data.sh`는 생성한 CSV를 MySQL `seat` 테이블에 적재한다. 세부 실행 방법과 환경 변수 기준은 스크립트 파일 상단 주석을 기준으로 확인한다.

### 적재 방식

- `LOAD DATA LOCAL INFILE`
- 대상 테이블: `seat`
- 대상 컬럼: `schedule_id, seat_number, status, price, held_at`

## 조회 쿼리

`queries/` 디렉터리는 EXPLAIN과 실행 시간 측정에 사용할 조회 쿼리를 정리한다. 각 SQL 파일은 측정 목적과 파라미터 기준을 파일 상단 주석에 둔다.

### 대상 쿼리

- `seat-by-schedule-status.sql`: 회차별 좌석 상태 조회
- `schedule-by-performance.sql`: 공연별 회차 조회
- `expired-held-seats.sql`: 만료 대상 좌석 조회
