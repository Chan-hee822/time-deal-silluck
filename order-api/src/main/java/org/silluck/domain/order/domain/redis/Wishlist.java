package org.silluck.domain.order.domain.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.silluck.domain.order.domain.dto.request.AddProductInWishlistForm;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("wishlist")  // key의 prefix 설정
public class Wishlist {
    @Id
    private Long customerId;
    private List<Product> products = new ArrayList<>();
    // 장바구니 안 상품 정보가 바뀌었을 때 알려주는 메시지
    private List<String> messages = new ArrayList<>();

    public Wishlist(Long customerId) {
        this.customerId = customerId;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    // product 정보 다 가지고 있다. -> 무엇인가 바뀌었을 때 고객에게 알려주기 위해
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private List<ProductItem> productItems = new ArrayList<>();

        public static Product from(AddProductInWishlistForm form) {

            return Product.builder()
                    .id(form.getId())
                    .sellerId(form.getSellerId())
                    .name(form.getName())
                    .description(form.getDescription())
                    .productItems(form.getProductItems()
                            .stream()
                            .map(ProductItem::from).toList())
                    .build();
        }
    }

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

        public static ProductItem from(AddProductInWishlistForm.ProductItem form) {
            return ProductItem.builder()
                    .id(form.getId())
                    .sellerId(form.getSellerId())
                    .name(form.getName())
                    .price(form.getPrice())
                    .count(form.getCount())
                    .build();
        }
    }

    public Wishlist clone() {
        return new Wishlist(customerId, products, messages);
    }
}
