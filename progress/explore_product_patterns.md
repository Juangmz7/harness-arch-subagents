# Explorer: Existing Product Catalog Patterns

## Package
`com.cne_project.harnessdemo`

## Entity Pattern (Product.java)
```java
@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false) private String name;
    @Column(name = "description")            private String description;
    @Column(name = "price", nullable = false) private BigDecimal price;
    @Column(name = "stock", nullable = false) private int stock;
}
```
Rules: explicit `@Column` names on every field, Lombok for boilerplate.

## DTO Pattern (Java Records — no mutable POJOs)
```java
public record ProductDTO(Long id, String name, String description, BigDecimal price, int stock) {}
```
Manual mapping in service (no MapStruct).

## Repository Pattern
```java
public interface ProductRepository extends JpaRepository<Product, Long> {}
```
No custom queries needed for read-only catalog.

## Service Pattern
```java
@Service @RequiredArgsConstructor @Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() { ... }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }
    private ProductDTO toDTO(Product p) { return new ProductDTO(...); }
}
```

## Exception Hierarchy
```
DomainException extends RuntimeException
  └── ResourceNotFoundException extends DomainException
         constructor: ResourceNotFoundException(String resourceName, Long id)
         message: "%s not found with id: %d"
```
All exceptions are unchecked (extend RuntimeException via DomainException).

## ErrorResponseDTO (record)
```java
public record ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path) {}
```

## GlobalExceptionHandler
```java
@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        var error = new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```
