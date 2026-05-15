package com.cne_project.harnessdemo.service;

import com.cne_project.harnessdemo.model.dto.CreateOrderRequest;
import com.cne_project.harnessdemo.model.dto.OrderDTO;
import com.cne_project.harnessdemo.model.dto.OrderItemDTO;
import com.cne_project.harnessdemo.model.dto.OrderItemRequest;
import com.cne_project.harnessdemo.model.entity.Order;
import com.cne_project.harnessdemo.model.entity.OrderItem;
import com.cne_project.harnessdemo.model.entity.Product;
import com.cne_project.harnessdemo.model.exception.InsufficientStockException;
import com.cne_project.harnessdemo.model.exception.ResourceNotFoundException;
import com.cne_project.harnessdemo.repository.OrderRepository;
import com.cne_project.harnessdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for order creation. Stock validation and order persistence
 * occur within a single transaction; any domain exception triggers full rollback.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    /**
     * Creates an order atomically: validates stock, deducts it, and persists
     * the order — all within a single transaction.
     *
     * @param request validated request containing one or more items
     * @return DTO representing the persisted order
     * @throws ResourceNotFoundException   if any referenced product does not exist
     * @throws InsufficientStockException  if any product has insufficient stock
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        List<Product> products = resolveProducts(request.items());
        validateStock(request.items(), products);
        List<OrderItem> orderItems = deductStockAndBuildItems(request.items(), products);
        Order savedOrder = persistOrder(orderItems);
        log.debug("Order {} created with {} item(s), total {}",
                savedOrder.getId(), orderItems.size(), savedOrder.getTotal());
        return toDTO(savedOrder);
    }

    private List<Product> resolveProducts(List<OrderItemRequest> items) {
        List<Product> products = new ArrayList<>();
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", item.productId()));
            products.add(product);
        }
        return products;
    }

    private void validateStock(List<OrderItemRequest> items, List<Product> products) {
        for (int i = 0; i < items.size(); i++) {
            OrderItemRequest item = items.get(i);
            Product product = products.get(i);
            if (product.getStock() < item.quantity()) {
                throw new InsufficientStockException(product.getId(), item.quantity(), product.getStock());
            }
        }
    }

    private List<OrderItem> deductStockAndBuildItems(List<OrderItemRequest> items, List<Product> products) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            OrderItemRequest item = items.get(i);
            Product product = products.get(i);
            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);
            orderItems.add(OrderItem.builder()
                    .productId(product.getId())
                    .quantity(item.quantity())
                    .unitPrice(product.getPrice())
                    .build());
        }
        return orderItems;
    }

    private Order persistOrder(List<OrderItem> orderItems) {
        BigDecimal total = computeTotal(orderItems);
        Order order = Order.builder()
                .items(orderItems)
                .total(total)
                .build();
        return orderRepository.save(order);
    }

    private BigDecimal computeTotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDTO toDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .toList();
        return new OrderDTO(order.getId(), itemDTOs, order.getTotal(), order.getCreatedAt(), order.getStatus());
    }
}
