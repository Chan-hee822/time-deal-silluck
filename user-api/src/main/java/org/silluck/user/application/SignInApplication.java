package org.silluck.user.application;

import lombok.RequiredArgsConstructor;
import org.silluck.user.config.JwtAuthenticationProvider;
import org.silluck.user.domain.common.UserType;
import org.silluck.user.domain.dto.request.SignInForm;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.domain.entity.Seller;
import org.silluck.user.exception.CustomException;
import org.silluck.user.service.customer.CustomerService;
import org.silluck.user.service.seller.SellerService;
import org.springframework.stereotype.Service;

import static org.silluck.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;

    /**
     * 1. 로그인 가능 여부 체크
     * 2. 토큰 발행
     * 3. 토큰을 response
     *
     * @param form
     * @return
     */
    public String customerLoginToken(SignInForm form) {
        // 로그인 가능 여부
        Customer customer = customerService
                .findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        // 토큰 발행 및 response
        return provider.createToken(
                customer.getEmail(), customer.getId(), UserType.CUSTOMER);
    }

    public String sellerLoginToken(SignInForm form) {
        Seller seller = sellerService
                .findValidSeller(form.getEmail(), form.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        return provider.createToken(
                seller.getEmail(), seller.getId(), UserType.SELLER);
    }
}
