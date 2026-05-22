-- SeatControllerTest 전용 테스트 데이터
-- 좌석 점유, 해제, 만료 복구 시나리오를 함께 검증한다.

INSERT INTO performance (title, description, venue, total_seats)
VALUES ('좌석 점유 테스트 공연', '설명', '올림픽홀', 5);

INSERT INTO schedule (performance_id, start_time, end_time, status)
VALUES (1, '2026-05-10 19:00:00', '2026-05-10 21:00:00', 'OPEN');

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (1, 'A1', 'AVAILABLE', 50000);

INSERT INTO seat (schedule_id, seat_number, status, price, held_at)
VALUES (1, 'A2', 'HELD', 50000, '2026-05-10 18:40:00');

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (1, 'A3', 'CONFIRMED', 50000);

INSERT INTO seat (schedule_id, seat_number, status, price)
VALUES (1, 'A4', 'CANCELLED', 50000);

INSERT INTO seat (schedule_id, seat_number, status, price, held_at)
VALUES (1, 'A5', 'HELD', 50000, '2026-05-10 18:59:30');
