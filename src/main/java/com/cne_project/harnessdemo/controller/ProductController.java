package com.cne_project.harnessdemo.controller;

import com.cne_project.harnessdemo.model.dto.CreateProductRequest;
import com.cne_project.harnessdemo.model.dto.ProductDTO;
import com.cne_project.harnessdemo.model.dto.UpdateStockRequest;
import com.cne_project.harnessdemo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid CreateProductRequest request) {
        var dto = productService.create(request);
        URI location = buildLocationUri(dto.id());
        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStockRequest request) {
        return ResponseEntity.ok(productService.updateStock(id, request));
    }

    private URI buildLocationUri(Long id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
