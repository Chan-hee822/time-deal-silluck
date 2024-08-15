package org.silluck.domain.order.repository;

import org.silluck.domain.order.domain.entity.Product;
import org.silluck.domain.order.domain.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

}
