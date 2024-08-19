package org.silluck.domain.order.client.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO {

    private Long id;
    private String email;
    private Integer balance;
}
