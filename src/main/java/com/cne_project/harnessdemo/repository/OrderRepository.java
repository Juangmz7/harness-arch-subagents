package com.cne_project.harnessdemo.repository;

import com.cne_project.harnessdemo.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
