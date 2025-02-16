package org.silluck.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice   // controller 단으로 들어오는 것 처리
@Slf4j
public class ExceptionController {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ExceptionResponse> customRequestException(
            final CustomException customException) {

        log.warn("api Exception : {}", customException.getErrorCode());

        return ResponseEntity.badRequest().body(new ExceptionResponse
                (customException.getMessage(), customException.getErrorCode()));
    }

    @Getter
    @AllArgsConstructor
    @ToString   // 정상적이 호출위해
    public static class ExceptionResponse {
        private String message;
        private ErrorCode errorCode;
    }
}
