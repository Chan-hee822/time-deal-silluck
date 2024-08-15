package org.silluck.domain.order.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomException.CustomExceptionResponse> exceptionHandler(
            HttpServletRequest request, final CustomException exception) {

        return ResponseEntity.status(exception.getStatus())
                .body(CustomException.CustomExceptionResponse.builder()
                        .status(exception.getStatus())
                        .code(exception.getErrorCode().name())
                        .message(exception.getMessage())
                        .build());
    }
}
