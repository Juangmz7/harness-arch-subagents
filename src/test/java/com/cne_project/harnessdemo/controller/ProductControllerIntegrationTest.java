package com.cne_project.harnessdemo.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cne_project.harnessdemo.dto.response.ProductDTO;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		productRepository.deleteAll();
	}

	@Test
	void getAllProducts_returns200WithJsonArray() throws Exception {
		var product = Product.builder()
				.name("Laptop")
				.description("A powerful laptop")
				.price(new BigDecimal("999.99"))
				.stock(10)
				.build();
		productRepository.save(product);

		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is("Laptop")))
				.andExpect(jsonPath("$[0].description", is("A powerful laptop")))
				.andExpect(jsonPath("$[0].stock", is(10)));
	}

	@Test
	void getAllProducts_returns200WithEmptyArray_whenNoProducts() throws Exception {
		mockMvc.perform(get("/api/products"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	void getProductById_returns200WithCorrectData_whenExists() throws Exception {
		var product = Product.builder()
				.name("Phone")
				.description("A smartphone")
				.price(new BigDecimal("499.99"))
				.stock(5)
				.build();
		var savedProduct = productRepository.save(product);

		mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.id", is(savedProduct.getId().intValue())))
				.andExpect(jsonPath("$.name", is("Phone")))
				.andExpect(jsonPath("$.description", is("A smartphone")))
				.andExpect(jsonPath("$.price", is(499.99)))
				.andExpect(jsonPath("$.stock", is(5)));
	}

	@Test
	void getProductById_returns404WithErrorBody_whenNotExists() throws Exception {
		mockMvc.perform(get("/api/products/9999"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.status", is(404)))
				.andExpect(jsonPath("$.error", is("Not Found")))
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.path", is("/api/products/9999")));
	}
}
