# Keycloak POC — Phase 1: Setup

## What This Sets Up

- Keycloak 24 running on `http://localhost:8080`
- PostgreSQL 15 as Keycloak's database (port 5433, won't conflict with your app DB on 5432)
- Realm: `sunbird-serve`
- Client: `serve-ui` (public, PKCE-enabled, for React SPA)
- 6 Roles: sAdmin, nAdmin, nCoordinator, vAdmin, vCoordinator, Volunteer
- 2 Groups with attributes (agencyId, agencyType)
- 4 Test users with passwords, roles, and group memberships
- Protocol mappers so JWT contains: roles, agencyId, agencyType, email, preferred_username

---

## Prerequisites

- Docker & Docker Compose installed
- Port 8080 available
- PostgreSQL running locally on port 5432 (same instance your app uses)

---

## Steps

### 1. Create the Keycloak Database

In your local PostgreSQL, create a separate database for Keycloak:

```bash
psql -U postgres -c "CREATE DATABASE keycloak;"
```

Or via pgAdmin / DBeaver — just create a database named `keycloak`.

### 2. Start Keycloak

```bash
cd keycloak-poc
docker-compose up -d
```

Wait ~30 seconds for Keycloak to start and import the realm.

### 3. Verify Keycloak Is Running

Open: http://localhost:8080

Admin Console login:
- Username: `admin`
- Password: `admin123`

### 4. Verify Realm Import

1. Go to Admin Console → select realm dropdown (top-left) → choose `sunbird-serve`
2. Check **Clients** → `serve-ui` should exist
3. Check **Realm Roles** → all 6 roles should be listed
4. Check **Groups** → `need-agency-demo` and `volunteer-agency-demo` with attributes
5. Check **Users** → 4 test users

### 5. Test Login & Get a Token

Use this curl command to get a token via Resource Owner Password Grant (for quick testing):

```bash
curl -X POST http://localhost:8080/realms/sunbird-serve/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=serve-ui" \
  -d "grant_type=password" \
  -d "username=needadmin@test.org" \
  -d "password=test1234"
```

You'll get back a JSON with `access_token`, `refresh_token`, etc.

### 6. Decode the Token

Copy the `access_token` value and paste it at https://jwt.io

You should see these custom claims in the payload:
```json
{
  "preferred_username": "needadmin@test.org",
  "email": "needadmin@test.org",
  "roles": ["nAdmin"],
  "agencyId": "agency-need-001",
  "agencyType": "NeedAgency"
}
```

### 7. Verify All Users

Repeat step 4 with each user:

| Username | Password | Expected Role | Expected Agency |
|----------|----------|---------------|-----------------|
| needadmin@test.org | test1234 | nAdmin | agency-need-001 / NeedAgency |
| ncoord@test.org | test1234 | nCoordinator | agency-need-001 / NeedAgency |
| vadmin@test.org | test1234 | vAdmin | agency-vol-001 / VolunteerAgency |
| vcoord@test.org | test1234 | vCoordinator | agency-vol-001 / VolunteerAgency |

---

## Useful Endpoints

| Endpoint | Purpose |
|----------|---------|
| http://localhost:8080 | Keycloak Admin Console |
| http://localhost:8080/realms/sunbird-serve/.well-known/openid-configuration | OIDC discovery document |
| http://localhost:8080/realms/sunbird-serve/protocol/openid-connect/certs | JWKS (public keys for token verification) |
| http://localhost:8080/realms/sunbird-serve/protocol/openid-connect/token | Token endpoint |

---

## Cleanup

```bash
cd keycloak-poc
docker-compose down
```

To also drop the Keycloak database (clean slate):
```bash
psql -U postgres -c "DROP DATABASE keycloak;"
```

---

## Troubleshooting

**Realm not imported?**
- Check logs: `docker-compose logs keycloak`
- The import only runs on first start. If you need to re-import, drop and recreate the `keycloak` database, then `docker-compose up -d` again.

**Keycloak can't connect to Postgres?**
- Ensure your local Postgres accepts connections from Docker. Check `pg_hba.conf` allows `host all all 0.0.0.0/0 md5` (or scram-sha-256).
- On Windows, `host.docker.internal` should resolve to the host automatically.

**Port 8080 in use?**
- Change the port mapping in `docker-compose.yml`: `"8180:8080"` and update all URLs accordingly.

**Token doesn't have custom claims?**
- Go to Admin Console → Clients → serve-ui → Client Scopes → Dedicated scope → Mappers
- Verify all 5 mappers are present and enabled

---

## Next Phase

Once this is verified, Phase 2 will update `serve-need` backend to validate these JWTs and expose `GET /auth/me`.
