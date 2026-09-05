# Razorpay Clone

A payment gateway backend modeled on Razorpay's core flows — merchant onboarding, order and payment lifecycle management, card tokenization through an isolated vault, webhook delivery, and settlements. Designed around domain-driven entity modeling and event-driven patterns (transactional outbox, dead-letter handling), with a target architecture of independently deployable microservices.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Development Status](#development-status)
- [Known Issues](#known-issues)
- [Development Log](#development-log)

## Overview

The system is designed around five domains:

| Domain         | Responsibility                                                                   |
| -------------- | -------------------------------------------------------------------------------- |
| **Merchant**   | Merchant accounts, KYC, API key issuance, dashboard users, webhook configuration |
| **Payment**    | Orders, payments, refunds, and payment state transitions                         |
| **Vault**      | PCI-scoped card storage and tokenization, isolated from the rest of the system   |
| **Operations** | Webhook delivery, settlements, dead-letter/event replay                          |
| **Common**     | Shared value types, enums, and cross-cutting concerns (auditing) used across domains |

## Architecture

Target deployment is a microservices architecture:

| Service              | Responsibility                                     |
| -------------------- | --------------------------------------------------- |
| `api-gateway`        | Authentication, rate limiting, request routing     |
| `merchant-service`   | Merchant authorization, KYC, API key management    |
| `payment-service`    | Order creation, payment processing, refunds        |
| `operations-service` | Webhook dispatch, settlement processing, analytics |
| `vault-service`      | Isolated PCI scope; card tokenization              |
| `discovery-service`  | Service discovery                                  |
| `config-service`     | Centralized configuration                          |
| `common-lib`         | Shared models and utilities                        |

Asynchronous communication is handled via **Kafka** using the outbox pattern; **Redis** is used for caching and counters. Observability is provided through **Zipkin** (distributed tracing) and **Prometheus/Grafana** (metrics).

The system is currently implemented as a single Spring Boot module — domain modeling and business logic are being built out first, with the microservices split planned once core flows are validated.

**Authentication** is split by consumer:
- **Dashboard/admin routes** (`/v1/auth/**`, `/v1/merchant/**`, `/v1/admin/**`, `/v1/actuator/**`) are protected by a stateless **JWT** filter chain. Tokens carry the merchant's email as subject plus `merchant_id` and `role` claims.
- **API-facing routes** (`/v1/orders/**`, `/v1/payments/**`, `/v1/vault/**`) are protected by a **Basic Auth-style API key** filter, validated against BCrypt-hashed key secrets, with a 24-hour grace period honored for recently rotated keys.

Both filters populate a request-scoped `MerchantContext`, so downstream controllers/services no longer need the merchant ID passed in manually. JPA auditing (`BaseEntity` + `AuditorAwareImpl`) uses this same context to stamp `createdBy`/`updatedBy` with the API key ID (or merchant ID as a fallback).

API key lookups are backed by a **Redis cache** (`ApiKeyCache` / `RedisApiKeyCache`, 5-minute TTL) so the API-key filter doesn't hit Postgres on every request; a cache miss falls back to the DB and repopulates the cache. Each API-key-authenticated request is also checked against a **Redis-backed fixed-window rate limiter** (`RateLimiter` / `FixedWindowRateLimiter`), keyed per API key, with configurable requests-per-minute. Requests over the limit get a `429` with `Retry-After` / `X-RateLimit-*` headers; allowed requests get their remaining quota back in the response headers.

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.1.1, Spring Data JPA, Spring Security
- **Auth:** JWT (`jjwt`), BCrypt-hashed API keys
- **Database:** PostgreSQL
- **Cache / Rate Limiting:** Redis (`spring-boot-starter-data-redis`) — API key cache + fixed-window rate limiter
- **Object Mapping:** MapStruct
- **Build Tool:** Maven
- **Planned:** Apache Kafka, Docker, Zipkin, Prometheus/Grafana

## Project Structure

```
src/main/java/com/ankitgupta/razorpay/
├── common/
│   ├── entity/         # Shared value types (Money), BaseEntity (created/updated audit columns)
│   ├── audit/           # AuditorAwareImpl — resolves current actor for JPA auditing
│   ├── enums/          # Shared enums (statuses, actors, events, roles)
│   ├── exception/      # GlobalExceptionHandler, custom exceptions (incl. RateLimitException), ErrorResponse
│   ├── ratelimit/       # RateLimiter, FixedWindowRateLimiter, RateLimitResult
│   ├── config/          # RedisConfig
│   └── util/           # RandomizerUtil
├── merchant/
│   ├── entity/         # Merchant, ApiKey, AppUser, Customer, MerchantWebhookConfig
│   ├── controller/     # AuthController, ApiKeyController
│   ├── service/        # AuthService, ApiKeyService (+ impl)
│   ├── repository/     # MerchantRepository, ApiKeyRepository, AppUserRepository
│   ├── security/       # WebSecurityConfig, JwtUtil, JwtAuthenticationFilter,
│   │                    # ApiKeyAuthenticationFilter, MerchantContext
│   ├── cache/           # ApiKeyCache, ApiKeyCacheEntry, RedisApiKeyCache
│   └── dto/             # request/response DTOs
├── payment/
│   ├── entity/         # OrderRecord, Payment, Refund, PaymentTransitionLog
│   ├── controller/     # OrderController, PaymentController
│   ├── service/        # OrderService, PaymentService (+ impl)
│   ├── repository/     # OrderRepository, PaymentRepository
│   └── dto/             # request/response DTOs
├── vault/
│   ├── entity/         # VaultCard, CardToken
│   ├── controller/     # VaultController
│   ├── service/        # VaultService
│   └── dto/             # request/response DTOs
└── operations/
    └── entity/         # WebhookEvent, Settlement, SettlementPayment, DlqEvent
```

## Getting Started

### Prerequisites

- Java 25
- PostgreSQL
- Maven (or use the included `mvnw` wrapper)

### Installation

```
git clone <repo-url>
cd razorpay
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

Set the following environment variables:

| Variable      | Description                                                  |
| ------------- | -------------------------------------------------------------- |
| `DB_URL`      | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/razorpayDB` |
| `DB_USERNAME` | PostgreSQL username                                          |
| `DB_PASSWORD` | PostgreSQL password                                          |
| `SECRET_KEY`  | Secret key used to sign/verify JWT access tokens              |
| `REDIS_HOST`  | Redis host (defaults to `localhost`)                          |
| `REDIS_PORT`  | Redis port (defaults to `6379`)                               |
| `REDIS_PASSWORD` | Redis password, if any (defaults to empty)                 |

A local Redis instance is required — e.g. `docker run -p 6379:6379 redis` — for the API key cache and rate limiter to work.

### Run

```
./mvnw spring-boot:run
```

## Development Status

**Completed**

- JPA entity layer modeled across all five domains (Merchant, Payment, Vault, Operations, Common)
- JPA auditing (`BaseEntity`, `AuditorAwareImpl`) stamping `createdAt`/`updatedAt`/`createdBy`/`updatedBy` on entities
- Merchant signup flow (`AuthController`, `AuthService`)
- API key lifecycle: issuance, listing, revocation, rotation with grace period (`ApiKeyController`, `ApiKeyService`)
- Dual Spring Security filter chains: JWT auth for dashboard/admin routes, API key (Basic-style) auth for API routes (`WebSecurityConfig`, `JwtUtil`, `JwtAuthenticationFilter`, `ApiKeyAuthenticationFilter`)
- Request-scoped `MerchantContext` propagating the authenticated merchant across controllers/services
- Order creation flow (`OrderController`, `OrderService`)
- Payment initiation and capture endpoints (`PaymentController`) — see [Known Issues](#known-issues)
- Card tokenization endpoint (`VaultController`)
- Repository layer for Merchant, ApiKey, AppUser, Order, Payment
- Global exception handling (`GlobalExceptionHandler`, custom exceptions, error response format)
- Request validation via `spring-boot-starter-validation`
- Redis-backed API key cache (`ApiKeyCache`/`RedisApiKeyCache`) to avoid a DB hit on every API-key-authenticated request
- Redis-backed fixed-window rate limiter (`RateLimiter`/`FixedWindowRateLimiter`) applied per API key in `ApiKeyAuthenticationFilter`, with `429`/`Retry-After`/`X-RateLimit-*` handling in `GlobalExceptionHandler`

**In progress**

- Payment capture / state-transition logic
- Refunds
- Webhook delivery and settlements (Operations domain)

## Known Issues

- **Payments:** the payment initiate/capture flow (`PaymentController` → `PaymentService`) has a known issue currently being debugged — treat this endpoint as unstable until it's fixed and this note is removed.

This project is being built step by step, domain by domain, rather than end-to-end — expect other rough edges outside of what's listed above as work progresses.

## Development Log

### 2026-09-05

- Added Redis integration (`RedisConfig`, `StringRedisTemplate` bean)
- Added `ApiKeyCache` interface with a `RedisApiKeyCache` implementation (5-minute TTL) — `ApiKeyAuthenticationFilter` now checks the cache before hitting `ApiKeyRepository`, and repopulates the cache on a miss
- Added Redis-backed fixed-window rate limiting: `RateLimiter` interface, `FixedWindowRateLimiter` implementation, `RateLimitResult`, keyed per API key and applied inside `ApiKeyAuthenticationFilter`
- Added `RateLimitException` and a corresponding handler in `GlobalExceptionHandler` returning `429` with `Retry-After` / `X-RateLimit-Remaining` / `X-RateLimit-Reset` headers
- `ApiKeyServiceImpl` now evicts the Redis cache entry on key revoke and rotate, so stale keys/secrets stop working immediately instead of waiting out the TTL
- Added `spring-boot-starter-data-redis` dependency and Redis connection settings (`REDIS_HOST`/`REDIS_PORT`/`REDIS_PASSWORD`) to `application.yaml.example`

### 2026-09-04

- Added Spring Security: `WebSecurityConfig` with two filter chains — JWT for dashboard/admin routes, API key (Basic-style, BCrypt-verified) for API routes
- Added `JwtUtil` (access token generation/verification) and `JwtAuthenticationFilter`
- Added `ApiKeyAuthenticationFilter`, including grace-period support for rotated keys
- Added request-scoped `MerchantContext` and wired it through `OrderController`, `PaymentController`, `VaultController`, and `ApiKeyController` in place of manually passed merchant IDs
- Added JPA auditing support: `BaseEntity` (`createdAt`/`updatedAt`/`createdBy`/`updatedBy`) and `AuditorAwareImpl`, enabled via `@EnableJpaAuditing` in `RazorpayApplication`
- Added `PaymentController` with payment initiation and capture endpoints (known issue — see [Known Issues](#known-issues))
- Added `VaultController` with a card tokenization endpoint

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
