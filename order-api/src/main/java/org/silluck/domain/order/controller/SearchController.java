package org.silluck.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.domain.dto.response.ProductDTO;
import org.silluck.domain.order.service.ProductSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search/product")
public class SearchController {

    private final ProductSearchService productSearchService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(
                productSearchService.searchByName(name).stream()
                        .map(ProductDTO::withoutItemsFrom).toList());
    }

    @GetMapping("/detail")
    public ResponseEntity<ProductDTO> getDetail(@RequestParam Long id) {
        return ResponseEntity.ok(
                ProductDTO.from(productSearchService.getByProductId(id)));
    }
}