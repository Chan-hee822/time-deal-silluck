package org.silluck.user.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.config.JwtAuthenticationProvider;
import org.silluck.domain.domain.common.UserVo;
import org.silluck.user.domain.customer.CustomerDTO;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.exception.CustomException;
import org.silluck.user.exception.ErrorCode;
import org.silluck.user.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDTO> getInfo(
            @RequestHeader(name = "X-AUTH-TOKEN") String token) {

        UserVo userVo = provider.getUserVo(token);
        Customer customer = customerService.findByIdAndEmail(
                userVo.getId(), userVo.getEmail()).orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return ResponseEntity.ok(CustomerDTO.from(customer));
    }
}
