# Razorpay Clone

A payment gateway backend modeled on Razorpay's core flows — merchant onboarding, order and payment lifecycle management, card tokenization through an isolated vault, webhook delivery, and settlements. Designed around domain-driven entity modeling and event-driven patterns (transactional outbox, dead-letter handling), with a target architecture of independently deployable microservices.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
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

## Design Patterns

| Pattern | Where | Purpose |
|---|---|---|
| **Strategy** | `payment.processor` (`PaymentProcessor`, `CardPaymentProcessor`, `NetBankingPaymentProcessor`, `UpiPaymentProcessor`) | Per-payment-method processing logic, selected at runtime via `PaymentProcessorRouter` |
| **Adapter** | `payment.gateway` (`PaymentAdapter`, `CardPaymentAdapter`, `NetBankingAdapter`, `UpiPaymentAdapter`) | Wraps gateway-specific initiate/capture logic behind a common interface, routed via `PaymentGatewayRouter` |
| **State Machine** | `payment.statemachine` (`PaymentStateMachine`, `PaymentTransitionService`) | Enforces valid payment status transitions via a transition table, with each transition logged to `PaymentTransitionLog` |

## Tech Stack

- **Language:** Java 25
- **Framework:** Spring Boot 4.1.1, Spring Data JPA, Spring Security
- **Database:** PostgreSQL
- **Mapping:** MapStruct
- **Auth:** JWT (jjwt)
- **Build Tool:** Maven
- **Planned:** Apache Kafka, Redis, Docker, Zipkin, Prometheus/Grafana

## Project Structure

```
src/main/java/com/ankitgupta/razorpay/
├── common/
│   ├── entity/         # Shared value types (Money, BaseEntity)
│   ├── enums/          # Shared enums (statuses, actors, events, roles)
│   ├── exception/      # GlobalExceptionHandler, custom exceptions, ErrorResponse
│   └── util/           # RandomizerUtil
├── merchant/
│   ├── entity/         # Merchant, ApiKey, AppUser, Customer, MerchantWebhookConfig
│   ├── controller/     # AuthController, ApiKeyController
│   ├── service/        # AuthService, ApiKeyService (+ impl)
│   ├── repository/     # MerchantRepository, ApiKeyRepository, AppUserRepository
│   ├── mapper/          # MerchantMapper, ApiKeyMapper
│   ├── security/        # JwtUtil, WebSecurityConfig, MerchantUserDetailsService
│   └── dto/             # request/response DTOs
├── payment/
│   ├── entity/         # OrderRecord, Payment, Refund, PaymentTransitionLog
│   ├── controller/     # OrderController, PaymentController
│   ├── service/        # OrderService, PaymentService (+ impl)
│   ├── repository/     # OrderRepository, PaymentRepository, PaymentTransitionLogRepository
│   ├── mapper/          # OrderMapper, PaymentMapper
│   ├── gateway/         # PaymentAdapter + per-method adapters (Card, NetBanking, UPI)
│   ├── processor/       # PaymentProcessor + per-method strategies (Card, NetBanking, UPI)
│   ├── statemachine/    # PaymentStateMachine, PaymentTransitionService
│   ├── simulator/       # BankCallbackSimulator, SimulatorConfig
│   ├── config/          # PaymentAdapterConfig, PaymentProcessorConfig
│   └── dto/             # request/response DTOs
├── vault/
│   ├── entity/          # VaultCard, CardToken
│   ├── controller/      # VaultController
│   ├── service/         # VaultService (+ impl)
│   ├── repository/      # VaultCardRepository, CardTokenRepository
│   ├── config/          # VaultEncryptionConfig
│   ├── validation/      # ExpiryYear custom validator
│   └── dto/              # request/response DTOs
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
- Merchant signup and login flow (`AuthController`, `AuthService`), password hashing via `BCryptPasswordEncoder`
- API key lifecycle: issuance, listing, revocation, rotation (`ApiKeyController`, `ApiKeyService`)
- Order creation flow (`OrderController`, `OrderService`)
- Payment initiation and capture flow (`PaymentController`, `PaymentService`)
- Payment routing via Strategy/Adapter pattern across Card, NetBanking, and UPI methods
- Payment state machine for status transitions, with transition logging
- Vault card tokenization with envelope encryption (AES-GCM, DEK/KEK key hierarchy)
- Bank callback simulator for asynchronous payment status polling
- JWT generation and verification (`JwtUtil`)
- Spring Security configuration scaffold (`WebSecurityConfig`, `MerchantUserDetailsService`)
- Repository layer for Merchant, ApiKey, AppUser, Order, Payment, PaymentTransitionLog, VaultCard, CardToken
- Global exception handling (`GlobalExceptionHandler`, custom exceptions, error response format)
- Request validation via `spring-boot-starter-validation`, including a custom `@ExpiryYear` validator
- Entity auditing (`BaseEntity` with `createdAt`/`updatedAt`, `@EnableJpaAuditing`)
- MapStruct mappers for Merchant, ApiKey, Order, Payment DTO conversion

## Development Log

### 2026-09-03
- Added merchant login flow: `AuthController` login endpoint, `LoginRequest`/`LoginResponse` DTOs
- Added JWT support: `JwtUtil` for token generation/verification (jjwt)
- Added Spring Security scaffold: `WebSecurityConfig`, `MerchantUserDetailsService`, `AppUser` implementing `UserDetails`
- Added `spring-boot-starter-security` and `jjwt` dependencies
- Fixed `PaymentTransitionService.apply()` `fromStatus` ordering bug
- Fixed `PaymentProcessorConfig` to use constructor-injected Spring beans instead of manual instantiation
- Fixed `SimulatorConfig` missing `@Getter`/`@Setter` for configuration property binding

### 2026-09-02
- Implemented payment initiation and capture flow: `PaymentController`, `PaymentService`/`PaymentServiceImpl`
- Added Strategy pattern for payment processing: `PaymentProcessor` interface with `CardPaymentProcessor`, `NetBankingPaymentProcessor`, `UpiPaymentProcessor`, routed via `PaymentProcessorRouter`
- Added Adapter pattern for gateway integration: `PaymentAdapter` interface with `CardPaymentAdapter`, `NetBankingAdapter`, `UpiPaymentAdapter`, routed via `PaymentGatewayRouter`
- Added payment state machine: `PaymentStateMachine`, `PaymentTransitionService`, `InvalidStateTransitionException`
- Added vault card tokenization: `VaultController`, `VaultService`/`VaultServiceImpl`, envelope encryption via `VaultEncryptionConfig`
- Added bank callback simulator: `BankCallbackSimulator`, `SimulatorConfig`
- Added `PaymentTransitionLogRepository`, `VaultCardRepository`, `CardTokenRepository`
- Added `PaymentAdapterConfig`, `PaymentProcessorConfig` for bean wiring

### 2026-09-01
- Added JPA auditing: `BaseEntity` with `@CreatedDate`/`@LastModifiedDate`, wired into entities via `@EnableJpaAuditing`
- Added MapStruct dependency and mappers: `MerchantMapper`, `ApiKeyMapper`, `OrderMapper`, `PaymentMapper`
- Added `BusinessRuleViolationException` for business-rule-level errors
- Added `PaymentResponse` DTO
- Refactored service layer to use mappers instead of manual DTO construction
- Fixed MapStruct core/processor version mismatch in `pom.xml`
- Fixed `SettlementPaymentId`/`SettlementPayment` auditing placement (moved `BaseEntity` inheritance to the entity, off the embedded ID)
- Fixed `MerchantMapper` field mapping for `status` → `merchantStatus`
- Added database indexes across entities: `ApiKey`, `Merchant`, `AppUser`, `Customer`, `MerchantWebhookConfig`, `Payment`, `OrderRecord`, `PaymentTransitionLog`

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
