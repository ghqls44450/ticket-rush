-- 목적:
-- seat 적재 전에 필요한 performance, schedule 참조 데이터를 준비한다.
--
-- 사용 순서:
-- 1. ticket 데이터베이스 선택
-- 2. 기존 측정 데이터 정리
-- 3. 성능 측정용 performance, schedule 데이터 추가
-- 4. 10만 건 seat 적재를 받을 수 있도록 schedule 2000건 기준으로 준비

USE ticket;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM seat;
DELETE FROM schedule;
DELETE FROM performance;
SET FOREIGN_KEY_CHECKS = 1;
SET SESSION cte_max_recursion_depth = 3000;

INSERT INTO performance (id, title, description, venue, total_seats)
VALUES
    (1, 'Index Benchmark Performance 1', 'seat(schedule_id, status) 측정용 공연', 'Benchmark Hall A', 500000),
    (2, 'Index Benchmark Performance 2', 'schedule(performance_id, start_time) 측정용 공연', 'Benchmark Hall B', 500000);

INSERT INTO schedule (id, performance_id, start_time, end_time, status)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1
    FROM seq
    WHERE n < 2000
)
SELECT
    n AS id,
    CASE
        WHEN n <= 1500 THEN 1
        ELSE 2
    END AS performance_id,
    TIMESTAMPADD(DAY, n - 1, '2026-06-06 19:00:00') AS start_time,
    TIMESTAMPADD(DAY, n - 1, '2026-06-06 21:30:00') AS end_time,
    'OPEN' AS status
FROM seq;
