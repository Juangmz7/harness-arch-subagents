package com.cne_project.harnessdemo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cne_project.harnessdemo.dto.response.ProductDTO;
import com.cne_project.harnessdemo.mapper.ProductMapper;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.ProductRepository;
import com.cne_project.harnessdemo.service.impl.ProductServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findAll_returnsMappedDtos() {
        var product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("A powerful laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .build();
        var productDTO = new ProductDTO(1L, "Laptop", "A powerful laptop", new BigDecimal("999.99"), 10);
        var products = List.of(product);
        var dtos = List.of(productDTO);

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDtoList(products)).thenReturn(dtos);

        var result = productService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Laptop");
    }

    @Test
    void findById_returnsCorrectDto() {
        var product = Product.builder()
                .id(2L)
                .name("Phone")
                .description("A smartphone")
                .price(new BigDecimal("499.99"))
                .stock(5)
                .build();
        var productDTO = new ProductDTO(2L, "Phone", "A smartphone", new BigDecimal("499.99"), 5);

        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        var result = productService.findById(2L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Phone");
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("499.99"));
    }

    @Test
    void findById_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 99");
    }
}
