package org.silluck.domain.order.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.domain.entity.QProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> searchByName(String name) {
        String search = "%" + name + "%";

        QProduct product = QProduct.product;
        return queryFactory.select(product)
                .from(product)  // from 절 추가
                .where(product.name.like(search)).fetch();
    }
}
