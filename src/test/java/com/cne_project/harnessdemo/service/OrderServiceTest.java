package com.cne_project.harnessdemo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cne_project.harnessdemo.dto.request.CreateOrderRequest;
import com.cne_project.harnessdemo.dto.request.OrderItemRequest;
import com.cne_project.harnessdemo.dto.response.OrderDTO;
import com.cne_project.harnessdemo.mapper.OrderMapper;
import com.cne_project.harnessdemo.model.entity.Order;
import com.cne_project.harnessdemo.model.entity.OrderStatus;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.InsufficientStockException;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.OrderRepository;
import com.cne_project.harnessdemo.repository.ProductRepository;
import com.cne_project.harnessdemo.service.impl.OrderServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderAndDeductStockForAllItems() {
        var product1 = Product.builder().id(1L).name("P1").price(BigDecimal.TEN).stock(10).build();
        var product2 = Product.builder().id(2L).name("P2").price(BigDecimal.valueOf(5)).stock(10).build();
        var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 2)
        ));
        var savedOrder = Order.builder().id(100L).status(OrderStatus.PENDING).total(BigDecimal.valueOf(30)).build();
        var expectedDto = new OrderDTO(100L, List.of(), BigDecimal.valueOf(30), null, "PENDING");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(expectedDto);

        var result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        assertThat(product1.getStock()).isEqualTo(8);
        assertThat(product2.getStock()).isEqualTo(8);
        verify(productRepository).saveAll(anyList());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowInsufficientStockExceptionWhenStockIsInsufficient() {
        var product = Product.builder().id(1L).name("P1").price(BigDecimal.TEN).stock(1).build();
        var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(1L, 5)
        ));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class);

        verify(productRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(99L, 1)
        ));

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any(Order.class));
    }
}
