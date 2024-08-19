package org.silluck.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;
import org.silluck.domain.order.domain.dto.request.UpdateProductItemForm;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.domain.entity.ProductItem;
import org.silluck.domain.order.exception.CustomException;
import org.silluck.domain.order.repository.ProductItemRepository;
import org.silluck.domain.order.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.silluck.domain.order.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form) {
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        if (product.getProductItems().stream().anyMatch(
                productItem -> productItem.getName().equals(form.getName()))) {
            throw new CustomException(SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);

        return product;
    }

    @Transactional
    public ProductItem updateProductItem(Long sellerId, UpdateProductItemForm form) {
        ProductItem productItem = productItemRepository.findById(form.getId())
                .filter(pi -> pi.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));
        productItem.setName(form.getName());
        productItem.setCount(form.getCount());
        productItem.setPrice(form.getPrice());
        return productItem;
    }

    @Transactional
    public void deleteProductItem(Long sellerId, Long productItemId) {
        ProductItem productItem = productItemRepository.findById(productItemId)
                .filter(pr -> pr.getSellerId().equals(sellerId))
                .orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));
        productItemRepository.delete(productItem);
    }

    // 1. get & save
    // (2. 바로 update하는 방식도 고려해볼 수 있음)
    public ProductItem getProductItem(Long id) {
        return productItemRepository.getReferenceById(id);
    }
}
