package org.silluck.user.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.user.application.SignInApplication;
import org.silluck.user.domain.dto.request.SignInForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
public class SignInController {

    private final SignInApplication signInApplication;

    /**
     * 1 로그인 처리를 한다.
     * 2 토큰을 발행 한다.
     *
     * 토큰을 통해 접근 제어 권환을 확인
     * 기획하지 않은 endpoint에 접근을 막기 위해
     * @param form
     * @return
     */
    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm form) {
        return ResponseEntity.ok(signInApplication.customerLoginToken(form));
    }

    @PostMapping("/seller")
    public ResponseEntity<String> signInSeller(@RequestBody SignInForm form) {
        return ResponseEntity.ok(signInApplication.sellerLoginToken(form));
    }
}
