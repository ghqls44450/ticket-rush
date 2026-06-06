-- 목적:
-- 특정 공연의 회차 조회 성능을 측정한다.
--
-- 파라미터:
-- performance_id: 조회 대상 공연 ID

EXPLAIN
SELECT id, performance_id, start_time, end_time, status
FROM schedule
WHERE performance_id = 1
ORDER BY start_time ASC;
