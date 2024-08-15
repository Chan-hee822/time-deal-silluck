package org.silluck.domain.order.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.silluck.domain.order.domain.dto.request.AddProductForm;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Audited    // entity의 내용이 변할 때마다 그 변화에 대해 저장
// ex) 상품에 대한 데이터가 변할 때 내 주문 내역 상품들과 맞지 않을 수 있음 -> 이런 것을 트래킹
@Entity
public class Product extends BaseEntity {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;
    private String name;
    private String description;

    private String imageUrl;
    private Integer price;
    private Integer stock;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private List<ProductItem> productItems = new ArrayList<>();

    public static Product of(Long sellerId, AddProductForm form) {  // form에서 seller id를 가져오기 올 수 없기 때문에 from 대신 of (2개 이상의 파라마티)
        return Product.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .description(form.getDescription())
                .productItems(form.getItems().stream().map(
                        piForm -> ProductItem.of(sellerId, piForm)).toList())
                .build();
    }
}
