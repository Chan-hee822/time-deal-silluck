package org.silluck.user.service;

import lombok.RequiredArgsConstructor;
import org.silluck.user.domain.SignUpForm;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form) {
        return customerRepository.save(Customer.from(form));
    }
}