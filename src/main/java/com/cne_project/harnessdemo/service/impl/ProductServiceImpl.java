package com.cne_project.harnessdemo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cne_project.harnessdemo.dto.response.ProductDTO;
import com.cne_project.harnessdemo.mapper.ProductMapper;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.ProductRepository;
import com.cne_project.harnessdemo.service.ProductService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        log.info("Fetching all products");
        var products = productRepository.findAll();
        var result = productMapper.toDtoList(products);
        log.info("Fetched {} products", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        log.info("Fetching product with id={}", id);
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        var result = productMapper.toDto(product);
        log.info("Fetched product with id={}", id);
        return result;
    }
}
