package org.silluck.domain.order.domain.dto.response;

import lombok.*;
import org.silluck.domain.order.domain.entity.Product;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private List<ProductItemDTO> items;

    public static ProductDTO from(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .items(product.getProductItems().stream().map(ProductItemDTO::from).toList())
                .build();
    }
}
