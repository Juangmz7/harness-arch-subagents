# Architecture — What "doing good work" means

> This document defines the quality standard. Reviewer agents evaluate
> code against this file. If it's not here, it's not a requirement.

## Principles

1. **Clear layers.** The project has strictly three main layers:
   - `Controller` — Handles HTTP requests, input validation, and API responses.
   - `Service` — Contains all business domain logic (e.g., calculating totals, verifying stock).
   - `Repository` — Persistence layer using Spring Data JPA interfaces.
   - `Model` — Contains all domain entities and custom exceptions.
   - `Util` — Contains all helper utilities not related app logic.
     Do not introduce additional structural layers without a concrete reason documented in the project specifications.

2. **Managed dependencies.** Rely on the standard Spring Boot ecosystem (Spring Web, Spring Data JPA), Lombok for boilerplate reduction, and MapStruct for mapping. If a feature requires a new external library, it must be discussed first (status `blocked`).

3. **Explicit errors.** Business failures (e.g., product ID doesn't exist, insufficient stock) throw specific custom exceptions (like `ResourceNotFoundException` or `InsufficientStockException`). They do not return `null` or generic internal errors.

4. **Data isolation.** Database Entities (`Product`, `Order`) are never exposed directly to the outside world. Controllers strictly accept and return Data Transfer Objects (DTOs).

5. **Transactional integrity.** Any operation that modifies data, especially across multiple records (like creating an order and deducting product stock), must be annotated with `@Transactional` at the Service level to guarantee atomicity. Also (readonly = true) to improve queries.

## Data flow

```text
client  ─→  Controller (@RestController)
              │
              ├─ parses request, validates DTOs
              │
              └─→  Service (@Service)
                     │
                     ├─ executes business logic, maps DTO ↔ Entity
                     │
                     └─→  Repository (Spring Data JPA)
                            │
                            └─→  Database (H2)
                         
```

## Package Structure

```
com.ecommerce.store
├── config           
│   └── exception    # @ControllerAdvice
├── controller       # @RestController classes
├── service          # interfaces + @Service implementations
├── repository       # Spring Data JPA interfaces
├── model
│   ├── entity       # @Entity classes
│   └── exception    # DomainException subclasses
├── mapper           # MapStruct interfaces
├── dto
│   ├── request      # inbound records (CreateXRequest, UpdateXRequest)
│   └── response     # outbound records (XDto, XSummaryDto)
└── util             # stateless helpers, no Spring beans
```

## Mapping Responsibility

Mapping between DTOs and entities is done exclusively in the **Service layer**
via injected MapStruct mappers. Controllers never call mappers directly.
Controllers receive DTOs, pass them to the service, and return whatever the service returns.

## Aggregate Rule

One service per aggregate root. Child entities (e.g., `OrderItem`) are managed
through their root's service (`OrderService`), never via their own service class.
Repositories for child entities are allowed only if querying them independently
is a justified performance need.

## What NOT to do

- Do not put business logic inside the Controller. Controllers exist only to route HTTP traffic and delegate to Services.
- Do not use field injection (@Autowired on variables). Use constructor injection, ideally via Lombok's @RequiredArgsConstructor.
- Do not catch exceptions to return generic HTTP 500s. Let exceptions bubble up to a global @ControllerAdvice to ensure standardized, elegant JSON error responses.
- Do not write manual SQL queries for basic CRUD operations. Rely on Spring Data JPA method naming conventions unless performance dictates otherwise.