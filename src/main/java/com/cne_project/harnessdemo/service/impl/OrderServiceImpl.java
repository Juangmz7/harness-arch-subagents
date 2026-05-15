package com.cne_project.harnessdemo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.cne_project.harnessdemo.dto.request.CreateOrderRequest;
import com.cne_project.harnessdemo.dto.request.OrderItemRequest;
import com.cne_project.harnessdemo.dto.response.OrderDTO;
import com.cne_project.harnessdemo.mapper.OrderMapper;
import com.cne_project.harnessdemo.model.entity.Order;
import com.cne_project.harnessdemo.model.entity.OrderItem;
import com.cne_project.harnessdemo.model.entity.OrderStatus;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.InsufficientStockException;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.OrderRepository;
import com.cne_project.harnessdemo.repository.ProductRepository;
import com.cne_project.harnessdemo.service.OrderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        log.info("Creating order with {} items", request.items().size());

        var productMap = fetchProducts(request.items());
        validateStock(request.items(), productMap);
        var updatedProducts = deductStock(request.items(), productMap);
        productRepository.saveAll(updatedProducts);

        var order = buildOrder(request.items(), productMap);
        var saved = orderRepository.save(order);

        log.info("Created order id={}, total={}", saved.getId(), saved.getTotal());
        return orderMapper.toDto(saved);
    }

    private Map<Long, Product> fetchProducts(List<OrderItemRequest> items) {
        var productMap = new HashMap<Long, Product>();
        for (var item : items) {
            var product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.productId()));
            productMap.put(item.productId(), product);
        }
        return productMap;
    }

    private void validateStock(List<OrderItemRequest> items, Map<Long, Product> productMap) {
        for (var item : items) {
            var product = productMap.get(item.productId());
            if (product.getStock() < item.quantity()) {
                throw new InsufficientStockException(item.productId(), item.quantity(), product.getStock());
            }
        }
    }

    private List<Product> deductStock(List<OrderItemRequest> items, Map<Long, Product> productMap) {
        var updated = new ArrayList<Product>();
        for (var item : items) {
            var product = productMap.get(item.productId());
            product.setStock(product.getStock() - item.quantity());
            updated.add(product);
        }
        return updated;
    }

    private Order buildOrder(List<OrderItemRequest> items, Map<Long, Product> productMap) {
        var order = Order.builder()
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.ZERO)
                .build();

        var orderItems = buildOrderItems(items, productMap, order);
        var total = computeTotal(items, productMap);

        order.setItems(orderItems);
        order.setTotal(total);
        return order;
    }

    private List<OrderItem> buildOrderItems(List<OrderItemRequest> items, Map<Long, Product> productMap, Order order) {
        var orderItems = new ArrayList<OrderItem>();
        for (var item : items) {
            var product = productMap.get(item.productId());
            var orderItem = OrderItem.builder()
                    .order(order)
                    .productId(item.productId())
                    .quantity(item.quantity())
                    .unitPrice(product.getPrice())
                    .build();
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal computeTotal(List<OrderItemRequest> items, Map<Long, Product> productMap) {
        return items.stream()
                .map(item -> productMap.get(item.productId())
                        .getPrice()
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
