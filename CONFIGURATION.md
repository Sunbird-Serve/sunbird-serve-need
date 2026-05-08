# Configuration Guide

## How It Works

All configuration lives in a single `application.properties` file using `${ENV_VAR:default}` syntax. You control behavior per environment by setting environment variables — not by maintaining separate properties files.

The only exception is `application-test.properties` which switches to an H2 in-memory database for unit tests.

## Quick Start (Local Dev)

1. Copy the template:
   ```bash
   cp env-template.txt .env
   ```

2. Edit `.env` with your local values (it's gitignored, safe to put real passwords).

3. Run:
   ```bash
   ./gradlew bootRun
   ```

   Spring Boot will pick up `.env` if you use a dotenv plugin, or export them:
   ```bash
   export $(cat .env | grep -v '^#' | xargs) && ./gradlew bootRun
   ```

## Deployment (Prod / Stage)

Pass environment variables via your deployment mechanism (Docker, systemd, ECS task definition, etc.):

```bash
docker run -e DB_URL=jdbc:postgresql://prod-host:5432/serve-need \
           -e DB_PASSWORD=secret \
           -e SPRING_PROFILES_ACTIVE=prod \
           -e CORS_ALLOWED_ORIGINS=https://serve-v1.evean.net \
           -e SWAGGER_ENABLED=false \
           your-image:latest
```

No properties files to swap. No credentials in git.

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/serve-need` | JDBC connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `DB_POOL_MAX` | `10` | Max connection pool size |
| `DB_POOL_MIN_IDLE` | `2` | Min idle connections |
| `DB_POOL_IDLE_TIMEOUT` | `300000` | Idle timeout (ms) |
| `DB_POOL_CONN_TIMEOUT` | `20000` | Connection timeout (ms) |
| `JPA_SHOW_SQL` | `false` | Log SQL statements |
| `JPA_FORMAT_SQL` | `false` | Pretty-print SQL |
| `JPA_DDL_AUTO` | `validate` | Hibernate DDL strategy |
| `HIBERNATE_BATCH_SIZE` | `25` | JDBC batch size |
| `HIBERNATE_SQL_LOG_LEVEL` | `WARN` | Hibernate SQL log level |
| `HIBERNATE_BINDER_LOG_LEVEL` | `WARN` | Hibernate binder log level |
| `APP_LOG_LEVEL` | `INFO` | Application log level |
| `NETTY_LOG_LEVEL` | `WARN` | Netty HTTP client log level |
| `LOG_FILE_NAME` | `needLog.log` | Log file name |
| `INCLUDE_EXCEPTION` | `false` | Include exception in error responses |
| `INCLUDE_STACKTRACE` | `never` | Include stacktrace in error responses |
| `ACTUATOR_ENDPOINTS` | `health,info,metrics` | Exposed actuator endpoints |
| `ACTUATOR_HEALTH_DETAILS` | `when-authorized` | Health detail visibility |
| `SWAGGER_ENABLED` | `true` | Enable Swagger UI |
| `BASIC_AUTH_USERNAME` | `admin` | Basic auth username |
| `BASIC_AUTH_PASSWORD` | `admin` | Basic auth password |
| `CORS_ALLOWED_ORIGINS` | `*` | Comma-separated allowed origins |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev`, `prod`, `test`) |

## Profiles (Security Only)

Profiles are still used to activate the correct security configuration class:

- `dev` / `test` → `SecurityConfig.java` (open access, no auth)
- `prod` → `ProductionSecurityConfig.java` (restricted CORS, auth-ready)
- `basic-auth` → `BasicAuthSecurityConfig.java` (username/password)

All other behavior (DB, logging, pooling, swagger) is driven purely by env vars.

## Running Tests

```bash
./gradlew test
```

This automatically uses `application-test.properties` (H2 in-memory DB).
