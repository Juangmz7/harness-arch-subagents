package com.cne_project.harnessdemo.controller;

import com.cne_project.harnessdemo.model.entity.Product;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerInventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void createProduct_shouldReturn201WithLocationHeader_whenValidRequest() throws Exception {
        String requestBody = """
                {
                  "name": "New Widget",
                  "description": "A brand new widget",
                  "price": 29.99,
                  "stock": 50
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/products/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Widget"))
                .andExpect(jsonPath("$.description").value("A brand new widget"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    void createProduct_shouldReturn409_whenDuplicateName() throws Exception {
        productRepository.save(Product.builder()
                .name("Existing Product")
                .description("Already in catalog")
                .price(new BigDecimal("9.99"))
                .stock(10)
                .build());

        String requestBody = """
                {
                  "name": "Existing Product",
                  "description": "Another description",
                  "price": 14.99,
                  "stock": 5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void updateStock_shouldReturn200WithUpdatedStock_whenProductExists() throws Exception {
        var saved = productRepository.save(Product.builder()
                .name("Stock Product")
                .description("Product for stock test")
                .price(new BigDecimal("5.00"))
                .stock(100)
                .build());

        String requestBody = """
                {
                  "quantity": 50
                }
                """;

        mockMvc.perform(put("/api/products/{id}/stock", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    void createProduct_shouldReturn400_whenInvalidFields() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "description": "Valid description",
                  "price": 9.99,
                  "stock": 10
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("name")));
    }

    @Test
    void updateStock_shouldReturn404_whenProductDoesNotExist() throws Exception {
        String requestBody = """
                {
                  "quantity": 20
                }
                """;

        mockMvc.perform(put("/api/products/{id}/stock", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
