package org.silluck.domain.order.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class ProductItem extends BaseEntity {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;
    @Audited
    private String name;
    @Audited
    private Integer price;
    private Integer count;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;

    public static ProductItem of(Long sellerId, AddProductItemForm form) {  // form에서 seller id를 가져오기 올 수 없기 때문에 from 대신 of (2개 이상의 파라마티)
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
    }
}
