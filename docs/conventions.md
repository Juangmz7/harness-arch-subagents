# Code Conventions

> Extreme homogeneity. AI predicts better when the repository looks
> like itself everywhere.

## Java & Spring Boot Style

- **Version:** Java 21+ (utilizing modern features like `record` for DTOs and `var` where the type is explicitly clear).
- **Formatting:** Standard Java style (e.g., Google Java Format). Maximum 120 characters per line.
- **Imports:** `java.*`/`jakarta.*` first, followed by external dependencies (Spring, Lombok), and finally local project imports. Wildcard imports (`*`) are strictly prohibited.
- **Strings:** Use `String.format()` for text formatting or SLF4J's `{}` syntax for logging. Avoid excessive string concatenation with `+`.

## Naming

| Type                 | Convention   | Example                       |
|----------------------|--------------|-------------------------------|
| Packages             | `lowercase`  | `com.ecommerce.store.service` |
| Classes / Interfaces | `PascalCase` | `ProductService`, `Order`     |
| Methods / Variables  | `camelCase`  | `calculateTotal`, `orderId`   |
| Constants            | `UPPER_SNAKE`| `MAX_STOCK_LIMIT`             |
| API Endpoints (URI)  | `kebab-case` | `/api/products/{id}`          |

## Code style
1. Apply **SOLID principles** and **compose method** for good code quality.
2. Be **pragmatic** regarding to code abstractions and complexity
3. Apply **STRICTLY** defensive programming (do not trust data from any caller).
   Make **validations for any external** given values (Integrity and edges cases).
4. Example of good code:

```java
public void updatePost(UUID postId, UpdatePostRequest request) throws AccessDeniedException {
    var post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

    assertOwnership(post);
    assertUpdatableStatus(post);

    applyContentChange(post, request.getBody());

    var newlyAddedUserIds = applyTagDiff(post, parseRequestedTagUserIds(request));

    if (!newlyAddedUserIds.isEmpty()) {
        post.updateStatus(PostStatus.PENDING);
    }

    postRepository.save(post);

    if (!newlyAddedUserIds.isEmpty()) {
        messageSender.sendValidateUserBatchCommand(
                ValidateUserBatchCommand.byUserIds(post.getId(), newlyAddedUserIds)
        );
    }

    log.info("Post {} updated by user {} (added tags: {}, status: {})",
            postId, post.getUserId(), newlyAddedUserIds.size(), post.getStatus());
}

private void assertOwnership(Post post) throws AccessDeniedException {
    if (!post.getUserId().equals(getCurrentUserId())) {
        throw new AccessDeniedException("Post " + post.getId() + " does not belong to the current user");
    }
}

private void assertUpdatableStatus(Post post) {
    var status = post.getStatus();
    if (status == PostStatus.CANCELLED) {
        throw new IllegalStateException("Cannot update a CANCELLED post");
    }
    if (status != PostStatus.PUBLISHED && status != PostStatus.PENDING) {
        throw new IllegalStateException("Post must be PUBLISHED or PENDING to be updated, was: " + status);
    }
}

private void applyContentChange(Post post, String newBody) {
    if (newBody == null || newBody.equals(post.getContent())) {
        return;
    }
    post.setBody(newBody);
}

private Set<UUID> parseRequestedTagUserIds(UpdatePostRequest request) {
    if (request.getTags() == null) {
        return Set.of();
    }
    return request.getTags().stream()
            .map(UUID::fromString)
            .collect(Collectors.toSet());
}

private Set<UUID> applyTagDiff(Post post, Set<UUID> requestedUserIds) {
    Set<UUID> currentUserIds = post.getTags().stream()
            .map(PostTag::getTaggedUserId)
            .collect(Collectors.toSet());

    currentUserIds.stream()
            .filter(userId -> !requestedUserIds.contains(userId))
            .forEach(post::removeTagByUserId);

    return requestedUserIds.stream()
            .filter(userId -> !currentUserIds.contains(userId))
            .collect(Collectors.toSet());
}
```

## File Structure

Every class in `src/main/java/...` follows a strict organizational flow:

```java
package com.ecommerce.store.service;

// 1. java.* / jakarta.* imports
import java.util.List;
import jakarta.transaction.Transactional;

// 2. Framework & Library imports (Spring, Lombok, MapStruct, etc.)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 3. Local project imports
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.model.Product;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    // 4. Injected Dependencies (private final)
    private final ProductRepository productRepository;

    // 5. Public Methods
    @Transactional
    public void updateStock(...) { ... }

    // 6. Private Helper Methods
    private void validateStock(...) { ... }
}
```

## Tests

- One test class per component: `src/test/java/.../<Class>Test.java`.
- Exclusive use of JUnit 5 and Mockito. For unit tests, use `@ExtendWith(MockitoExtension.class)` and avoid loading the full application context with `@SpringBootTest` unless it is a dedicated integration test.
- Controller slice tests use `@WebMvcTest(XController.class)` with `@MockBean` for service dependencies. Named `<Controller>IT.java`.
- Structure tests using the Arrange-Act-Assert (or Given-When-Then) pattern, separated by blank lines.
- Descriptive test method names explaining the expected behavior: `shouldThrowExceptionWhenStockIsInsufficient()`.

## Error Handling

Domain exceptions should be placed in `src/main/java/.../model/exception/`:

```java
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}

public class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
```

The API layer catches domain exceptions via a global `@ControllerAdvice`, logs the error on the server side, and returns an appropriate HTTP Status (e.g., 404, 400) with a standardized JSON payload to the client (e.g., RFC 7807 Problem Details). Never propagate Java stack traces to the user.

## Logging

Logging is mandatory for every critical operation. Use SLF4J via Lombok `@Slf4j` annotation. Never declare the logger field manually.

Rules:
- ERROR: caught exceptions and unrecoverable states
- WARN: degraded behavior, fallbacks, unexpected-but-handled conditions
- INFO: operation entry/exit for service-layer methods (not repositories)
- DEBUG: variable state, branch decisions, loop iterations when diagnosing is non-trivial

Forbidden:
- `System.out` / `System.err`
- Logging inside repository interfaces
- Log messages without context (e.g., `"error occurred"` → always include relevant IDs or state)

```java
public ProductDTO updateStock(Long id, UpdateStockRequest request) {
    var product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    log.debug("Updating stock: id={}, oldStock={}, newStock={}",
            id, product.getStock(), request.getQuantity());
    product.setStock(request.getQuantity());
    var saved = productRepository.save(product);
    log.info("Updated stock: id={}, newStock={}", saved.getId(), saved.getStock());
    return toDTO(saved);
}

private void assertNoDuplicateName(String name) {
    if (productRepository.existsByName(name)) {
        log.warn("Duplicate product name attempted: name={}", name);
        throw new DuplicateProductNameException("Product with name '" + name + "' already exists");
    }
}
```

## Mappers

Use MapStruct. Mappers live in `src/main/java/.../mapper/`.

```java
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDto(Product product);

    Product toEntity(CreateProductRequest request);

    List<ProductDTO> toDtoList(List<Product> products);
}
```

Rules:
- Always `componentModel = "spring"` — injected as a Spring bean, never instantiated manually.
- Interface only, never abstract class.
- Method naming: `toDto`, `toEntity`, `toDtoList`, `toEntityList`.
- If a field mapping is not obvious, use `@Mapping(source = "...", target = "...")`. Never write manual conversion logic inside the mapper.
- If a field must be ignored: `@Mapping(target = "fieldName", ignore = true)`.
- Mappers are pure data converters. No service calls, no repository calls, no business logic inside.
- One mapper per aggregate root (e.g., `ProductMapper`, not `ProductAndCategoryMapper`).

## DTO

Use Java records:

```java
public record CreateOrderRequest(
        @NotNull(message = "items must not be null")
        @NotEmpty(message = "items must not be empty")
        @Valid
        List<OrderItemRequest> items
) {}

public record ProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock
) {}
```

## Comments

Comments are forbidden by default. No Javadoc, no inline comments, unless one of these conditions is met:

1. A non-obvious business rule that cannot be expressed in a method/variable name
2. A documented workaround for a framework/library bug (include ticket or link)
3. A public API intended for external consumers

Class-level Javadoc is only allowed if the class name + package do not fully convey its responsibility.

When in doubt, don't comment. Rename instead.