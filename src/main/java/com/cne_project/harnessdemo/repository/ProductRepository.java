package com.cne_project.harnessdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cne_project.harnessdemo.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
