package org.silluck.domain.order.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductItemForm {
    private Long id;        // productItem Id
    private Long productId;
    private String name;
    private Integer price;
    private Integer count;
}