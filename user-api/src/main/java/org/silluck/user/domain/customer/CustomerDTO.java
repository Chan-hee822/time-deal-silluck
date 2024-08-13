package org.silluck.user.domain.customer;

import lombok.*;
import org.silluck.user.domain.entity.Customer;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class CustomerDTO {

    private Long id;
    private String email;
    private Integer balance;

    public static CustomerDTO from(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .balance(customer.getBalance() == null ? 0 : customer.getBalance())
                .build();
    }
}
