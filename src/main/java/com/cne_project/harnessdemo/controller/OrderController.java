package com.cne_project.harnessdemo.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.cne_project.harnessdemo.dto.request.CreateOrderRequest;
import com.cne_project.harnessdemo.dto.response.OrderDTO;
import com.cne_project.harnessdemo.service.OrderService;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
		log.debug("POST /api/orders - creating order with {} items", request.items().size());
		var order = orderService.createOrder(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}
}
