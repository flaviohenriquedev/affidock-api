# Affidock API

Backend modular do Affidock com base gen\u00E9rica de CRUD, auditoria autom\u00E1tica, status de entidade, autentica\u00E7\u00E3o JWT com refresh token e migrations Flyway.

## Stack

- Java 21
- Spring Boot 4
- Spring Security (JWT Resource Server)
- Spring Data JPA
- Flyway
- PostgreSQL

## Ambientes

Arquivos de configura\u00E7\u00E3o:

- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `src/main/resources/application-prod.properties`

Ativo por padr\u00E3o:

- `spring.profiles.active=dev`

Credenciais atuais (dev/prod, provis\u00F3rias):

- Host: `localhost`
- Database: `affidock`
- User: `postgres`
- Password: `postgres`

## Migrations

As migrations ficam em:

- `src/main/resources/db/migration`

Migration inicial:

- `V1__init_schema.sql`

## Autentica\u00E7\u00E3o

Endpoints principais:

- `POST /api/v1/auth/google/exchange`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`

Fluxo:

1. Frontend (NextAuth Google) chama `google/exchange`.
2. Backend cria/sincroniza usu\u00E1rio.
3. Backend emite `accessToken` + `refreshToken`.
4. Frontend renova access token via `refresh`.

## Unicode escaped (refer\u00EAncia de acentos)

Para evitar problemas de codifica\u00E7\u00E3o em arquivos `.properties`, usar escapes Unicode:

- \u00E1 = `\\u00E1`
- \u00E0 = `\\u00E0`
- \u00E2 = `\\u00E2`
- \u00E3 = `\\u00E3`
- \u00E9 = `\\u00E9`
- \u00EA = `\\u00EA`
- \u00ED = `\\u00ED`
- \u00F3 = `\\u00F3`
- \u00F4 = `\\u00F4`
- \u00F5 = `\\u00F5`
- \u00FA = `\\u00FA`
- \u00E7 = `\\u00E7`
- \u00C1 = `\\u00C1`
- \u00C9 = `\\u00C9`
- \u00CD = `\\u00CD`
- \u00D3 = `\\u00D3`
- \u00DA = `\\u00DA`
- \u00C7 = `\\u00C7`
