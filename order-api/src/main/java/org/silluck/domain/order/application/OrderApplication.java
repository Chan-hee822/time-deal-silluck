package org.silluck.domain.order.application;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.client.UserClient;
import org.silluck.domain.order.client.user.CustomerDTO;
import org.silluck.domain.order.domain.entity.ProductItem;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.silluck.domain.order.exception.CustomException;
import org.silluck.domain.order.service.ProductItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.silluck.domain.order.exception.ErrorCode.ORDER_FAIL_CHECK_WISHLIST;
import static org.silluck.domain.order.exception.ErrorCode.ORDER_FAIL_NOT_ENOUGH_BALANCE;

/**
 * 주문 시 체크 사항
 * 1. 상품들이 주문가능 상태인지
 * 2. 장바구니에서 주문한다면 가격변동이 있었는지
 * 3. 예치금을 사용한다면 고객의 돈이 충분한지
 * 4. 결제 후 상품의 제고 관리
 */
@Service
@RequiredArgsConstructor
public class OrderApplication {

    private final WishlistApplication wishlistApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    // 1. 주문 시 기존 카트 버림 - 현재 방식
    // (2. 선택 주문 : 내가 사지 않은 아이팀을 살려여함) - 추가해보기
    @Transactional
    public void Order(String token, Wishlist wishlist) {
        // 변경사항 체크 - 장바구니 조회 시 구현한 로직 재사용
        Wishlist orderWishlist = wishlistApplication.refreshWishlist(wishlist);

        if (!orderWishlist.getMessages().isEmpty()) {
            throw new CustomException(ORDER_FAIL_CHECK_WISHLIST);
        }

        CustomerDTO customerDTO = userClient.getCustomerInfo(token).getBody();

        Integer totalPrice = getTotalPrice(wishlist);
        if (customerDTO.getBalance() < totalPrice) {
            throw new CustomException(ORDER_FAIL_NOT_ENOUGH_BALANCE);
        }

        // 롤백 전략 필요 - 필요한 전략 찾기
        // 결제 서비스로 수행
//        userClient.changeBalance(token,
//                ChangeBalanceForm.builder()
//                .from("USER")
//                .message("Order")
//                .money(-totalPrice)
//                .build());

        for (Wishlist.Product wishlistProduct : orderWishlist.getProducts()) {
            for (Wishlist.ProductItem wishlistProductItem : wishlistProduct.getProductItems()) {
                ProductItem productItem = productItemService
                        .getProductItem(wishlistProductItem.getId());
                productItem.setCount(
                        productItem.getCount() - wishlistProductItem.getCount());

            }
        }
    }

    public Integer getTotalPrice(Wishlist wishlist) {

        return wishlist.getProducts().stream().flatMapToInt(
                        product -> product.getProductItems().stream().flatMapToInt(
                                productItem -> IntStream.of(
                                        productItem.getPrice() * productItem.getCount())))
                .sum();
    }
}