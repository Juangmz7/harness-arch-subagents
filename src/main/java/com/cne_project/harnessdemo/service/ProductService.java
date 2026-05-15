package com.cne_project.harnessdemo.service;

import java.util.List;

import com.cne_project.harnessdemo.dto.response.ProductDTO;

public interface ProductService {

    List<ProductDTO> findAll();

    ProductDTO findById(Long id);
}
