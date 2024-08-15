package org.silluck.domain.order.repository;

import org.silluck.domain.order.domain.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> searchByName(String name);
}