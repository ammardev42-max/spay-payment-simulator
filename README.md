# SPay

**Secure | Swift | Safe**

SPay is a Spring Boot based UPI payment switch simulator for the Build-A-Thon hackathon. It simulates a GPay-style journey without real money, real UPI rails, real bank APIs, or real card storage.

## MVP Scope

- Register and login users.
- Simulate bank discovery using a registered phone number.
- Simulate OTP and debit-card verification without storing card details.
- Set a BCrypt-hashed UPI PIN.
- Create SPay UPI handles like `ammar@spay`.
- Send user-to-user UPI payments.
- Pay merchants using fake SPay QR payloads.
- Track transaction status, ledger entries, timeline events, retries, DLQ, and outbox events.

## Local Stack

Required:

- Java 21+
- Docker Desktop
- Maven wrapper included in this project

Start infrastructure:

```bash
docker compose up -d
```

Run backend:

```bash
./mvnw spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Kafka UI:

```text
http://localhost:8081
```

## Local Services

| Service | URL |
| --- | --- |
| Backend | `http://localhost:8080` |
| Swagger | `http://localhost:8080/swagger-ui.html` |
| PostgreSQL | `localhost:5432/spay_db` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |
| Kafka UI | `http://localhost:8081` |

## Environment Variables

| Variable | Default |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/spay_db` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `Root@123` |
| `REDIS_HOST` | `localhost` |
| `REDIS_PORT` | `6379` |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` |
| `SERVER_PORT` | `8080` |

## Security Note

SPay uses PCI-inspired controls for the demo: it never accepts or stores real card numbers, CVV, real bank credentials, or real UPI credentials. Mock debit-card verification values are transient only. Passwords and UPI PINs are stored only as BCrypt hashes.

## Docs

- `docs/architecture.md`
- `docs/build-plan.md`
- `docs/frontend-flow.md`
- `docs/er-diagram.mmd`
- `docs/architecture-diagram.mmd`
- `docs/payment-sequence.mmd`
