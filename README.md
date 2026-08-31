# Razorpay Clone

A payment gateway backend modeled on Razorpay's core flows — merchant onboarding, order and payment lifecycle management, card tokenization through an isolated vault, webhook delivery, and settlements. Designed around domain-driven entity modeling and event-driven patterns (transactional outbox, dead-letter handling), with a target architecture of independently deployable microservices.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Development Status](#development-status)
- [Development Log](#development-log)

## Overview

The system is designed around five domains:

| Domain | Responsibility |
|---|---|
| **Merchant** | Merchant accounts, KYC, API key issuance, dashboard users, webhook configuration |
| **Payment** | Orders, payments, refunds, and payment state transitions |
| **Vault** | PCI-scoped card storage and tokenization, isolated from the rest of the system |
| **Operations** | Webhook delivery, settlements, dead-letter/event replay |
| **Common** | Shared value types and enums used across domains |

## Architecture

Target deployment is a microservices architecture:

| Service | Responsibility |
|---|---|
| `api-gateway` | Authentication, rate limiting, request routing |
| `merchant-service` | Merchant authorization, KYC, API key management |
| `payment-service` | Order creation, payment processing, refunds |
| `operations-service` | Webhook dispatch, settlement processing, analytics |
| `vault-service` | Isolated PCI scope; card tokenization |
| `discovery-service` | Service discovery |
| `config-service` | Centralized configuration |
| `common-lib` | Shared models and utilities |

Asynchronous communication is handled via **Kafka** using the outbox pattern; **Redis** is used for caching and counters. Observability is provided through **Zipkin** (distributed tracing) and **Prometheus/Grafana** (metrics).

The system is currently implemented as a single Spring Boot module — domain modeling and business logic are being built out first, with the microservices split planned once core flows are validated.

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.1.1, Spring Data JPA
- **Database:** PostgreSQL
- **Build Tool:** Maven
- **Planned:** Spring Security, Apache Kafka, Redis, Docker, Zipkin, Prometheus/Grafana

## Project Structure

```
src/main/java/com/ankitgupta/razorpay/
├── common/
│   ├── entity/         # Shared value types (Money)
│   ├── enums/          # Shared enums (statuses, actors, events, roles)
│   ├── exception/      # GlobalExceptionHandler, custom exceptions, ErrorResponse
│   └── util/           # RandomizerUtil
├── merchant/
│   ├── entity/         # Merchant, ApiKey, AppUser, Customer, MerchantWebhookConfig
│   ├── controller/     # AuthController, ApiKeyController
│   ├── service/        # AuthService, ApiKeyService (+ impl)
│   ├── repository/     # MerchantRepository, ApiKeyRepository, AppUserRepository
│   └── dto/             # request/response DTOs
├── payment/
│   ├── entity/         # OrderRecord, Payment, Refund, PaymentTransitionLog
│   ├── controller/     # OrderController
│   ├── service/        # OrderService (+ impl)
│   ├── repository/     # OrderRepository, PaymentRepository
│   └── dto/             # request/response DTOs
├── vault/
│   └── entity/         # VaultCard, CardToken
└── operations/
    └── entity/         # WebhookEvent, Settlement, SettlementPayment, DlqEvent
```

## Getting Started

### Prerequisites

- Java 25
- PostgreSQL
- Maven (or use the included `mvnw` wrapper)

### Installation

```bash
git clone <repo-url>
cd razorpay
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

Set the following environment variables:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/razorpayDB` |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |

### Run

```bash
./mvnw spring-boot:run
```

## Development Status

**Completed**
- JPA entity layer modeled across all five domains (Merchant, Payment, Vault, Operations, Common)
- Merchant signup flow (`AuthController`, `AuthService`)
- API key lifecycle: issuance, listing, revocation, rotation (`ApiKeyController`, `ApiKeyService`)
- Order creation flow (`OrderController`, `OrderService`)
- Repository layer for Merchant, ApiKey, AppUser, Order, Payment
- Global exception handling (`GlobalExceptionHandler`, custom exceptions, error response format)
- Request validation via `spring-boot-starter-validation`

## Development Log

### 2026-08-31
- Added API key management: list keys by merchant, revoke key, rotate key (with grace period on the previous secret)
- Implemented order creation flow: `OrderController`, `OrderService`/`OrderServiceImpl`, duplicate-receipt detection, configurable order expiry
- Added `RandomizerUtil` for key/secret generation
- Added `jackson-databind` dependency
- Refactored `Money` to use Lombok annotations instead of manual boilerplate

### 2026-08-30
- Implemented merchant signup flow: `AuthController`, `AuthService`/`AuthServiceImpl`, `MerchantSignupRequest`/`MerchantResponse` DTOs
- Implemented API key issuance flow: `ApiKeyController`, `ApiKeyService`/`ApiKeyServiceImpl`, `CreateApiKeyRequest`/`ApiKeyCreateResponse` DTOs
- Added repository layer: `MerchantRepository`, `ApiKeyRepository`, `AppUserRepository`
- Added global exception handling: `GlobalExceptionHandler`, `ResourceNotFoundException`, `DuplicateResourceException`, `ErrorResponse`
- Added `spring-boot-starter-validation` dependency for request validation

### 2026-08-27
- Modeled full entity relationship schema and JPA entities across merchant, payment, vault, and operations domains
- Initialized project with Spring Boot 4.1.1, Java 25, Spring Data JPA, PostgreSQL, Lombok
