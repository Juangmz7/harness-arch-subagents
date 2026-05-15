package com.cne_project.harnessdemo.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerIT {

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
	@Transactional
	void shouldCreateOrderAndReturnHttp201() throws Exception {
		var product = productRepository.save(Product.builder()
				.name("Widget")
				.description("A widget")
				.price(new BigDecimal("9.99"))
				.stock(10)
				.build());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productId":%d,"quantity":2}]}
						""".formatted(product.getId())))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.status").value("PENDING"))
				.andExpect(jsonPath("$.total").value(19.98));
	}

	@Test
	@Transactional
	void shouldReturnHttp404WhenProductDoesNotExist() throws Exception {
		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productId":99999,"quantity":1}]}
						"""))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	@Transactional
	void shouldReturnHttp422WhenStockIsInsufficient() throws Exception {
		var product = productRepository.save(Product.builder()
				.name("LowStock")
				.description("Low stock item")
				.price(new BigDecimal("5.00"))
				.stock(1)
				.build());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productId":%d,"quantity":5}]}
						""".formatted(product.getId())))
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422));
	}

	@Test
	@Transactional
	void shouldReturnHttp400WhenItemsListIsEmpty() throws Exception {
		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[]}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@Transactional
	void shouldReturnHttp400WhenQuantityIsZero() throws Exception {
		var product = productRepository.save(Product.builder()
				.name("AnyProduct")
				.description("desc")
				.price(new BigDecimal("1.00"))
				.stock(10)
				.build());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productId":%d,"quantity":0}]}
						""".formatted(product.getId())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400));
	}

	@Test
	@Transactional
	void shouldNotDeductStockWhenOrderFails() throws Exception {
		var product = productRepository.save(Product.builder()
				.name("StockCheckProduct")
				.description("desc")
				.price(new BigDecimal("2.00"))
				.stock(3)
				.build());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"items":[{"productId":%d,"quantity":10}]}
						""".formatted(product.getId())))
				.andExpect(status().isUnprocessableEntity());

		var unchanged = productRepository.findById(product.getId()).orElseThrow();
		assertThat(unchanged.getStock()).isEqualTo(3);
	}
}
