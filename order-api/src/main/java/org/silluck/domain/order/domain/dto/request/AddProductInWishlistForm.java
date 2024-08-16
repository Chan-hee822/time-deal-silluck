package org.silluck.domain.order.domain.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductInWishlistForm {
    private Long id;    // productId
    private Long sellerId;
    private String name;
    private String description;
    private List<ProductItem> productItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private Long id;
        private Long sellerId;
        private String name;
        private Integer price;
        private Integer count;
    }
}