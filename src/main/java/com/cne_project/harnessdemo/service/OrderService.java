package com.cne_project.harnessdemo.service;

import com.cne_project.harnessdemo.dto.request.CreateOrderRequest;
import com.cne_project.harnessdemo.dto.response.OrderDTO;

public interface OrderService {

    OrderDTO createOrder(CreateOrderRequest request);
}
