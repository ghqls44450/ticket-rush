-- ScheduleControllerTest 전용 테스트 데이터
-- 회차별 좌석 목록 조회와 status 필터 조회를 검증한다.

INSERT INTO performance (title, description, venue, total_seats)
VALUES ('테스트 공연', '설명', '올림픽홀', 500);

-- 회차 정렬 검증을 위해 start_time 순서와 삽입 순서를 다르게 둔다.
INSERT INTO schedule (performance_id, start_time, end_time, status)
VALUES (1, '2026-05-10 19:00:00', '2026-05-10 21:00:00', 'OPEN');

INSERT INTO schedule (performance_id, start_time, end_time, status)
VALUES (1, '2026-05-11 19:00:00', '2026-05-11 21:00:00', 'OPEN');

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (1, 'A1', 'AVAILABLE', 50000);

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (1, 'A2', 'HELD', 50000);

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (1, 'A3', 'CONFIRMED', 50000);

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (2, 'B1', 'AVAILABLE', 70000);
