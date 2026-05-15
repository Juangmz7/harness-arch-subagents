package com.cne_project.harnessdemo.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.cne_project.harnessdemo.dto.response.ProductDTO;
import com.cne_project.harnessdemo.model.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDto(Product product);

    List<ProductDTO> toDtoList(List<Product> products);
}
