# 좌석 상태 전이 규칙

좌석 상태는 예매 과정에서 한 좌석이 어떤 단계에 있는지를 나타낸다. 상태 전이 규칙은 중복 예매를 막고, 결제 이탈이나 취소 같은 흐름을 일관되게 처리하기 위한 기준이다.

## 좌석 상태

| 상태 | 의미 |
|---|---|
| `AVAILABLE` | 예매 가능한 좌석 |
| `HELD` | 사용자가 선택해 임시 점유한 좌석 |
| `CONFIRMED` | 결제 또는 예약 확정이 완료된 좌석 |
| `CANCELLED` | 확정 이후 취소된 좌석 |

## 허용되는 상태 전이

| 현재 상태 | 다음 상태 | 의미 |
|---|---|---|
| `AVAILABLE` | `HELD` | 사용자가 좌석을 선택해 임시 점유한다. |
| `HELD` | `CONFIRMED` | 임시 점유된 좌석의 예약을 확정한다. |
| `HELD` | `AVAILABLE` | 임시 점유 시간이 만료되거나 사용자가 이탈해 좌석을 다시 해제한다. |
| `CONFIRMED` | `CANCELLED` | 확정된 예약을 취소한다. |

현재 코드에서는 [SeatStatus](/Users/seo/workspace/Idea/ticket-rush/back/central-server/src/main/java/com/ticketrush/centralserver/domain/seat/SeatStatus.java)의 `canTransitionTo` 메서드가 위 규칙을 판단한다.

## 허용하지 않는 상태 전이

명시적으로 허용하지 않은 상태 전이는 모두 거부한다.

대표 예시:

| 현재 상태 | 다음 상태 | 거부 이유 |
|---|---|---|
| `AVAILABLE` | `CONFIRMED` | 임시 점유 없이 바로 확정할 수 없다. |
| `AVAILABLE` | `CANCELLED` | 예매되지 않은 좌석은 취소할 수 없다. |
| `CONFIRMED` | `HELD` | 확정된 좌석은 다시 임시 점유 상태로 되돌릴 수 없다. |
| `CANCELLED` | `HELD` | 취소된 좌석의 재오픈 정책은 별도 기준이 필요하다. |
| 임의 상태 | `null` | 대상 상태가 없으므로 전이할 수 없다. |

## 예매 흐름과의 연결

기본 예매 흐름은 다음 순서로 본다.

```text
AVAILABLE -> HELD -> CONFIRMED
```

결제 중 이탈이나 임시 점유 만료가 발생하면 다음 흐름으로 좌석을 복구한다.

```text
HELD -> AVAILABLE
```

예약 확정 이후 취소가 발생하면 다음 흐름으로 처리한다.

```text
CONFIRMED -> CANCELLED
```

`CANCELLED` 좌석을 다시 `AVAILABLE`로 되돌릴지는 현재 규칙에 포함하지 않는다. 이 동작은 재오픈 정책, 환불 정책, 좌석 이력 관리 방식에 따라 달라질 수 있으므로 이후 예약/취소 기능을 구현할 때 별도로 결정한다.

## 테스트 기준

상태 전이 규칙은 [SeatStatusTest](/Users/seo/workspace/Idea/ticket-rush/back/central-server/src/test/java/com/ticketrush/centralserver/domain/seat/SeatStatusTest.java)에서 검증한다.

- 허용되는 상태 전이는 `true`를 반환한다.
- 허용되지 않는 상태 전이는 `false`를 반환한다.
- `null` 상태로의 전이는 `false`를 반환한다.
