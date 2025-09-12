# Fleet Management Backend 🚗🛠️

Backend for a **Fleet Management MVP for Rental Vehicles**. It exposes **GraphQL** APIs to manage **vehicles, drivers, contracts, and maintenance**, built with **Kotlin + Spring Boot**, **PostgreSQL**, and **JWT**.

---

## 📌 Project Overview

**What it is**  
A modular backend to administer **vehicle fleets**.

**Goal**  
Provide GraphQL queries/mutations and secure persistence to manage:
- **Vehicles**
- **Drivers**
- **Contracts (leasing)**
- **Maintenance records**

---

## 🛠️ Tech Stack

- **Language/Framework:** Kotlin + Spring Boot (Java 17)
- **API:** Spring Boot GraphQL Starter
- **Database:** PostgreSQL (Docker) + **pgAdmin**
- **Security:** Spring Security + JWT (initial setup in place)
- **Testing:** JUnit 5 + Testcontainers
- **Local Infra:** Docker Compose
- **CI/CD:** _planned_ (GitHub Actions / GitLab CI)

---

## 🚀 Getting Started

### Prerequisites
- **Docker & Docker Compose**
- **JDK 17**
- **Gradle wrapper** (use `./gradlew`, already in the repo)

### 1) Start Postgres + pgAdmin

From the repository root (or wherever your `docker-compose.yml` lives):

```bash
docker compose up -d
# Services:
# - postgres  -> 5432
# - pgadmin   -> 5050
```

Default connection details (adjust to your `docker-compose.yml`):
- **JDBC:** `jdbc:postgresql://localhost:5432/fleetdb`
- **User/Pass:** `fleet` / `fleet`
- **pgAdmin UI:** http://localhost:5050

### 2) Configure the application

Edit `src/main/resources/application.properties` (or `application.yml`) with your local values:

```properties
# --- Datasource ---
spring.datasource.url=jdbc:postgresql://localhost:5432/fleetdb
spring.datasource.username=fleet
spring.datasource.password=fleet
spring.jpa.hibernate.ddl-auto=update

# --- GraphQL ---
spring.graphql.path=/graphql
# Optional dev UI:
spring.graphql.graphiql.enabled=true

# --- Security (JWT placeholders) ---
app.security.jwt.secret=CHANGE_ME
app.security.jwt.expiration=3600
```

### 3) Run the backend

```bash
./gradlew bootRun
```

- GraphQL endpoint: `POST /graphql`
- If GraphiQL is enabled: `/graphiql`

### 4) Run tests

```bash
./gradlew test
# Integration tests that use Testcontainers require Docker to be running.
```

---

## 📂 Project Structure

```
src
├─ main
│  ├─ kotlin/com/fleet/management/be
│  │  ├─ common/             # shared utilities, helpers, TestData, etc.
│  │  ├─ config/             # configuration (security, beans, etc.)
│  │  └─ modules/
│  │     ├─ auth/
│  │     │  ├─ application/      # use cases (application services)
│  │     │  ├─ domain/           # domain entities/aggregates
│  │     │  └─ infrastructure/
│  │     │     ├─ graphql/       # GraphQL resolvers (e.g., AuthResolver)
│  │     │     └─ jpa/           # JPA repositories (e.g., JpaUserRepository)
│  │     └─ vehicle/
│  │        ├─ application/      # use cases (ListVehicles, GetVehicleById, …)
│  │        ├─ domain/           # Vehicle entity and domain logic
│  │        └─ infrastructure/
│  │           ├─ graphql/       # resolvers (VehicleResolver, VehicleMutationResolver)
│  │           └─ jpa/           # repositories (JpaVehicleRepository)
│  └─ resources/
│     └─ graphql/schema.graphqls # GraphQL schema
└─ test
   └─ kotlin/...                 # unit & integration tests (JUnit + Testcontainers)
```

> **Where things live**
> - **GraphQL resolvers:** `modules/**/infrastructure/graphql`
> - **JPA repositories:** `modules/**/infrastructure/jpa`

---

## ✅ Features Implemented So Far

- ✅ Project bootstrap with **Kotlin + Spring Boot**
- ✅ **Vehicle** entity and **GraphQL query `vehicles`**
- ✅ **PostgreSQL** connectivity via **Docker Compose**
- ✅ **pgAdmin** integration for DB management
- ✅ Initial **JWT** scaffolding and auth module structure
- ✅ Tests with **JUnit** (Testcontainers-ready project)

**Example query**

```graphql
query {
  vehicles {
    id
    # plateNumber
    # brand
    # model
  }
}
```

---

## 🌱 Roadmap / Next Steps

- ✳️ GraphQL **mutations** for creating vehicles
- ✳️ **Driver** entity + association with `Vehicle`
- ✳️ **Fleet** entity to group vehicles and drivers
- ✳️ **Contract** (leasing) entity + mutations
- ✳️ **MaintenanceRecord** entity for maintenance logs
- ✳️ Full **JWT** flow (login, register, refresh token)
- ✳️ GraphQL documentation and schema enhancements
- ✳️ Unit & integration tests with **Testcontainers**
- ✳️ **CI/CD** pipeline (example with GitHub Actions)

---

## 🌿 Branching Strategy

We follow an **incremental / stacked branching** approach:

- `01-init` – initial project setup
- `02-auth` – JWT security configuration and basic auth scaffolding
- `03-vehicle` – Vehicle entity + base query
- (and upcoming branches for Driver, Fleet, Contracts, Maintenance, …)

> Keep PRs **small and focused** to ease code review and speed up delivery.

---

## 🤝 Contributing

1. Create a small, focused branch off `main`.
2. Add tests for your change.
3. Open a PR with a clear description and checklist.
4. Keep PRs incremental (prefer multiple small PRs over a single large one).

---

## 📄 License

If this project will be open-sourced, add a license file (e.g., `MIT`, `Apache-2.0`) and reference it here.  
_For now, this section is a placeholder until a license is selected._