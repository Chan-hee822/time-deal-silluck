package org.silluck.domain.order.application;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.dto.request.AddProductInWishlistForm;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.domain.entity.ProductItem;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.silluck.domain.order.exception.CustomException;
import org.silluck.domain.order.service.ProductSearchService;
import org.silluck.domain.order.service.WishlistService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static org.silluck.domain.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static org.silluck.domain.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class WishlistApplication {

    private final ProductSearchService productSearchService;
    private final WishlistService wishlistService;

    public Wishlist addProductInWishlist(
            Long customerId, AddProductInWishlistForm form) {

        Product product = productSearchService.getByProductId(form.getId());

        if (product == null) {
            throw new CustomException(NOT_FOUND_PRODUCT);
        }

        Wishlist wishlist = wishlistService.getWishlist(customerId);

        if (wishlist != null && !addAble(wishlist, product, form)) {
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }
        return wishlistService.addProductInWishlist(customerId, form);
    }

    private boolean addAble(Wishlist wishlist, Product product, AddProductInWishlistForm form) {
        Wishlist.Product wishListProduct = wishlist.getProducts().stream().filter(p -> p.getId().equals(form.getId()))
                .findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        Map<Long, Integer> wishListItemCountMap = wishListProduct.getProductItems()
                .stream().collect(Collectors.toMap(
                        Wishlist.ProductItem::getId, Wishlist.ProductItem::getCount));

        Map<Long, Integer> curItemCountMap = product.getProductItems()
                .stream().collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getProductItems().stream().noneMatch(
                formItem -> {
                    Integer wishlistCount = wishListItemCountMap.get(formItem.getId());
                    Integer curCount = curItemCountMap.get(formItem.getId());
                     return (formItem.getCount() + wishlistCount > curCount);
                });
    }
}
