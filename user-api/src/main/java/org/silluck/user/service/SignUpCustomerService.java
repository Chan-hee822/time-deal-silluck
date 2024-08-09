package org.silluck.user.service;

import lombok.RequiredArgsConstructor;
import org.silluck.user.domain.dto.request.SignUpForm;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.exception.CustomException;
import org.silluck.user.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.silluck.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }

    public boolean isEmailExist(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 이미 인증을 완료한 경우
        if (customer.isVerify()) {
            throw new CustomException(ALREADY_VERIFIED);
        }
        // 인증코드가 다를 경우
        else if (!customer.getVerifiedCode().equals(code)) {
            throw new CustomException(WRONG_VERIFICATION);
        }
        // 설정한 인증 시간이 지난 경우
        else if (customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(EXPIRED_CODE);
        }

        customer.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeCustomerValidationEmail(Long customerId, String verificationCode) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setVerifiedCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return customer.getVerifyExpiredAt();
        }

        throw new CustomException(NOT_FOUND_USER);
    }
}