INSERT INTO performance (title, description, venue, total_seats)
VALUES ('테스트 공연', '설명', '올림픽홀', 500);

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
