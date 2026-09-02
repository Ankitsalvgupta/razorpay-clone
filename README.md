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
- **Framework:** Spring Boot 4.1.1, Spring Data JPA, Spring Security
- **Database:** PostgreSQL
- **Build Tool:** Maven
- **Planned:** Apache Kafka, Redis, Docker, Zipkin, Prometheus/Grafana

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
│   ├── mapper/          # MerchantMapper, ApiKeyMapper (MapStruct)
│   ├── repository/     # MerchantRepository, ApiKeyRepository, AppUserRepository
│   └── dto/             # request/response DTOs
├── payment/
│   ├── entity/         # OrderRecord, Payment, Refund, PaymentTransitionLog
│   ├── controller/     # OrderController, PaymentController
│   ├── service/        # OrderService, PaymentService (+ impl)
│   ├── statemachine/   # PaymentStateMachine, PaymentTransitionService
│   ├── gateway/        # PaymentGatewayRouter, PaymentAdapter + UPI/Card/NetBanking adapters
│   ├── processor/      # PaymentProcessorRouter, PaymentProcessor strategies (UPI/Card/NetBanking)
│   ├── simulator/      # BankCallbackSimulator, SimulatorConfig (configurable chaos/delay/success-rate)
│   ├── config/         # PaymentProcessorConfig, PaymentAdapterConfig
│   ├── mapper/          # OrderMapper, PaymentMapper (MapStruct)
│   ├── repository/     # OrderRepository, PaymentRepository, PaymentTransitionLogRepository
│   └── dto/             # request/response DTOs (CreateOrderRequest, PaymentInitRequest, OrderResponse, PaymentResponse)
├── vault/
│   ├── entity/         # VaultCard, CardToken
│   ├── controller/     # VaultController
│   ├── service/        # VaultService (+ impl) — card tokenization
│   ├── repository/     # VaultCardRepository, CardTokenRepository
│   ├── validation/     # ExpiryYear custom validator
│   ├── config/         # VaultEncryptionConfig
│   └── dto/             # TokenizeRequest, TokenizeResponse
└── operations/
    ├── entity/         # WebhookEvent, Settlement, SettlementPayment, DlqEvent
    └── dto/             # (scaffolded, no service/controller yet)
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
- Order service layer: create, get by id, cancel, list payments for an order (`OrderService`/`OrderServiceImpl`) — only `create` is currently wired to `OrderController`
- MapStruct mapper layer for Merchant, ApiKey, Order, and Payment DTO conversions, replacing manual field-by-field mapping
- Payment initiation and capture flow (`PaymentController`, `PaymentService`/`PaymentServiceImpl`), with a method-based routing layer (`PaymentGatewayRouter` → `PaymentAdapter` per method → `PaymentProcessorRouter` → `PaymentProcessor` strategy per method) for UPI, Card, and NetBanking
- Explicit payment state machine (`PaymentStateMachine`) with a transition service (`PaymentTransitionService`) that logs every status change to `PaymentTransitionLog`
- Configurable bank callback simulator (`BankCallbackSimulator`, `SimulatorConfig`) with per-method delay/success-rate and a chaos mode, for exercising the async payment flow without a real bank integration
- Card vault: tokenization endpoint and service (`VaultController`, `VaultService`/`VaultServiceImpl`), with dedicated encryption config and a custom expiry-year validator
- Repository layer for Merchant, ApiKey, AppUser, Order, Payment, PaymentTransitionLog, VaultCard, CardToken
- Global exception handling (`GlobalExceptionHandler`, custom exceptions including `InvalidStateTransitionException`, error response format)
- Request validation via `spring-boot-starter-validation`

## Development Log

### 2026-09-02
- Added payment initiation and capture flow: `PaymentController`, `PaymentService`/`PaymentServiceImpl`, `PaymentInitRequest` DTO
- Built the payment routing layer: `PaymentGatewayRouter`/`PaymentAdapter` (per payment method) and `PaymentProcessorRouter`/`PaymentProcessor` strategies for UPI, Card, and NetBanking, plus `PaymentAdapterConfig`/`PaymentProcessorConfig`
- Added an explicit payment state machine: `PaymentStateMachine`, `PaymentTransitionService`, `PaymentTransitionLogRepository`, and `InvalidStateTransitionException`
- Added `BankCallbackSimulator` and `SimulatorConfig` to simulate async bank callbacks with configurable delay, success rate, and chaos mode; enabled scheduling (`@EnableScheduling`) in `RazorpayApplication`
- Added the Vault domain's service layer: `VaultController`, `VaultService`/`VaultServiceImpl`, `TokenizeRequest`/`TokenizeResponse` DTOs, `VaultCardRepository`/`CardTokenRepository`, `VaultEncryptionConfig`, and a custom `@ExpiryYear` validator
- Added `CardBrand` and `ChaosMode` enums
- Added `spring-boot-starter-security` dependency and a `vault.master-key` entry in `application.yaml.example`

### 2026-08-31
- Added API key management: list keys by merchant, revoke key, rotate key (with grace period on the previous secret)
- Implemented order creation flow: `OrderController`, `OrderService`/`OrderServiceImpl`, duplicate-receipt detection, configurable order expiry
- Added `RandomizerUtil` for key/secret generation
- Added `jackson-databind` dependency
- Refactored `Money` to use Lombok annotations instead of manual boilerplate
- Introduced MapStruct (`mapstruct`, `mapstruct-processor`, `lombok-mapstruct-binding`) and replaced manual mapping in `AuthServiceImpl`/`OrderServiceImpl` with generated mappers: `MerchantMapper`, `ApiKeyMapper`, `OrderMapper`, `PaymentMapper`
- Extended `OrderService`/`OrderServiceImpl` with `getById`, `cancel`, and `listPayments`, plus `PaymentRepository.findByOrder_Id` and `PaymentResponse`/`PaymentMapper` to support listing an order's payments
- Added `operations/dto` package (scaffolding, not yet backed by a service or controller)

### 2026-08-30
- Implemented merchant signup flow: `AuthController`, `AuthService`/`AuthServiceImpl`, `MerchantSignupRequest`/`MerchantResponse` DTOs
- Implemented API key issuance flow: `ApiKeyController`, `ApiKeyService`/`ApiKeyServiceImpl`, `CreateApiKeyRequest`/`ApiKeyCreateResponse` DTOs
- Added repository layer: `MerchantRepository`, `ApiKeyRepository`, `AppUserRepository`
- Added global exception handling: `GlobalExceptionHandler`, `ResourceNotFoundException`, `DuplicateResourceException`, `ErrorResponse`
- Added `spring-boot-starter-validation` dependency for request validation

### 2026-08-27
- Modeled full entity relationship schema and JPA entities across merchant, payment, vault, and operations domains
- Initialized project with Spring Boot 4.1.1, Java 25, Spring Data JPA, PostgreSQL, Lombok
