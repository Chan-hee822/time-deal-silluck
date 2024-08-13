package org.silluck.user.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.user.application.SignUpApplication;
import org.silluck.user.domain.dto.request.SignUpForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping
    public ResponseEntity<String> customerSignup(@RequestBody SignUpForm form) {
        return ResponseEntity.ok(signUpApplication.customerSignUp(form));
    }

    @GetMapping("/verify/customer")
    public ResponseEntity<String> verifyCustomer(String email, String code) {   // 이메일을 노출시키는 상황 암호화 고려
        signUpApplication.verifyCustomer(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
