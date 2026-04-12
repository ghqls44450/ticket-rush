# Ticket Rush Backend

`back`은 Ticket Rush 프로젝트의 public 저장소 루트다.

## 현재 범위

- Docker Compose 기반 로컬 인프라
- `central-server` Spring Boot 부트스트랩
- DB 연결과 도메인 로직은 이후 이슈에서 순차적으로 추가

## 준비물

- Java 17
- Docker Desktop

## 로컬 인프라 실행

`back` 디렉토리에서 아래 명령어를 실행한다.

```bash
docker compose up -d mysql-master mysql-slave-1 mysql-slave-2 redis prometheus grafana
```

MySQL 호스트 포트:

- `13306` -> master
- `13307` -> slave 1
- `13308` -> slave 2

## Central Server 실행

`central-server` 디렉토리에서 아래 명령어를 실행한다.

```bash
./gradlew bootRun
```

기본 실행 프로필은 `local`이다.
현재 단계에서는 Issue 5 전까지 datasource auto-configuration을 비활성화해 DB 연결 없이도 부트스트랩이 가능하도록 구성했다.

기본 포트는 `18080`이다.
다른 포트를 쓰고 싶으면 `SERVER_PORT` 환경 변수로 변경할 수 있다.

```bash
SERVER_PORT=18081 ./gradlew bootRun
```

헬스체크 확인:

```bash
curl http://localhost:18080/actuator/health
```

## 프로필

- `local`: 로컬 실행용 기본 프로필
- `test`: 테스트 실행용 최소 프로필
