# Ticket Rush Backend

대용량 트래픽 상황의 티켓 예매 시스템을 직접 설계하고 구현하는 백엔드 프로젝트다.
예매 서비스에서 실제로 문제가 되는 동시성, 읽기/쓰기 분리, 캐시, 모니터링을
로컬 환경에서 직접 재현하고 검증하는 데 목적이 있다.

## 한 줄 소개

공연 예매 오픈런 상황을 가정하고,
같은 좌석에 동시에 몰리는 요청을 안정적으로 처리하기 위한 백엔드 구조를 구현하는 프로젝트다.

## 왜 이 프로젝트를 만들었는가

일반적인 CRUD 중심 프로젝트만으로는
트래픽 폭주, 좌석 중복 예매, 복제 지연, 캐시 일관성 같은 문제를 깊게 다루기 어렵다.

이 프로젝트를 진행하면서 아래 문제들을 고민하며 해결하는 것을 목표로 한다.

- 같은 좌석에 여러 사용자가 동시에 요청하면 어떻게 막을 것인가
- 조회와 쓰기 부하를 어떻게 분리할 것인가
- 인기 공연 조회를 어떻게 캐싱할 것인가
- 성능 저하와 병목을 어떤 지표로 확인할 것인가

## 핵심 기술 과제

### 1. 동시성 제어

같은 좌석에 대한 중복 예매를 방지하기 위해
상태 전이와 락 전략을 비교하고 적용한다.

### 2. 읽기/쓰기 분리

MySQL Master-Slave 레플리케이션을 직접 구성하고,
쓰기와 조회 트래픽을 분리해 부하 분산 구조를 검증한다.

### 3. 캐시와 임시 점유

Redis를 사용해 인기 조회 데이터를 캐싱하고,
좌석 임시 점유 상태를 TTL 기반으로 제어한다.

### 4. 관측 가능성

Prometheus와 Grafana를 연결해
TPS, 응답 시간, 병목 지점을 직접 확인할 수 있는 환경을 만든다.

## 시스템 구성

- `central-server`
  - 예매 비즈니스 로직을 담당하는 Spring Boot 애플리케이션
- `mysql-master`
  - 쓰기 처리용 DB
- `mysql-slave-1`, `mysql-slave-2`
  - 조회 처리용 DB
- `redis`
  - 캐시와 임시 점유 TTL 관리
- `prometheus`, `grafana`
  - 지표 수집과 시각화

## 아키텍처 방향

- 중앙 서버에서 예매 흐름과 상태 전이를 관리한다.
- MySQL Master-Slave 구조로 읽기/쓰기 분리를 실험한다.
- Redis TTL로 좌석 임시 점유 만료를 제어한다.
- 트래픽 발생 환경과 모니터링 환경을 함께 두고 병목을 관찰한다.

## 사용 기술

- Java 17
- Spring Boot 3
- MyBatis
- MySQL 8
- Redis 7
- Prometheus
- Grafana
- Docker Compose
- Gradle

## 로컬 실행

### 인프라 실행

```bash
docker compose up -d mysql-master mysql-slave-1 mysql-slave-2 redis prometheus grafana
```

### Central Server 실행

```bash
cd central-server
./gradlew bootRun
```

기본 포트는 `18080`이다.

```bash
curl http://localhost:18080/actuator/health
```
