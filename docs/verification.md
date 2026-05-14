# Verification — How to Prove the Work Functions

> Golden rule: **the agent doesn't say "it works", it proves it**.
> Every feature ends with executable evidence, not assertions.

## Verification Levels

### Level 1 — Unit Tests (mandatory for Services)

Every public method in the `Service` layer has at least one test in `src/test/java/...` that:

1. Covers the happy path.
2. Covers at least one error path (e.g., throwing a `DomainException`) if the function can fail.
3. Uses Mockito to mock the `Repository` layer.

Command:
```bash
mvn test
```

### Level 2 — API Integration Test (mandatory for Controllers)

Because the project uses an in-memory H2 database, we do not need complex external integration setups. Features that add new endpoints are verified by bringing up the Spring Context with H2 and calling it via `MockMvc`:

```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateProduct() throws Exception {
        String jsonPayload = """
            { "name": "Test Product", "price": 100.0, "stockQuantity": 10 }
            """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
```

### Level 3 — Manual Smoke Test (optional but recommended)

Before closing the session, run the application locally (it will use the H2 database automatically) and execute a quick `curl` to verify the endpoint is reachable:

```bash
# Terminal 1
mvn spring-boot:run

# Terminal 2
curl -X GET http://localhost:8081/api/products
```

## Anti-patterns (do not do)

- ❌ "I added the endpoint, it should work." → missing executable MockMvc test.
- ❌ Test that only verifies the method doesn't throw an exception. → it must check the concrete HTTP status or JSON response.
- ❌ `mock` the database for API Integration tests. → use the real in-memory H2 database.
- ❌ Mark the feature as `done` without passing `mvn clean verify`.

## Final Verification Before Closing

```bash
mvnw clean verify           # must finish with BUILD SUCCESS
```

If the build fails or tests are red, do **not** mark anything as `done`. Log the blocker and fix the compilation/test errors before proceeding.