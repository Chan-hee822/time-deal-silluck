package org.silluck.user.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.user.domain.dto.request.ChangeBalanceForm;
import org.silluck.user.domain.dto.response.CustomerBalanceHistoryDTO;
import org.silluck.user.domain.dto.response.CustomerDTO;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.exception.CustomException;
import org.silluck.user.exception.ErrorCode;
import org.silluck.user.service.customer.CustomerBalanceService;
import org.silluck.user.service.customer.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDTO> getInfo(
            @RequestHeader(name = "X-USER-ID") String userId) {

        Customer customer = customerService.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        return ResponseEntity.ok(CustomerDTO.from(customer));
    }

    @PostMapping("/balance")
    public ResponseEntity<CustomerBalanceHistoryDTO> changeBalance(
            @RequestHeader(name = "X-USER-ID") String userId,
            @RequestBody ChangeBalanceForm form) {

        return ResponseEntity.ok(CustomerBalanceHistoryDTO.from(
                customerBalanceService.changeBalance(
                        Long.parseLong(userId), form)));
    }
}
