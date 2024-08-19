package org.silluck.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "해당 상품이 존재하지 않습니다."),
    NOT_FOUND_ITEM(HttpStatus.BAD_REQUEST, "해당 아이템이 존재하지 않습니다."),
    SAME_ITEM_NAME(HttpStatus.BAD_REQUEST, "같은 이름의 아이템이 존재합니다."),

    WISHLIST_CHANGE_FAIL(HttpStatus.BAD_REQUEST, "장바구니에 추가할 수 없습니다."),
    ITEM_COUNT_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "상품 수량이 부족합니다."),

    ORDER_FAIL_CHECK_WISHLIST(HttpStatus.BAD_REQUEST, "상품 정보가 변경되어 주문이 불가합니다. 장바구니를 확인해주세요."),
    ORDER_FAIL_NOT_ENOUGH_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족하여 주문이 불가합니다. 잔액을 확인해주세요."),

    NOT_FOUND_ORDER(HttpStatus.BAD_REQUEST, "해당 주문이 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String detail;

}
