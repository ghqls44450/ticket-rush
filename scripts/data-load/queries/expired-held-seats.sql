-- 목적:
-- 만료 대상 HELD 좌석 조회 성능을 측정한다.
--
-- 파라미터:
-- held_at 기준 시각: 이 시각보다 이전인 HELD 좌석 조회

EXPLAIN
SELECT id, schedule_id, seat_number, status, price
FROM seat
WHERE status = 'HELD'
  AND held_at < '2026-06-06 12:00:00'
ORDER BY held_at ASC, id ASC;
