package com.cne_project.harnessdemo.service;

import com.cne_project.harnessdemo.model.dto.CreateProductRequest;
import com.cne_project.harnessdemo.model.dto.ProductDTO;
import com.cne_project.harnessdemo.model.dto.UpdateStockRequest;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.DuplicateProductNameException;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        var products = productRepository.findAll();
        log.debug("Fetched {} products from catalog", products.size());
        return products.stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return toDTO(product);
    }

    @Transactional
    public ProductDTO create(CreateProductRequest request) {
        assertNoDuplicateName(request.getName());
        var product = buildProduct(request);
        var saved = productRepository.save(product);
        log.info("Created product: id={}, name={}", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    @Transactional
    public ProductDTO updateStock(Long id, UpdateStockRequest request) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setStock(request.getQuantity());
        var saved = productRepository.save(product);
        log.info("Updated stock: id={}, newStock={}", saved.getId(), saved.getStock());
        return toDTO(saved);
    }

    private void assertNoDuplicateName(String name) {
        if (productRepository.existsByName(name)) {
            throw new DuplicateProductNameException("Product with name '" + name + "' already exists");
        }
    }

    private Product buildProduct(CreateProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }

    private ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
