package org.silluck.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.config.JwtAuthenticationProvider;
import org.silluck.domain.domain.common.UserVo;
import org.silluck.domain.order.domain.dto.request.AddProductForm;
import org.silluck.domain.order.domain.dto.request.AddProductItemForm;
import org.silluck.domain.order.domain.dto.response.ProductDTO;
import org.silluck.domain.order.service.ProductItemService;
import org.silluck.domain.order.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/product")
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductForm form) {

        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(ProductDTO.from(
                productService.addProduct(userVo.getId(), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDTO> addProductItem(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductItemForm form) {

        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(ProductDTO.from(
                productItemService.addProductItem(userVo.getId(), form)));
    }
}
