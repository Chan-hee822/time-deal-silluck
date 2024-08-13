package org.silluck.user.service.customer;

import lombok.RequiredArgsConstructor;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findByIdAndEmail(Long id, String email) {
        // chaining 익숙해지기 위해 이렇게 표현
        return customerRepository.findById(id).stream().filter(
                customer -> customer.getEmail().equals(email)
        ).findFirst();
    }

    public Optional<Customer> findValidCustomer(String email, String password) {
        return customerRepository.findByEmail(email).stream().filter(
                customer -> customer.getPassword().equals(password)
                && customer.isVerify()
        ).findFirst();
    }
}
