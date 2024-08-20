package org.silluck.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.order.application.WishlistApplication;
import org.silluck.domain.order.domain.dto.request.AddProductInWishlistForm;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class CustomerWishlistController {

    private final WishlistApplication wishlistApplication;

    @PostMapping
    public ResponseEntity<Wishlist> addProductInWishlist(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody AddProductInWishlistForm form) {
//        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(wishlistApplication
                .addProductInWishlist(Long.parseLong(token), form));
    }

    @GetMapping
    public ResponseEntity<Wishlist> getWishlist(
            @RequestHeader(name = "X-USER-ID") String token) {
//        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(wishlistApplication.getWishlist(Long.parseLong(token)));
    }

    @PutMapping
    private ResponseEntity<Wishlist> updateWishlist(
            @RequestHeader(name = "X-USER-ID") String token,
            @RequestBody Wishlist wishlist) {
//        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(wishlistApplication
                .updateWishlist(Long.parseLong(token), wishlist));
    }
}