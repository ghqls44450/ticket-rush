-- 조회 API 통합 테스트 공통 정리 스크립트
-- FK 제약을 고려해 자식 테이블부터 삭제하고, AUTO_INCREMENT 값을 초기화한다.

DELETE FROM payment;
DELETE FROM reservation;
DELETE FROM seat;
DELETE FROM schedule;
DELETE FROM performance;
DELETE FROM `user`;

ALTER TABLE payment AUTO_INCREMENT = 1;
ALTER TABLE reservation AUTO_INCREMENT = 1;
ALTER TABLE seat AUTO_INCREMENT = 1;
ALTER TABLE schedule AUTO_INCREMENT = 1;
ALTER TABLE performance AUTO_INCREMENT = 1;
ALTER TABLE `user` AUTO_INCREMENT = 1;
