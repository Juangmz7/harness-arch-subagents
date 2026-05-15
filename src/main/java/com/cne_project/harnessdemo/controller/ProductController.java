package com.cne_project.harnessdemo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cne_project.harnessdemo.dto.response.ProductDTO;
import com.cne_project.harnessdemo.service.ProductService;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public ResponseEntity<List<ProductDTO>> getAllProducts() {
		log.debug("GET /api/products - retrieving all products");
		var products = productService.findAll();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
		log.debug("GET /api/products/{} - retrieving product by id", id);
		var product = productService.findById(id);
		return ResponseEntity.ok(product);
	}
}
