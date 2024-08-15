package org.silluck.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.dto.request.AddProductForm;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form) {
        return productRepository.save(Product.of(sellerId, form));
    }

}
