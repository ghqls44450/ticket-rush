-- 목적:
-- seat 적재 전에 필요한 performance, schedule 참조 데이터를 준비한다.
--
-- 사용 순서:
-- 1. ticket 데이터베이스 선택
-- 2. 기존 측정 데이터 정리
-- 3. 성능 측정용 performance, schedule 데이터 추가

USE ticket;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM seat;
DELETE FROM schedule;
DELETE FROM performance;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO performance (id, title, description, venue, total_seats)
VALUES
    (1, 'Index Benchmark Performance 1', 'seat(schedule_id, status) 측정용 공연', 'Benchmark Hall A', 500000),
    (2, 'Index Benchmark Performance 2', 'schedule(performance_id, start_time) 측정용 공연', 'Benchmark Hall B', 500000);

INSERT INTO schedule (id, performance_id, start_time, end_time, status)
VALUES
    (1, 1, '2026-06-06 19:00:00', '2026-06-06 21:30:00', 'OPEN'),
    (2, 1, '2026-06-07 19:00:00', '2026-06-07 21:30:00', 'OPEN'),
    (3, 1, '2026-06-08 19:00:00', '2026-06-08 21:30:00', 'OPEN'),
    (4, 2, '2026-06-09 19:00:00', '2026-06-09 21:30:00', 'OPEN'),
    (5, 2, '2026-06-10 19:00:00', '2026-06-10 21:30:00', 'OPEN');
