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
│   ├── entity/        # Shared value types (Money)
│   └── enums/         # Shared enums (statuses, actors, events, roles)
├── merchant/
│   └── entity/        # Merchant, ApiKey, AppUser, Customer, MerchantWebhookConfig
├── payment/
│   └── entity/        # OrderRecord, Payment, Refund, PaymentTransitionLog
├── vault/
│   └── entity/        # VaultCard, CardToken
└── operations/
    └── entity/        # WebhookEvent, Settlement, SettlementPayment, DlqEvent
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

**In Progress / Not Started**
- Repository, service, and controller layers
- Authentication (API key + JWT) and request validation
- Idempotency handling for orders and payments
- Kafka outbox publishing and consumers
- Vault encryption and card tokenization logic
- Webhook delivery with retry and dead-letter handling
- Settlement batch processing
- Test coverage, Docker Compose, CI pipeline

## Development Log

### 2026-08-27
- Modeled full entity relationship schema and JPA entities across merchant, payment, vault, and operations domains
- Initialized project with Spring Boot 4.1.1, Java 25, Spring Data JPA, PostgreSQL, Lombok
