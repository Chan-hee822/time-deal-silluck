package org.silluck.user.domain.seller;

import lombok.*;
import org.silluck.user.domain.entity.Customer;
import org.silluck.user.domain.entity.Seller;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class SellerDTO {

    private Long id;
    private String email;

    public static SellerDTO from(Seller customer) {
        return SellerDTO.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .build();
    }
}
