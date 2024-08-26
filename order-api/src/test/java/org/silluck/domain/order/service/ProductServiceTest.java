package org.silluck.domain.order.service;

import org.junit.jupiter.api.Test;
import org.silluck.domain.order.domain.dto.request.AddProductForm;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_SECRET_KEY=testSecretKey"
})
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

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
                .count(1)
                .build();
    }

    @Test
    void ADD_PRODUCT_TEST() {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키 에어포스 1", "나이키 운동화", 3);

        Product product = productService.addProduct(sellerId, form);

        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        assertNotNull(result);
        assertEquals(result.getName(), "나이키 에어포스 1");
        assertEquals(result.getDescription(), "나이키 운동화");
        // 지연로딩으로 하위 엔티티 가져오지 못 함 처리 필요
        assertEquals(result.getProductItems().size(), 3);
        assertEquals(result.getProductItems().get(0).getName(), "나이키 에어포스 10");
        assertEquals(result.getProductItems().get(0).getPrice(), 50000);
        assertEquals(result.getProductItems().get(0).getCount(), 1);
    }
}