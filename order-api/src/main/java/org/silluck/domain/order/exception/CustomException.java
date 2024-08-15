package org.silluck.domain.order.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Integer status;
    private static final ObjectMapper mapper = new ObjectMapper();

    // 커스텀으로 예외처리하여 상태관리에 적합하게
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getDetail());
        this.errorCode = errorCode;
        this.status = errorCode.getHttpStatus().value();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomExceptionResponse {
        private int status;
        private String code;
        private String message;
    }

}
