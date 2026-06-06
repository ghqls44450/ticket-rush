#!/usr/bin/env bash
#
# 목적:
# 생성한 seat CSV 데이터를 MySQL seat 테이블에 적재한다.
#
# 사용법:
# ./scripts/data-load/load-seat-data.sh <csv_path>
#
# 예시:
# ./scripts/data-load/load-seat-data.sh ./scripts/data-load/output/seat-data-10k.csv
#
# 환경 변수:
# DB_HOST: MySQL host, 기본값 localhost
# DB_PORT: MySQL port, 기본값 13306
# DB_NAME: database name, 기본값 ticket
# DB_USERNAME: MySQL username, 기본값 root
# DB_PASSWORD: MySQL password, 기본값 rootpassword

set -euo pipefail

CSV_PATH="${1:-}"

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-13306}"
DB_NAME="${DB_NAME:-ticket}"
DB_USERNAME="${DB_USERNAME:-root}"
DB_PASSWORD="${DB_PASSWORD:-rootpassword}"

if [ -z "${CSV_PATH}" ]; then
	echo "CSV_PATH is required" >&2
	exit 1
fi

if [ ! -f "${CSV_PATH}" ]; then
	echo "CSV file not found: ${CSV_PATH}" >&2
	exit 1
fi

if ! command -v mysql >/dev/null 2>&1; then
	echo "mysql command not found" >&2
	exit 1
fi

ROW_COUNT="$(wc -l < "${CSV_PATH}")"

echo "loading seat data"
echo "csv_path=${CSV_PATH}"
echo "row_count=${ROW_COUNT}"
echo "db_host=${DB_HOST}"
echo "db_port=${DB_PORT}"
echo "db_name=${DB_NAME}"

mysql \
	--local-infile=1 \
	-h "${DB_HOST}" \
	-P "${DB_PORT}" \
	-u "${DB_USERNAME}" \
	-p"${DB_PASSWORD}" \
	"${DB_NAME}" <<SQL
LOAD DATA LOCAL INFILE '${CSV_PATH}'
INTO TABLE seat
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
(schedule_id, seat_number, status, price, held_at);
SQL

echo "loaded: ${ROW_COUNT} rows"
