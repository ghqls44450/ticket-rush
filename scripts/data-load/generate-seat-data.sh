#!/usr/bin/env bash
#
# 목적:
# seat 더미 데이터를 CSV 형식으로 생성한다.
#
# 사용법:
# ./scripts/data-load/generate-seat-data.sh <output_path> <total_rows> <start_schedule_id> <seats_per_schedule>
#
# 예시:
# ./scripts/data-load/generate-seat-data.sh ./scripts/data-load/output/seat-data-10k.csv 10000 1 50

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_OUTPUT_PATH="${SCRIPT_DIR}/output/seat-data.csv"

OUTPUT_PATH="${1:-${DEFAULT_OUTPUT_PATH}}"
TOTAL_ROWS="${2:-10000}"
START_SCHEDULE_ID="${3:-1}"
SEATS_PER_SCHEDULE="${4:-50}"
HELD_INTERVAL="${HELD_INTERVAL:-20}"
PRICE="${PRICE:-100000}"
HELD_AT_VALUE="${HELD_AT_VALUE:-2026-06-05 12:00:00}"

mkdir -p "$(dirname "${OUTPUT_PATH}")"

if ! [[ "${TOTAL_ROWS}" =~ ^[0-9]+$ ]] || [ "${TOTAL_ROWS}" -le 0 ]; then
	echo "TOTAL_ROWS must be a positive integer" >&2
	exit 1
fi

if ! [[ "${START_SCHEDULE_ID}" =~ ^[0-9]+$ ]] || [ "${START_SCHEDULE_ID}" -le 0 ]; then
	echo "START_SCHEDULE_ID must be a positive integer" >&2
	exit 1
fi

if ! [[ "${SEATS_PER_SCHEDULE}" =~ ^[0-9]+$ ]] || [ "${SEATS_PER_SCHEDULE}" -le 0 ]; then
	echo "SEATS_PER_SCHEDULE must be a positive integer" >&2
	exit 1
fi

if ! [[ "${HELD_INTERVAL}" =~ ^[0-9]+$ ]] || [ "${HELD_INTERVAL}" -le 0 ]; then
	echo "HELD_INTERVAL must be a positive integer" >&2
	exit 1
fi

awk \
	-v total_rows="${TOTAL_ROWS}" \
	-v start_schedule_id="${START_SCHEDULE_ID}" \
	-v seats_per_schedule="${SEATS_PER_SCHEDULE}" \
	-v held_interval="${HELD_INTERVAL}" \
	-v price="${PRICE}" \
	-v held_at_value="${HELD_AT_VALUE}" '
BEGIN {
	for (row = 0; row < total_rows; row++) {
		schedule_offset = int(row / seats_per_schedule)
		schedule_id = start_schedule_id + schedule_offset
		seat_index = (row % seats_per_schedule) + 1
		seat_number = sprintf("A-%04d", seat_index)

		status = "AVAILABLE"
		held_at = ""

		if (((row + 1) % held_interval) == 0) {
			status = "HELD"
			held_at = held_at_value
		}

		printf "%d,%s,%s,%d,%s\n", schedule_id, seat_number, status, price, held_at
	}
}
' > "${OUTPUT_PATH}"

echo "generated: ${OUTPUT_PATH}"
