package org.silluck.user.service;

import org.junit.jupiter.api.Test;
import org.silluck.user.domain.dto.request.SignUpForm;
import org.silluck.user.domain.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = "mailgun.key=test_key")
@Transactional  // 테스트 후 데이터 롤백
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService signUpCustomerService;

    @Test
    void signUp() {

        SignUpForm form = SignUpForm.builder()
                .email("email123@test.com")
                .nickname("nickname")
                .phone("01012341234")
                .address("seoul")
                .password("1111")
                .build();

        Customer c = signUpCustomerService.signUp(form);
        assertEquals("email123@test.com", c.getEmail());
    }
}