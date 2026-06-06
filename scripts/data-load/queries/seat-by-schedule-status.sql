-- 목적:
-- 특정 회차의 좌석 상태 조회 성능을 측정한다.
--
-- 파라미터:
-- schedule_id: 조회 대상 회차 ID
-- status: 조회 대상 좌석 상태

EXPLAIN
SELECT id, schedule_id, seat_number, status, price
FROM seat
WHERE schedule_id = 1
  AND status = 'AVAILABLE'
ORDER BY seat_number ASC, id ASC;
