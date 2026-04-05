# Ticket Rush Backend

`back` is the public repository root for the Ticket Rush project.

## Current Scope

- Local infrastructure via Docker Compose
- `central-server` Spring Boot bootstrap
- Database integration and domain logic will be added in later issues

## Prerequisites

- Java 17
- Docker Desktop

## Local Infrastructure

Start the infrastructure services from the `back` directory:

```bash
docker compose up -d mysql-master mysql-slave-1 mysql-slave-2 redis prometheus grafana
```

MySQL host ports:

- `13306` -> master
- `13307` -> slave 1
- `13308` -> slave 2

## Central Server

Run the app from the `central-server` directory:

```bash
./gradlew bootRun
```

The app starts with the `local` profile by default.
At this stage, datasource auto-configuration is intentionally disabled until Issue 5.

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Profiles

- `local`: default runtime profile for local development
- `test`: minimal test profile used by bootstrap tests
