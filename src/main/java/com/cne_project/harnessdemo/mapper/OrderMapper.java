package com.cne_project.harnessdemo.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cne_project.harnessdemo.dto.response.OrderDTO;
import com.cne_project.harnessdemo.dto.response.OrderItemDTO;
import com.cne_project.harnessdemo.model.entity.Order;
import com.cne_project.harnessdemo.model.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toDto(Order order);

    OrderItemDTO toItemDto(OrderItem orderItem);

    List<OrderItemDTO> toItemDtoList(List<OrderItem> orderItems);
}
