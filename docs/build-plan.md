# SPay Step-By-Step Build Plan

This plan deliberately completes one working vertical slice before adding the next production pattern. Each phase ends with something testable in Swagger or Flutter.

## Working Agreement

- Base package stays `com.ammarbhatkar.SPay`.
- Domain services use `service` interfaces and `service.impl` implementations.
- The Razorpay course project is read-only reference material.
- We do not copy it or modify it.
- We write one feature, run it, test its unhappy cases, and only then continue.
- PostgreSQL constraints and transactions protect money correctness.
- Redis and Kafka improve scalability and reliability but do not replace database correctness.

## Priority Levels

- P0: required for a valid hackathon submission.
- P1: creates the backend reliability story judges expect.
- P2: polish or extra-credit work done only after P0 and P1 are stable.

## Phase 0: Design And Skeleton

Priority: P0

### Work

1. Freeze MVP scope and user journeys.
2. Finalize ER diagram and payment state machine.
3. Confirm package skeleton and `service` plus `service.impl` structure.
4. Add empty shells only for newly approved domain concepts.
5. Keep `architecture.md`, `build-plan.md`, and `frontend-flow.md` as the source of truth.

### Done When

- Every required feature maps to a module, entity, endpoint, test, and demo action.
- No one needs to invent fields while writing the first entity.
- Empty skeleton compiles.

## Phase 1: Runnable Infrastructure Foundation

Priority: P0

### You Build

1. Add JWT dependencies compatible with the selected Spring Boot version.
2. Add database migration support, preferably Flyway.
3. Add Docker Compose for PostgreSQL, Redis, Kafka, and Kafka UI.
4. Move database password, JWT secret, and service addresses to environment variables.
5. Add `application-local.yaml` and `application-test.yaml` only if profile separation is needed.
6. Configure OpenAPI metadata and permit Swagger routes in security.
7. Add a basic health endpoint through Actuator.

### Verify

- `docker compose up -d` starts all infrastructure.
- Spring Boot starts without manual DBeaver actions.
- PostgreSQL connection succeeds.
- Redis ping succeeds from the app.
- Kafka producer configuration loads.
- Swagger and Actuator health open.

### Stop Gate

Do not begin auth until the app starts repeatedly from documented commands.

## Phase 2: Auth And User

Priority: P0

### Entities And Rules

- `AppUser` with unique normalized email and phone number.
- Status values: `ACTIVE`, `LOCKED`, `DISABLED`.
- Role values: `USER`, `ADMIN`.
- Store `passwordHash`, never plain password.

### APIs

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/users/me
```

### Implementation Order

1. Add entity fields and enums.
2. Add repository queries by normalized email and phone.
3. Add register request validation.
4. Hash password using BCrypt.
5. Add JWT generation and verification.
6. Add authentication filter and security configuration.
7. Implement login.
8. Implement current-user endpoint.
9. Add global validation and authentication errors.

### Test Cases

- Register succeeds.
- Duplicate email returns 409.
- Duplicate phone returns 409.
- Invalid email and short password return field errors.
- Login succeeds with correct password.
- Login fails without revealing whether an email exists.
- Protected API returns 401 without JWT.
- User cannot access admin endpoint.

### Done When

Swagger can register, log in, authorize with JWT, and call `/api/users/me`.

## Phase 3: Mock Bank Activation

Priority: P0

### APIs

```text
GET  /api/banks
POST /api/bank-discovery/start
POST /api/bank-discovery/{sessionId}/verify-otp
POST /api/bank-accounts/{accountId}/verify-debit-card
POST /api/bank-accounts/{accountId}/upi-pin
GET  /api/bank-accounts/me
```

### Exact Flow

1. Return five supported banks from configuration.
2. Start discovery only for the authenticated user's phone number.
3. Generate an opaque account token, masked account, IFSC, OTP hash, and expiry.
4. Return the demo OTP only when the `demo` or `local` profile is active.
5. Verify OTP with attempt and expiry checks.
6. Create or expose a provisional bank account in `OTP_VERIFIED` state.
7. Validate mock last-six and expiry transiently.
8. Change account to `VERIFIED` and assign configured demo balance.
9. Hash and store UPI PIN in `UpiCredential`.
10. Lock UPI credential after three wrong PIN attempts for a configurable period.

### Important Rules

- One active discovery session per user and bank.
- Session expires after five minutes.
- Debit-card fields never reach entity, log, cache, Kafka, or exception output.
- Bank balance is stored in paise as `long`, not floating point.
- Account version is available for concurrency checks.
- A user may link more than one bank account, but one account is enough for the demo.

### Test Cases

- Unsupported bank is rejected.
- Expired or wrong OTP is rejected.
- Too many OTP attempts are rate limited.
- Debit-card values are absent from database and logs.
- PIN cannot be set before account verification.
- Accounts belong only to the authenticated user.

### Done When

A new user can complete the full activation flow in Swagger and see a masked verified account with demo balance.

## Phase 4: UPI Handle

Priority: P0

### APIs

```text
POST /api/upi/handles
GET  /api/upi/handles/me
GET  /api/upi/resolve/{upiId}
```

### Rules

- Normalize to lowercase.
- Handle must match the approved pattern and end with `@spay`.
- Handle is globally unique.
- Only a verified bank account with a configured UPI PIN may be linked.
- Resolution returns display name, UPI ID, and masked bank identity, never phone or balance.
- Deactivated handles do not resolve.

### Test Cases

- Create valid handle.
- Reject duplicate and invalid handles.
- Reject handle creation before PIN setup.
- Prevent linking another user's account.
- Resolve valid handle and return 404 for unknown handle.

### Done When

Two activated demo users can resolve each other's handles.

## Phase 5: Synchronous Payment Happy Path

Priority: P0

This phase intentionally starts synchronously so payment correctness is understood before Kafka is introduced.

### APIs

```text
POST /api/payments/upi
GET  /api/payments/{id}
GET  /api/payments/{id}/timeline
GET  /api/payments/history
```

### Processing Order

1. Authenticate sender.
2. Validate UPI PIN.
3. Resolve sender default account and receiver UPI.
4. Validate amount, account status, and sender not equal receiver.
5. Lock database account rows in deterministic order.
6. Recheck available balance inside the transaction.
7. Debit sender and credit receiver.
8. Insert two append-only ledger entries with unique deduplication keys.
9. Create timeline events.
10. Complete transaction as `SUCCESS`.

### Test Cases

- Successful transfer changes both balances by the exact paise amount.
- Exactly one debit and one credit entry exist.
- History shows sent and received views.
- Invalid UPI, wrong PIN, insufficient balance, frozen account, same account, zero amount, negative amount, and amount over demo limit fail cleanly.
- Concurrent payments cannot make balance negative.

### Done When

The basic payment invariant is proven: total money across sender and receiver is unchanged and no account becomes negative.

## Phase 6: Merchant And Dynamic QR

Priority: P0

### APIs

```text
POST /api/merchants
GET  /api/merchants/me
POST /api/merchants/{id}/qrs
GET  /api/merchant-qrs/{qrReference}
POST /api/payments/merchant
GET  /api/merchants/{id}/payments
```

### Rules

- User must have a verified settlement account.
- One merchant profile per user for MVP.
- Merchant UPI ID is unique and ends in `@spay`.
- QR can contain fixed amount or allow payer-entered amount.
- QR is one-time, expires after a configured duration, and becomes used only after successful payment.
- QR payload is signed or references an opaque server record so amount and merchant cannot be tampered with.
- Merchant payment uses the same payment and ledger engine as UPI transfer.

### Test Cases

- Merchant creation and QR generation succeed.
- Expired, revoked, malformed, and tampered QR fail.
- Inactive merchant cannot receive.
- Merchant settlement account receives exactly one credit and the QR cannot be reused.

### Done When

A consumer can resolve a demo QR, pay it, and the merchant can see the received payment.

## Phase 7: Idempotency And Error Contract

Priority: P1 and hackathon mandatory

### Implementation

1. Require `X-Idempotency-Key` on payment creation.
2. Canonicalize non-sensitive request fields and hash them.
3. Add database unique constraint for user, endpoint, and key.
4. Store transaction reference and safe response snapshot.
5. Return the original response for a duplicate retry.
6. Return 409 for key reuse with a different fingerprint.
7. Add request ID to logs and error responses.

### Test Cases

- Same request and key returns one transaction and one debit.
- Concurrent duplicate requests create one transaction.
- Same key with changed amount returns 409.
- PIN is not persisted in idempotency data.

### Done When

The demo can send the same request repeatedly without changing the balance after the first successful processing.

## Phase 8: Simulator, Attempts, Retry, Reversal, And DLQ

Priority: P1 and hackathon mandatory

### Implementation Order

1. Add simulator modes with deterministic forced outcomes.
2. Write one `PaymentAttempt` for every execution.
3. Classify errors as retryable or final.
4. Add configurable backoff scheduler.
5. Change retrying payments to `PENDING` and set `nextRetryAt`.
6. Move exhausted payments to `DEAD_LETTERED` and create DLQ record.
7. Add admin DLQ list, detail, and replay.
8. Add debit-success-credit-failed mode and compensating reversal.

### Test Cases

- Forced success succeeds once.
- Timeout creates three attempts and then DLQ.
- Invalid UPI never retries.
- Admin replay resumes the same transaction.
- Replay cannot double debit.
- Partial failure creates reversal entries and returns original sender balance.

### Done When

Swagger can demonstrate success, retry, DLQ, replay, and reversal on demand.

## Phase 9: Redis Cache, Rate Limit, And Distributed Lock

Priority: P1 and hackathon mandatory

### Implementation Order

1. Configure Redis serialization and key naming.
2. Cache UPI resolution.
3. Cache QR resolution with expiry-aware TTL.
4. Add Redis rate limiter to login, OTP, PIN, resolve, and payment endpoints.
5. Add distributed account lock around processing.
6. Keep database row locks and constraints as the final safety layer.

### Test Cases

- Cache hit returns the same public result.
- Updating/deactivating handle evicts cache.
- QR cache expires no later than QR record.
- Rapid payment requests return 429.
- Concurrent workers cannot process the same account simultaneously.
- Lock expiry cannot create duplicate ledger entries.

### Done When

The judge can see all three Redis use cases and each has a clear reason to exist.

## Phase 10: Transactional Outbox And Kafka

Priority: P1 and hackathon mandatory

This phase changes payment creation from synchronous completion to asynchronous acceptance.

### Implementation Order

1. Save payment plus `PaymentRequested` outbox row in one database transaction.
2. Return HTTP 202 with transaction ID.
3. Add outbox publisher with batch claiming and publication status.
4. Publish to a payment-requested Kafka topic using transaction ID as key.
5. Add consumer that checks `ProcessedEvent` before handling.
6. Move existing payment engine into the consumer orchestration path.
7. Mark processed event in the same database transaction as the final state change.
8. Add outbox publication retry and operational admin view.

### Test Cases

- If Kafka is down, accepted payment remains recoverable in outbox.
- Restarting app publishes pending outbox rows.
- Duplicate Kafka message does not create duplicate ledger entries.
- Two consumer instances preserve account correctness.
- Flutter can poll from 202 until terminal status.

### Done When

The full request-to-outbox-to-Kafka-to-consumer-to-ledger flow is visible and reproducible.

## Phase 11: Flutter Consumer App

Priority: P0 for extra credit, but backend remains first

### First Vertical Slice

1. App shell, theme, routing, API client, token storage.
2. Register, login, and authenticated route guard.
3. Bank activation screens.
4. UPI handle setup.
5. Home with account summary and recent activity.
6. UPI payment and processing polling.
7. Receipt, history, and transaction detail.

### Second Vertical Slice

1. Merchant QR resolver and payment confirmation.
2. Merchant profile setup.
3. QR generation and merchant received-payment list.
4. Optional admin Demo Lab.

### Verify

- Loading, empty, validation, offline, timeout, success, failure, pending, reversed, and DLQ states look intentional.
- No real financial logos or claims imply a real payment service.
- Long names, UPI IDs, and amounts fit on small phones.

## Phase 12: Tests And Observability

Priority: P0/P1

### Minimum Automated Tests

- Unit tests for amount rules, simulator classification, and request hashing.
- Service integration tests for auth, activation, payment, idempotency, ledger, retry, and reversal.
- Repository constraint tests for unique UPI ID, idempotency key, processed event, and ledger deduplication key.
- Controller tests for validation, 401, 403, 409, 410, 423, and 429.
- Concurrency test for double-spend prevention.
- Kafka integration test if time permits; otherwise one documented manual test with evidence.

### Observability

- Structured logs with request ID and transaction ID.
- Actuator health for database, Redis, and Kafka where practical.
- Metrics or logs for payment outcomes, retry count, DLQ count, outbox lag, cache hits, and rate-limit rejections.
- Never log secrets or mock verification input.

## Phase 13: Submission Package

Priority: P0

### README

- Product description and simulator disclaimer.
- Feature list and architecture.
- Technology choices and tradeoffs.
- Setup from a clean machine.
- Environment variables and Docker commands.
- Swagger URL and demo credentials.
- API examples including idempotency.
- Failure modes, retry, DLQ, caching, rate limiting, locking, Kafka, and outbox explanations.
- Security and PCI-inspired controls.
- Test commands and known limitations.

### Other Deliverables

- Final ER diagram.
- Final architecture diagram.
- Payment sequence and state diagrams.
- API collection or curl script.
- Screenshots.
- Two-to-five minute demo script.
- Recorded video.
- Optional deployed URL.

## Demo Order

1. Show architecture for ten seconds.
2. Register or use seeded consumer account.
3. Show activated bank, UPI ID, and balance in Flutter.
4. Send a successful UPI payment.
5. Show receipt, ledger effect, and timeline.
6. Repeat the same API request with the same idempotency key and show no second debit.
7. Pay an expiring merchant QR.
8. Force timeout mode.
9. Show attempts, backoff, and DLQ.
10. Restore success mode and replay DLQ safely.
11. Show Kafka UI, outbox row, Redis use cases, Swagger, and README.

## Schedule Reality

The original full estimate is 38 to 50 focused hours. With an August 16 deadline, the strict execution order is:

1. Backend P0 happy path.
2. Idempotency plus one clean failure contract.
3. Simulator, retry, DLQ, Redis, and Kafka/outbox.
4. Flutter only for the judged demo path.
5. Documentation and video before optional polish.

Do not spend deadline hours on camera scanning, analytics charts, rewards, split bills, or deployment while a required backend pattern is incomplete.
