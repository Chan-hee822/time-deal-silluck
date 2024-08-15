package org.silluck.domain.order.domain.dto.response;

import lombok.*;
import org.silluck.domain.order.domain.entity.ProductItem;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemDTO {
    private Long id;
    private String name;
    private Integer price;
    private Integer count;

    public static ProductItemDTO from(ProductItem productItem) {
        return ProductItemDTO.builder()
                .id(productItem.getId())
                .name(productItem.getName())
                .price(productItem.getPrice())
                .count(productItem.getCount())
                .build();
    }
}
