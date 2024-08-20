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

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> findValidCustomer(String email, String password) {
        return customerRepository.findByEmail(email).stream().filter(
                customer -> customer.getPassword().equals(password)
                && customer.isVerify()
        ).findFirst();
    }
}
