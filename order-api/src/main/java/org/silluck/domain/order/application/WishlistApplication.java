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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     * 1. 장바구니에 상품 추가
     * 2. 상품의 가격이나 수량이 변동된다.
     * 3. 변동 되면 그것을 빼주거나하는 등 어떤 처리가 필요하다.
     */
    public Wishlist getWishlist(Long customerId) {
        /**
         * - 조회하는 순간 메시지를 읽었다고 판단 메시지 삭제
         * 1. response 에서는 메시지가 함께 나간다.
         * 2. redis 에 저장할 때는 메시지가 없어야 한다.
         */
        Wishlist wishlist = refreshWishlist(wishlistService.getWishlist(customerId));

        Wishlist returnWishlist = new Wishlist();
        returnWishlist.setCustomerId(customerId);
        returnWishlist.setProducts(wishlist.getProducts());
        returnWishlist.setMessages(wishlist.getMessages());

        wishlist.setMessages(new ArrayList<>());
        wishlistService.putWishlist(customerId, wishlist);

        return returnWishlist;
    }

    private Wishlist refreshWishlist(Wishlist wishlist) {
        /**
         * 1. 상품이나 상품 아이템 정보, 가격, 수량 변경 체크 그에 맞는 알람 제공
         * 2. 바뀐 데이터 임의로 변경
         * 3. 메시지를 보고 난 다음에는, 이미 본 메시지는 삭제 필요
         */
        // 현재 db에 있는 상품들
        Map<Long, Product> curProductMap = productSearchService
                .getListByProductIds(wishlist.getProducts().stream()
                        .map(Wishlist.Product::getId).toList()).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        for (int i = 0; i < wishlist.getProducts().size(); i++) {
            // 위시리스트에 있는 상품
            Wishlist.Product wishlistProduct = wishlist.getProducts().get(i);
            // 현재 db에 있는 상품
            Product curProduct = curProductMap.get(wishlistProduct.getId());

            // 현재 db에 그 상품이 없는 경우
            if (curProduct == null) {
                wishlist.getProducts().remove(wishlistProduct);
                i--;
                wishlist.addMessage(wishlistProduct.getName() + "상품이 삭제되었습니다.");
                continue;
            }

            // 현재 db에 있는 상품 아이템들
            Map<Long, ProductItem> curProductItemMap = curProduct.getProductItems()
                    .stream().collect(Collectors.toMap(
                            ProductItem::getId, productItem -> productItem));

            // 에러 케이스가 발생할 수 있는 상황들 체크
            List<String> tempMessages = new ArrayList<>();  // 발생할 수 있는 알림 임시 저장
            for (int j = 0; j < wishlistProduct.getProductItems().size(); j++) {
                Wishlist.ProductItem wishlistProductItem
                        = wishlistProduct.getProductItems().get(j);
                ProductItem curProductItem
                        = curProductItemMap.get(wishlistProductItem.getId());

                // 현재 그 상품 아이템이 없는 경우
                if (curProductItem == null) {
                    wishlistProduct.getProductItems().remove(wishlistProductItem);
                    j--;
                    tempMessages.add(wishlistProductItem.getName()
                            + " 옵션(아이템)이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChanged = false;
                boolean isCountNotEnough = false;
                // 가격 변동이 있는 경우
                if (!wishlistProductItem.getPrice().equals(curProductItem.getPrice())) {
                    isPriceChanged = true;
                    wishlistProductItem.setPrice(curProductItem.getPrice());
                }
                // 현재 수량이 모자라는 경우
                if (wishlistProductItem.getCount() > curProductItem.getCount()) {
                    isCountNotEnough = true;
                    wishlistProductItem.setCount(curProductItem.getCount());
                }
                if (isPriceChanged && isCountNotEnough) {
                    // 1번 경우 : 가격 변동 및 수량 부족
                    tempMessages.add(wishlistProductItem.getName()
                            + " 가격 변경 및 수량이 가능한 최대로 변경되었습니다.");
                } else if (isPriceChanged) {
                    // 2번 경우 : 가격 변동
                    tempMessages.add(wishlistProductItem.getName()
                            + " 가격이 변경되었습니다.");
                } else if (isCountNotEnough) {
                    // 3번 경우 : 수량 부족
                    tempMessages.add(wishlistProductItem.getName()
                            + " 수량이 가능한 최대로 변경되었습니다.");
                }
            }
            // 위시리스트(장바구니)에 있던 상품 아이템 모두 구매 불가한 경우 (아이템 자체가 없어져서)
            // 품절인 경우와는 다른 경우
            if (wishlistProduct.getProductItems().isEmpty()) {
                wishlist.getProducts().remove(wishlistProduct);
                i--;
                wishlist.addMessage(wishlistProduct.getName()
                        + " 상품 옵션(아이템)이 현재 존재하지 않아 구매할 수 없습니다.");
            }
            // 변동 사항 있으면 메시지 알림
            else if (!tempMessages.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(wishlistProduct.getName()).append(" 상품 번동 사항 : ");
                for (String message : tempMessages) {
                    sb.append(message).append(", ");
                }
                wishlist.addMessage(sb.toString().trim());
            }
        }
        wishlistService.putWishlist(wishlist.getCustomerId(), wishlist);
        return wishlist;
    }

    public void clearWishlist(Long customerId) {
        wishlistService.putWishlist(customerId, null);
    }

    private boolean addAble(Wishlist wishlist, Product product, AddProductInWishlistForm form) {

        Wishlist.Product wishListProduct = wishlist.getProducts().stream()
                .filter(p -> p.getId().equals(form.getId())).findFirst()
                .orElse(Wishlist.Product.builder()
                        .id(product.getId())
                        .productItems(Collections.emptyList())
                        .build());

        Map<Long, Integer> wishListItemCountMap = wishListProduct.getProductItems()
                .stream().collect(Collectors.toMap(
                        Wishlist.ProductItem::getId, Wishlist.ProductItem::getCount));

        Map<Long, Integer> curItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getProductItems().stream().noneMatch(
                formItem -> {
                    Integer wishlistCount =
                            wishListItemCountMap.get(formItem.getId()) == null ? 0
                                    : wishListItemCountMap.get(formItem.getId());

                    Integer curCount = curItemCountMap.get(formItem.getId()) == null ? 0
                            : curItemCountMap.get(formItem.getId());

                    return (formItem.getCount() + wishlistCount > curCount);
                });
    }
}