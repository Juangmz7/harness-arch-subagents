package com.cne_project.harnessdemo.controller;

import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.repository.OrderRepository;
import com.cne_project.harnessdemo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // 201 — successful order
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldReturn201WithOrderDTO_whenRequestIsValid() throws Exception {
        Product product = productRepository.save(Product.builder()
                .name("Widget")
                .description("A useful widget")
                .price(new BigDecimal("10.00"))
                .stock(50)
                .build());

        String requestBody = """
                {
                  "items": [
                    { "productId": %d, "quantity": 3 }
                  ]
                }
                """.formatted(product.getId());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.total").value(30.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.items[0].unitPrice").value(10.00));

        // Verify stock was actually deducted in the database
        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(47);
    }

    // -------------------------------------------------------------------------
    // 404 — product not found
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldReturn404_whenProductDoesNotExist() throws Exception {
        String requestBody = """
                {
                  "items": [
                    { "productId": 9999, "quantity": 1 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/orders"));
    }

    // -------------------------------------------------------------------------
    // 422 — insufficient stock
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldReturn422_whenStockIsInsufficient() throws Exception {
        Product product = productRepository.save(Product.builder()
                .name("LowStock")
                .description("Only 2 left")
                .price(new BigDecimal("5.00"))
                .stock(2)
                .build());

        String requestBody = """
                {
                  "items": [
                    { "productId": %d, "quantity": 10 }
                  ]
                }
                """.formatted(product.getId());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Content"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/orders"));

        // Verify stock was NOT deducted
        Product unchanged = productRepository.findById(product.getId()).orElseThrow();
        assertThat(unchanged.getStock()).isEqualTo(2);
    }

    // -------------------------------------------------------------------------
    // 400 — invalid request: empty items list
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldReturn400_whenItemsListIsEmpty() throws Exception {
        String requestBody = """
                {
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/orders"));
    }

    // -------------------------------------------------------------------------
    // 400 — invalid request: quantity is zero
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldReturn400_whenQuantityIsZero() throws Exception {
        Product product = productRepository.save(Product.builder()
                .name("Widget")
                .description("A widget")
                .price(new BigDecimal("10.00"))
                .stock(10)
                .build());

        String requestBody = """
                {
                  "items": [
                    { "productId": %d, "quantity": 0 }
                  ]
                }
                """.formatted(product.getId());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/orders"));
    }
}
