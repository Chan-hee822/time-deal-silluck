package org.silluck.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.silluck.domain.order.client.RedisClient;
import org.silluck.domain.order.domain.dto.request.AddProductInWishlistForm;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 1. 아이템 가격 변경 될 수도, 품절이 될 수도 항상 일정하지 않다.
 * 2. 하지만 그 상태를 항상 트래킹 할 수 없고, 어떤 특정 시점에 알 수 있다.
 * 3. 그 시점을 redis에 저장해 놓았다가 고객이 장바구니를 볼 때 확인할 수 있다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final RedisClient redisClient;

    public Wishlist getWishlist(Long customerId) {
        return redisClient.get(customerId, Wishlist.class);
    }

    public Wishlist addProductInWishlist(Long customerId, AddProductInWishlistForm form) {
        Wishlist wishlist = redisClient.get(customerId, Wishlist.class);

        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setCustomerId(customerId);
        }

        /**
         * 상품 추가 시 체크
         * 1. 이전의 같은 상품이 있는지
         * 2. 같은 상품이 있을 때 - 정보가 바뀌었는지
         */
        Optional<Wishlist.Product> productOptional = wishlist.getProducts()
                .stream().filter(p -> p.getId().equals(form.getId())).findFirst();
        if (productOptional.isPresent()) {
            Wishlist.Product redisProduct = productOptional.get();

            // 현재 요청한 아이템들
            List<Wishlist.ProductItem> productItems = form.getProductItems()
                    .stream().map(Wishlist.ProductItem::from).toList();

            // 레디스 안 아이템들 - 검색 속도 빠르게하기 위해 Map에 저장
            Map<Long, Wishlist.ProductItem> redisProductItemMap
                    = redisProduct.getProductItems().stream().collect(Collectors
                    .toMap(Wishlist.ProductItem::getId, pi -> pi));

            // 장바구니 안과 이름이 맞지 않을 경우
            if (!redisProduct.getName().equals(form.getName())) {
                wishlist.addMessage(
                        redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }
            // 장바구니 안과 설명이 맞지 않을 경우
            if (!redisProduct.getDescription().equals(form.getDescription())) {
                wishlist.addMessage(
                        redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }

            for (Wishlist.ProductItem item : productItems) {
                Wishlist.ProductItem redisItem = redisProductItemMap.get(item.getId());
                if (redisItem == null) {
                    // 없는 상풍이기 때문에 redis에 추가
                    redisProduct.getProductItems().add(item);
                } else {
                    // 가격이 변경된 경우
                    if (!redisItem.getPrice().equals(item.getPrice())) {
                        wishlist.addMessage(
                                redisProduct.getName() + "의 가격이 변경되었습니다. 확인 부탁드립니다.");
                    }
                    // 같은 상품 추가
                    redisItem.setCount(redisItem.getCount() + item.getCount());
                }
            }
        } else {
            Wishlist.Product product = Wishlist.Product.from(form);
            wishlist.getProducts().add(product);
        }
        redisClient.put(customerId, wishlist);
        return wishlist;
    }
}