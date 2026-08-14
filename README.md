# CypherCart — Backend

Backend service for **CypherCart**, an e-commerce catalog and product-recommendation application. Built with **Spring Boot** and backed by **CognoDB**, a managed graph database that speaks openCypher over the Bolt protocol.

**Frontend repo:** https://github.com/AdityaTalwatkar/cyphercart-frontend
**Live demo:** https://cyphercart-frontend.vercel.app/

---

## Table of Contents

- [Use Case](#use-case)
- [Why a Graph Database?](#why-a-graph-database)
- [Data Model](#data-model)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Setup](#setup)
  - [1. Create a CognoDB Instance](#1-create-a-cognodb-instance)
  - [2. Configure Environment Variables](#2-configure-environment-variables)
  - [3. Seed the Database](#3-seed-the-database)
  - [4. Run the Application](#4-run-the-application)
- [API Overview](#api-overview)
- [Key Cypher Queries](#key-cypher-queries)
- [Error Handling](#error-handling)

---

## Use Case

CypherCart is a small e-commerce storefront where customers browse products across categories, and the platform surfaces recommendations based on shared categories, co-purchase patterns, and browsing behavior. An admin dashboard lets a store owner manage inventory.

The interesting part of this problem isn't the catalog itself — it's the **relationships**: which products are frequently bought together, which categories a customer gravitates toward, and how a viewing session connects to a purchase decision. These are graph-shaped questions.

## Why a Graph Database?

In a relational schema, answering "what should I recommend to this customer?" typically means several `JOIN`s across `users`, `orders`, `order_items`, `products`, and `categories` — and it gets worse as you add more hops (e.g., "customers who bought what I bought also bought…"). Each additional hop is another join, and performance degrades as the tables grow.

In a graph model, that same question is a **traversal**, not a join chain. CognoDB stores relationships as first-class citizens, so a query like "find products purchased by other customers who bought the same category I'm currently viewing" is a direct 2–3 hop pattern match rather than a cascade of joins — and it stays fast as the dataset grows, because traversal cost depends on the number of relationships actually walked, not the size of the tables involved.

This is also a natural fit for **schema flexibility**: adding a new relationship type (e.g., `WISHLISTED`, `REVIEWED`) doesn't require a migration or a new join table — it's just a new edge.

## Data Model

**Nodes**

| Label | Key Properties |
|---|---|
| `User` | `id`, `name`, `email` |
| `Product` | `id`, `name`, `price`, `stock` |
| `Category` | `id`, `name` |
| `Order` | `id`, `createdAt` |

**Relationships**

| Relationship | From → To | Notes |
|---|---|---|
| `BELONGS_TO` | `(Product) → (Category)` | Each product belongs to one or more categories |
| `VIEWED` | `(User) → (Product)` | Browsing activity, timestamped |
| `PURCHASED` | `(User) → (Product)` | Via an `Order`, timestamped |
| `PLACED` | `(User) → (Order)` | Links a user to their order |
| `CONTAINS` | `(Order) → (Product)` | Line items in an order |

```
                (Category)
                    ▲
               BELONGS_TO
                    │
(User) ─VIEWED──▶ (Product) ◀──CONTAINS── (Order) ◀──PLACED── (User)
   │                  ▲
   └────PURCHASED─────┘
```

> Replace the diagram above with an exported image (e.g. from Neo4j Browser / Arrows.app) if you'd like a visual rendering instead of ASCII.

## Tech Stack

- **Java 21**, **Spring Boot**
- **Spring Data Neo4j** (official Neo4j driver, Bolt protocol — compatible with CognoDB)
- **CognoDB Cloud** — managed graph database instance
- **Maven** (via included `mvnw` wrapper)
- **Docker** (optional containerized run)

## Project Structure

```
cyphercart-backend/
├── src/
│   ├── main/
│   │   ├── java/com/cyphercart/backend/
│   │   │   ├── controller/    # REST endpoints
│   │   │   ├── model/         # Node & relationship entities
│   │   │   ├── repository/    # Spring Data Neo4j repositories
│   │   │   ├── service/       # Business logic
│   │   │   └── config/        # DB & CORS configuration
│   │   └── resources/
│   │       └── application.properties
├── scripts/                   # Data seeding scripts
├── Dockerfile
├── mvnw / mvnw.cmd
└── pom.xml
```

## Setup

### 1. Create a CognoDB Instance

1. Sign up at [console.cognodb.com/signup](https://console.cognodb.com/signup) (free tier, no credit card required).
2. Create a free **c0** instance and choose a region. Provisioning takes under a minute.
3. Copy your connection URI (`bolt+s://<instance-id>.databases.cognodb.cloud`) and the generated password for the `cognodb` user — the password is shown only once.

### 2. Configure Environment Variables

Connection details are read from environment variables and are **never committed** to this repository.

Create a `.env` file locally (already covered by `.gitignore`) or set these as environment variables in your shell / hosting provider:

```bash
COGNODB_URI=bolt+s://<your-instance-id>.databases.cognodb.cloud
COGNODB_USERNAME=cognodb
COGNODB_PASSWORD=<your-generated-password>
```

`application.properties` references these via placeholders:

```properties
spring.neo4j.uri=${COGNODB_URI}
spring.neo4j.authentication.username=${COGNODB_USERNAME}
spring.neo4j.authentication.password=${COGNODB_PASSWORD}
```

### 3. Seed the Database

With your environment variables set, run the seed script to load sample products, categories, users, and orders:

```bash
./scripts/seed.sh
```

*(Or, if the seed script is Java-based, run it via `./mvnw exec:java -Dexec.mainClass="com.cyphercart.backend.SeedRunner"` — update this line to match whatever your actual seed entrypoint is.)*

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The API will start on `http://localhost:8080` by default.

**Docker (optional):**

```bash
docker build -t cyphercart-backend .
docker run --env-file .env -p 8080:8080 cyphercart-backend
```

## API Overview

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products` | List all products |
| `GET` | `/api/products/{id}/recommendations` | Multi-hop recommendation query |
| `GET` | `/api/categories` | List categories |
| `POST` | `/api/admin/products` | Add a new product (admin) |
| `DELETE` | `/api/admin/products/{id}` | Remove a product (admin) |

*(Update this table to match your actual controller endpoints.)*

## Key Cypher Queries

All queries are executed as **parameterised** Cypher through the official Neo4j driver — no string concatenation.

**1. Multi-hop recommendation (2+ hops)** — products bought by other users who purchased the same product as the current user:

```cypher
MATCH (u:User {id: $userId})-[:PURCHASED]->(p:Product)<-[:PURCHASED]-(other:User)-[:PURCHASED]->(rec:Product)
WHERE rec <> p
RETURN rec.name, rec.price, count(*) AS strength
ORDER BY strength DESC
LIMIT 5
```

**2. Category-based discovery** — products in categories the user has recently viewed but not yet purchased:

```cypher
MATCH (u:User {id: $userId})-[:VIEWED]->(:Product)-[:BELONGS_TO]->(c:Category)<-[:BELONGS_TO]-(candidate:Product)
WHERE NOT (u)-[:PURCHASED]->(candidate)
RETURN DISTINCT candidate.name, c.name AS category
LIMIT 10
```

**3. A query relational SQL would find awkward** — variable-length "customers who bought what you bought, and what *they* bought" traversal:

```cypher
MATCH (u:User {id: $userId})-[:PURCHASED]->(:Product)<-[:PURCHASED]-(:User)-[:PURCHASED*1..2]->(rec:Product)
WHERE NOT (u)-[:PURCHASED]->(rec)
RETURN DISTINCT rec.name
LIMIT 10
```

This kind of variable-length path traversal requires recursive CTEs or repeated self-joins in SQL and becomes unwieldy quickly; in Cypher it's a single, readable pattern.

*(Swap in your actual query names/logic if these differ from what's implemented — keep the structure, update the specifics.)*

## Error Handling

- If the CognoDB instance is unreachable at startup, the application logs a clear connection error and the health endpoint reports a degraded status rather than crashing silently.
- API responses return a structured JSON error body (`{ "error": "..." }`) with an appropriate HTTP status code on database failures, rather than leaking stack traces to the client.

---

