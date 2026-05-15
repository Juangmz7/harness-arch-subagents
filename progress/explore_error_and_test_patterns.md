# Explorer: Error Handling & Test Patterns

## pom.xml — CRITICAL FINDING
- Spring Boot 4.0.6, Java 21
- **`spring-boot-starter-validation` is NOT present** — must be added for @NotNull/@Min/@NotEmpty to work
- Add this dependency:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  ```
- Other deps: spring-boot-starter-webmvc, spring-boot-starter-data-jpa, h2, lombok

## Error Response Structure
`ErrorResponseDTO` record: `{ timestamp, status, error, message, path }`
- timestamp: LocalDateTime
- status: int (HTTP status code)
- error: String (HTTP reason phrase e.g. "Not Found", "Unprocessable Entity")
- message: String (exception message)
- path: String (request URI)

## GlobalExceptionHandler — existing handlers
- `ResourceNotFoundException` → 404 NOT_FOUND

## Handlers to ADD for order_checkout
- `InsufficientStockException` → 422 UNPROCESSABLE_ENTITY
- `MethodArgumentNotValidException` → 400 BAD_REQUEST (with field-level error details)

## Integration Test Pattern
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ProductRepository productRepository;

    @BeforeEach
    void setUp() { productRepository.deleteAll(); }

    @Test
    void getProductById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 9999L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").value("/api/products/9999"));
    }
}
```

## Unit Test Pattern (ProductServiceTest)
- Uses Mockito only, no Spring context loaded (@ExtendWith(MockitoExtension.class))
- @Mock ProductRepository, @InjectMocks ProductService
- Verifies DTOs, exceptions thrown, etc.

## Controller Pattern
```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() { ... }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) { ... }
}
```
