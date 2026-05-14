package com.cne_project.harnessdemo.service;

import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_shouldReturnMappedDTOs() {
        // Arrange
        var product1 = Product.builder()
                .id(1L)
                .name("Widget")
                .description("A useful widget")
                .price(new BigDecimal("9.99"))
                .stock(100)
                .build();
        var product2 = Product.builder()
                .id(2L)
                .name("Gadget")
                .description("A cool gadget")
                .price(new BigDecimal("19.99"))
                .stock(50)
                .build();
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        // Act
        var result = productService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Widget");
        assertThat(result.get(0).price()).isEqualByComparingTo("9.99");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).name()).isEqualTo("Gadget");
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoneExist() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        var result = productService.findAll();

        // Assert
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void findById_shouldReturnCorrectDTO_whenProductExists() {
        // Arrange
        var product = Product.builder()
                .id(1L)
                .name("Widget")
                .description("A useful widget")
                .price(new BigDecimal("9.99"))
                .stock(100)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        var result = productService.findById(1L);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Widget");
        assertThat(result.description()).isEqualTo("A useful widget");
        assertThat(result.price()).isEqualByComparingTo("9.99");
        assertThat(result.stock()).isEqualTo(100);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
