package org.silluck.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.dto.request.AddProductForm;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;
import org.silluck.domain.order.domain.dto.request.UpdateProductForm;
import org.silluck.domain.order.domain.dto.request.UpdateProductItemForm;
import org.silluck.domain.order.domain.dto.response.ProductDTO;
import org.silluck.domain.order.domain.dto.response.ProductItemDTO;
import org.silluck.domain.order.service.ProductItemService;
import org.silluck.domain.order.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody AddProductForm form) {

        return ResponseEntity.ok(ProductDTO.from(
                productService.addProduct(Long.parseLong(token), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDTO> addProductItem(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody AddProductItemForm form) {

        return ResponseEntity.ok(ProductDTO.from(
                productItemService.addProductItem(Long.parseLong(token), form)));
    }

    @PutMapping
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody UpdateProductForm form) {

        return ResponseEntity.ok(ProductDTO.from(
                productService.updateProduct(Long.parseLong(token), form)));
    }

    @PutMapping("/item")
    public ResponseEntity<ProductItemDTO> updateProductItem(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody UpdateProductItemForm form) {

        return ResponseEntity.ok(ProductItemDTO.from(
                productItemService.updateProductItem(Long.parseLong(token), form)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestParam Long id) {

        productService.deleteProduct(Long.parseLong(token), id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item")
    public ResponseEntity<Void> deleteProductItem(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestParam Long id) {

        productItemService.deleteProductItem(Long.parseLong(token), id);
        return ResponseEntity.ok().build();
    }

}
