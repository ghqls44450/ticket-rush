INSERT INTO performance (title, description, venue, total_seats)
VALUES ('테스트 공연', '설명', '올림픽홀', 500);

INSERT INTO performance (title, description, venue, total_seats)
VALUES ('두번째 공연', '설명', '체조경기장', 1000);

INSERT INTO schedule (performance_id, start_time, end_time, status)
VALUES (1, '2026-05-11 19:00:00', '2026-05-11 21:00:00', 'OPEN');

INSERT INTO schedule (performance_id, start_time, end_time, status)
VALUES (1, '2026-05-10 19:00:00', '2026-05-10 21:00:00', 'OPEN');

INSERT INTO schedule (performance_id, start_time, end_time, status)
VALUES (2, '2026-05-12 19:00:00', '2026-05-12 21:00:00', 'OPEN');
