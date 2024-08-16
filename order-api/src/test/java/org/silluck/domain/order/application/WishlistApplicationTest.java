package org.silluck.domain.order.application;

import org.junit.jupiter.api.Test;
import org.silluck.domain.config.JwtAuthenticationProvider;
import org.silluck.domain.order.domain.dto.request.AddProductForm;
import org.silluck.domain.order.domain.dto.request.AddProductInWishlistForm;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.domain.entity.ProductItem;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.silluck.domain.order.repository.ProductRepository;
import org.silluck.domain.order.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest()
@TestPropertySource(properties = {
        "JWT_SECRET_KEY=testSecretKey"
})
class WishlistApplicationTest {

    @Autowired
    private WishlistApplication wishlistApplication;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Test
    void ADD_PRODUCT_IN_WISHLIST_TEST() {

        Long customerId = 100L;

        wishlistApplication.clearWishlist(customerId);

        Product product = addProduct();
        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        // wishlist에 상품 추가
        Wishlist wishlist = wishlistApplication.addProductInWishlist(
                customerId, makeAddProductForm(result));
        // 데이터가 검증이 들어갔는지 테스트 필요
        assertEquals(wishlist.getMessages().size(), 0);

        wishlist = wishlistApplication.getWishlist(customerId);
        assertEquals(wishlist.getMessages().size(), 1);


        product.getProductItems().get(0).setCount(0);
        productRepository.save(product);
    }

    private AddProductInWishlistForm makeAddProductForm(Product product) {
        // 상품 아이템 중 1개
        ProductItem pi = product.getProductItems().get(0);
        // 위시리스트에 담기는 상품 아이템 생성
        AddProductInWishlistForm.ProductItem productItem =
                AddProductInWishlistForm.ProductItem.builder()
                        .id(pi.getId())
                        .sellerId(pi.getSellerId())
                        .name(pi.getName())
                        .count(5)
                        .price(20000)
                        .build();
        // 반환
        return AddProductInWishlistForm.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .description(product.getDescription())
                .productItems(List.of(productItem))
                .build();
    }

    private Product addProduct() {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키 에어포스 1", "나이키 운동화", 3);

        return productService.addProduct(sellerId, form);
    }


    private static AddProductForm makeProductForm(
            String name, String description, int itemCount) {
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            itemForms.add(makeProductItemForm(null, name + i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(
            Long productId, String name) {
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(50000)
                .count(10)
                .build();
    }

}