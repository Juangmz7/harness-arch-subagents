package com.cne_project.harnessdemo.service;

import com.cne_project.harnessdemo.model.dto.CreateOrderRequest;
import com.cne_project.harnessdemo.model.dto.OrderItemRequest;
import com.cne_project.harnessdemo.model.entity.Order;
import com.cne_project.harnessdemo.model.entity.OrderItem;
import com.cne_project.harnessdemo.model.entity.OrderStatus;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.InsufficientStockException;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.OrderRepository;
import com.cne_project.harnessdemo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldReduceStockForAllItems_whenOrderIsSuccessful() {
        // Arrange
        Product productA = buildProduct(1L, "Widget", new BigDecimal("10.00"), 50);
        Product productB = buildProduct(2L, "Gadget", new BigDecimal("5.00"), 30);

        when(productRepository.findById(1L)).thenReturn(Optional.of(productA));
        when(productRepository.findById(2L)).thenReturn(Optional.of(productB));

        Order savedOrder = Order.builder()
                .id(100L)
                .items(List.of(
                        OrderItem.builder().productId(1L).quantity(2).unitPrice(new BigDecimal("10.00")).build(),
                        OrderItem.builder().productId(2L).quantity(3).unitPrice(new BigDecimal("5.00")).build()
                ))
                .total(new BigDecimal("35.00"))
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 3)
        ));

        // Act
        var result = orderService.createOrder(request);

        // Assert — returned DTO
        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.total()).isEqualByComparingTo("35.00");
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING);

        // Assert — stock was modified
        assertThat(productA.getStock()).isEqualTo(48);   // 50 - 2
        assertThat(productB.getStock()).isEqualTo(27);   // 30 - 3

        // Assert — both products were saved
        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository).save(any(Order.class));
    }

    // -------------------------------------------------------------------------
    // Insufficient stock — no stock must be modified
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldThrowInsufficientStockException_andNotModifyStock_whenStockIsInsufficient() {
        // Arrange
        Product productA = buildProduct(1L, "Widget", new BigDecimal("10.00"), 1);
        Product productB = buildProduct(2L, "Gadget", new BigDecimal("5.00"), 100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(productA));
        when(productRepository.findById(2L)).thenReturn(Optional.of(productB));

        var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(1L, 5),   // requests 5, only 1 available
                new OrderItemRequest(2L, 1)
        ));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("1");

        // Stock must remain unchanged for all products
        assertThat(productA.getStock()).isEqualTo(1);
        assertThat(productB.getStock()).isEqualTo(100);

        // No saves to product or order repositories
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // -------------------------------------------------------------------------
    // Product not found — no stock must be modified
    // -------------------------------------------------------------------------

    @Test
    void createOrder_shouldThrowResourceNotFoundException_andNotModifyStock_whenProductDoesNotExist() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(999L, 1)
        ));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        // No writes at all
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Product buildProduct(Long id, String name, BigDecimal price, int stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .description(name + " description")
                .price(price)
                .stock(stock)
                .build();
    }
}
