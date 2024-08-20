package org.silluck.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.application.OrderApplication;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderApplication orderApplication;

    @PostMapping
    public ResponseEntity<Void> order(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody Wishlist wishlist) {
        orderApplication.Order(token, wishlist);
        return ResponseEntity.ok().build();
    }
}