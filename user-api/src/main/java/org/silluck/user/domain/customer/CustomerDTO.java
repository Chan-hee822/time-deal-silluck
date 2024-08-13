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

    public static CustomerDTO from(Customer customer) {
        return new CustomerDTO(customer.getId(), customer.getEmail());
    }
}
