package org.silluck.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.silluck.user.client.MailgunClient;
import org.silluck.user.client.mailgun.SendMailForm;
import org.silluck.user.domain.dto.request.SignUpForm;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.exception.CustomException;
import org.silluck.user.service.SignUpCustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.silluck.user.exception.ErrorCode.ALREADY_EXIST_USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignUpApplication {    // 회원계정과 인증메일을 보내는 역할 확실히 하기 위해 service에서 분리

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void verifyCustomer(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    @Transactional
    public String customerSignUp(SignUpForm form) {
        if (signUpCustomerService.isEmailExist(form.getEmail())) {
            // custom exception
            throw new CustomException(ALREADY_EXIST_USER);
        } else {
            Customer customer = signUpCustomerService.signUp(form);

            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("silluckofficial@silluck.com")
                    .to(form.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(
                            form.getEmail(), form.getNickname(), code))
                    .build();

            log.info("Send Email Result : {}", mailgunClient.sendEmail(sendMailForm));

            signUpCustomerService.changeCustomerValidationEmail(customer.getId(), code);

            return "회원 가입에 성공하셨습니다.";
        }
    }

    //이메일 발송 준비
    // 인증 코드 생성
    private String getRandomCode() {
        // 코드를 줄이고 생산성 높이기 위해 외부 라이브러리 사용
        return RandomStringUtils.random(10, true, true);    // 숫자와 문자 모두 사용해서 10자리 코드 생성
    }

    //이메일을 위한 템플릿
    private String getVerificationEmailBody(String email, String name, String code) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("Hello ").append(name)
                .append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8081/sighup/verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }

}
