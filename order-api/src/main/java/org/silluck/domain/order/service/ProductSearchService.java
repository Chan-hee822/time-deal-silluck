package org.silluck.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.exception.CustomException;
import org.silluck.domain.order.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.silluck.domain.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

/**
 * 예를 들면 g 마켓에서 아이템 코드를 입력 했들 때 볼 수 있는 페이지
 */
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    public List<Product> searchByName(String name) {
        return productRepository.searchByName(name);
    }

    public Product getByProductId(Long productId) {
        return productRepository.findWithProductItemsById(productId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));
    }

    public List<Product> getListByProductIds(List<Long> productIds) {
        return productRepository.findAllByIdIn(productIds);
    }
}
