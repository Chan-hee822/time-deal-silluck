package org.silluck.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 유저 - customer, 회원가입
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 유저가 없습니다."),
    ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증이 완료 되었습니다."),
    EXPIRED_CODE(HttpStatus.BAD_REQUEST, "인증시가닝 만료되었습니다."),
    WRONG_VERIFICATION(HttpStatus.BAD_REQUEST, "잘못된 인증 시도입니다."),

    // 유저 - customer, 로그인
    LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST, "로그인 실패, 이메일이나 비밀번호를 확인해주세요.");

    private final HttpStatus httpStatus;
    private final String detail;

}
