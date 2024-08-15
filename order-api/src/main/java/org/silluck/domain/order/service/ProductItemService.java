package org.silluck.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.domain.entity.ProductItem;
import org.silluck.domain.order.exception.CustomException;
import org.silluck.domain.order.repository.ProductItemRepository;
import org.silluck.domain.order.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.silluck.domain.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static org.silluck.domain.order.exception.ErrorCode.SAME_ITEM_NAME;

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
}
