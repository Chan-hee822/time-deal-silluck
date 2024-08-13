package org.silluck.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND_ORDER(HttpStatus.BAD_REQUEST, "해당 주문이 존재하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String detail;

}
