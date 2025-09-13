# Deploying to AWS ECS (Fargate)

This project ships with a production-style setup to build a Docker image, push it to **ECR**, and deploy it on **ECS Fargate**, using **RDS (PostgreSQL)** for persistence. It’s “microservices-ready”: duplicate the task/service files per service.

---

## 1) Architecture Overview

- **ECR**: container registry (stores images built by CI).
- **ECS Fargate**: serverless containers. One service running one task definition.
- **ALB (optional but recommended)**: routes public traffic to the ECS service.
- **RDS (PostgreSQL)**: managed database.
- **CloudWatch Logs**: central logs from the container.

Container expects standard 12-factor config via env vars / secrets.

---

## 2) Prerequisites

- AWS account + IAM permissions.
- VPC with subnets and security groups for ALB/ECS/RDS.
- ECR repository (e.g., `fleet-backend`).
- ECS **Cluster** and **Service** (Fargate, `awsvpc` networking).
- RDS PostgreSQL (or Aurora Postgres).
- Two IAM roles:
    - `ecsTaskExecutionRole` (policy: `AmazonECSTaskExecutionRolePolicy`)
    - `ecsAppTaskRole` (permissions to read SSM Parameters/Secrets).

> You can provision these manually or via IaC (see `infra/terraform/` stub).

---

## 3) GitHub Actions: CI/CD

### CI
Runs on every push/PR:
- `.github/workflows/ci.yml` — builds and tests the app.

### CD
On push to `main` (or manual trigger):
- `.github/workflows/cd-aws-ecs.yml`
    1. Builds the JAR.
    2. Builds the Docker image (`docker/Dockerfile`).
    3. Logs into ECR and pushes the image (`:SHA` tag).
    4. Renders the ECS task definition (`deployment/ecs/taskdef.json`).
    5. Updates the ECS service and waits for stability.

#### Required GitHub secrets

AWS_ROLE_TO_ASSUME # OIDC-enabled IAM role ARN
AWS_REGION
ECR_REPOSITORY # e.g. fleet-backend
ECS_CLUSTER
ECS_SERVICE
ECS_TASK_FAMILY
RDS_ENDPOINT # e.g. mydb.abc123.eu-west-1.rds.amazonaws.com
RDS_DB_NAME
RDS_USERNAME

(Optional if not using SSM/Secrets)

RDS_PASSWORD
JWT_SECRET

> The workflow assumes OIDC for AWS auth (no long-lived keys).

---

## 4) Task Definition (Fargate)

File: `deployment/ecs/taskdef.json`

Key points:
- Single container named `app`, listening on **8080**.
- Logs to CloudWatch group `/ecs/fleet-backend`.
- Uses env + secrets for Spring configuration:
    - `SPRING_DATASOURCE_URL = jdbc:postgresql://${RDS_ENDPOINT}:5432/${RDS_DB_NAME}`
    - `SPRING_DATASOURCE_USERNAME = ${RDS_USERNAME}`
    - `SPRING_DATASOURCE_PASSWORD` via SSM (or Secrets Manager)
    - `APP_SECURITY_JWT_SECRET` via SSM (or Secrets Manager)

If you prefer Secrets Manager, replace the `valueFrom` ARNs accordingly.

---

## 5) Application Configuration (container)

The Docker image executes the JAR and relies on env vars:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_SECURITY_JWT_SECRET`
- Optional: `SERVER_PORT=8080`, `JAVA_OPTS=-Xms256m -Xmx512m`

Health checks:
- If you enable Spring Actuator, ALB target health can hit `/actuator/health`.

---

## 6) Bootstrapping (minimal checklist)

1. **Create ECR repo** (e.g., `fleet-backend`).
2. **Create ECS cluster** + **service** (Fargate) with:
    - Desired count 1+
    - Security groups allowing 8080 from ALB
    - Subnets in your VPC
3. **Create RDS PostgreSQL** and note host/DB/user.
4. **Store secrets**:
    - SSM Parameter: `/fleet/db/password`
    - SSM Parameter: `/fleet/jwt/secret`
    - Grant `ecsAppTaskRole` permission to read them.
5. **Configure GitHub Secrets** (see section 3).
6. **Push to `main`** → CD workflow builds, pushes, deploys.

---

## 7) Microservices-ready

To split services later:
- Duplicate `deployment/ecs/taskdef.json` per service (e.g., `contracts-taskdef.json`).
- Create a new ECS service per task family.
- Adjust container name, ports, log group, and env/secrets.
- Optionally split repositories (ECR) per service.

The CD workflow can be parameterized or duplicated for each service.

---

## 8) Local Docker Run (dev sanity check)

```bash
docker build -f docker/Dockerfile -t fleet-be:local .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/fleetdb \
  -e SPRING_DATASOURCE_USERNAME=fleet \
  -e SPRING_DATASOURCE_PASSWORD=fleet \
  -e APP_SECURITY_JWT_SECRET=dev-secret \
  fleet-be:local
  ```
 ---

## 9) Troubleshooting

- Service not stable: check CloudWatch logs for the task, verify DB connectivity and secrets.

- ALB 5xx: ensure target group health check path exists and SG rules allow traffic.

- Cannot pull image: check ECR permissions and that the image tag :SHA exists.

---

## 10) What to change for your account

- Replace <ACCOUNT_ID> placeholders in taskdef.json for IAM roles and secret ARNs.

- Ensure VPC/subnets/SGs are correct in your ECS service settings.

- If not using SSM, set DB password/JWT as GitHub secrets and pass them as plain env vars in the rendered task.


# Infrastructure & CI/CD Overview (Addendum)

This addendum explains the **technical intent** behind the `06-ci-aws-ecs-rds` branch.
It demonstrates how this backend can be containerized, built in CI, and deployed to AWS services in a **production-aligned** way—even if you don't fully provision all cloud resources yet.

---

## Goals

- Provide a **pipeline blueprint** that teams can adopt:
    - Build & test with GitHub Actions (CI)
    - Build Docker image and push to **ECR**
    - Deploy container to **ECS Fargate**
- Use **RDS (PostgreSQL)** as the managed database.
- Keep configuration **12-factor** (environment variables / secrets).
- Be **microservices-ready**: split into multiple independently deployable services later.
- Avoid long‑lived AWS keys by using **OIDC** from GitHub Actions.

---

## High-Level Architecture

```
Developer → GitHub → GitHub Actions (CI/CD)
                     |                |
                     | Build & Push   | Assume Role (OIDC)
                     v                v
                 Amazon ECR      AWS APIs (ECS, IAM, SSM)
                      |
                      v
               ECS Fargate Service  ←—  Task Definition (JSON)
                      |
                (optional) ALB
                      |
                    Users
                      |
                      v
                    Backend  ———→  RDS (PostgreSQL)
                         \———→  CloudWatch Logs
```

**Key pieces**

- **ECR** hosts versioned container images (`:SHA` tag).
- **ECS Fargate** runs the container without managing servers.
- (Optional) **ALB** exposes the service publicly and performs health checks.
- **RDS** provides a managed PostgreSQL instance.
- **CloudWatch Logs** centralizes container logs.
- **SSM Parameter Store / Secrets Manager** holds sensitive values (db password, JWT secret).
- **IAM Roles** split responsibilities: `ecsTaskExecutionRole` (pull image/emit logs) and `ecsAppTaskRole` (read secrets).

---

## CI/CD Pipelines

### CI (`.github/workflows/ci.yml`)
- Trigger: any push / PR.
- Steps: checkout → set up JDK 17 → `./gradlew clean build`.
- Outcome: unit tests + build verification.

### CD (`.github/workflows/cd-aws-ecs.yml`)
- Trigger: push to `main` or manual `workflow_dispatch`.
- Steps:
    1. Build JAR (`bootJar`).
    2. Build Docker image (`docker/Dockerfile`).
    3. Authenticate to **ECR** (OIDC via `configure-aws-credentials`).
    4. Push image with tag `:GITHUB_SHA`.
    5. Render **ECS Task Definition** (`deployment/ecs/taskdef.json`) with the new image.
    6. Update **ECS Service** and wait for stability.

**GitHub Secrets expected**
- `AWS_ROLE_TO_ASSUME`, `AWS_REGION`
- `ECR_REPOSITORY`, `ECS_CLUSTER`, `ECS_SERVICE`, `ECS_TASK_FAMILY`
- `RDS_ENDPOINT`, `RDS_DB_NAME`, `RDS_USERNAME` (and optionally `RDS_PASSWORD`)
- `JWT_SECRET` (if not using SSM/Secrets Manager)

> With SSM/Secrets, the workflow can omit DB/JWT plaintext secrets and only set non-sensitive envs.

---

## Container & Configuration

- Multi-stage `docker/Dockerfile` builds the JAR and produces a lean runtime image.
- The app reads configuration from environment variables (12‑factor):
    - `SPRING_DATASOURCE_URL = jdbc:postgresql://${RDS_ENDPOINT}:5432/${RDS_DB_NAME}`
    - `SPRING_DATASOURCE_USERNAME`
    - `SPRING_DATASOURCE_PASSWORD` (via SSM/Secrets in the task definition)
    - `APP_SECURITY_JWT_SECRET` (via SSM/Secrets)
    - optional: `SERVER_PORT`, `JAVA_OPTS`
- Logging to CloudWatch via the task’s `awslogs` driver.

---

## Microservices-Ready Strategy

This repo is prepared to scale into multiple services:

- Duplicate `deployment/ecs/taskdef.json` per service (e.g., `contracts-taskdef.json`).
- Create a new **ECS Service** per task family; reuse the same cluster.
- Give each service its own **ECR repository** and log group.
- Keep shared libraries in a `common/` module; keep service code isolated in its module.
- Optionally move each service to its own repository later (one repo per service).
- CI/CD workflow can be parameterized per path/service or duplicated with per‑service secrets.

**Service boundaries (example)**
- `fleet-service` (fleets, vehicles)
- `contract-service` (leasing/contracting)
- `maintenance-service` (maintenance records)
- `auth-service` (JWT/authn)

> Current code remains monolithic but the infra/pipeline layout anticipates this split.

---

## Security Considerations

- No AWS static keys stored in GitHub: use **OIDC** to assume a short‑lived role.
- Principle of least privilege:
    - `ecsTaskExecutionRole` → pull images, write logs.
    - `ecsAppTaskRole` → read strictly the parameters/secrets needed.
- Secrets never committed; provisioned via **SSM/Secrets** and injected at runtime.
- Separate **security groups** for ALB ↔ ECS ↔ RDS communication.

---

## Rollback & Releases

- **ECR** keeps previous image tags; re‑deploy an older SHA if needed.
- **ECS** preserves Task Definition revisions; roll back the service to a previous revision.
- Future enhancements: blue/green or canary deployments with CodeDeploy or weighted target groups.

---

## Environments & Promotion (suggested)

- Branch-based or tag-based promotion:
    - `develop` → **dev** environment
    - `release/*` → **staging**
    - `main` → **production**
- Each environment uses its own:
    - Cluster / service names
    - ECR repos (optional)
    - RDS instances (or databases)
    - SSM parameters / secrets namespaces

---

## Local Parity (sanity check)

Run locally against Dockerized Postgres:

```bash
docker build -f docker/Dockerfile -t fleet-be:local .
docker run --rm -p 8080:8080   -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/fleetdb   -e SPRING_DATASOURCE_USERNAME=fleet   -e SPRING_DATASOURCE_PASSWORD=fleet   -e APP_SECURITY_JWT_SECRET=dev-secret   fleet-be:local
```

---

## What’s intentionally out of scope (for now)

- Full infra provisioning (VPC/Subnets/ALB/RDS/IAM) — provided as **Terraform stubs** only.
- Advanced deployment strategies (blue/green, canary).
- Autoscaling policies and cost guardrails.
- Observability stack (metrics/traces).

This branch showcases knowledge and intent, keeping the implementation **lean** for demo purposes.
