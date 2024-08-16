package org.silluck.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.domain.config.JwtAuthenticationProvider;
import org.silluck.domain.domain.common.UserVo;
import org.silluck.domain.order.application.WishlistApplication;
import org.silluck.domain.order.domain.dto.request.AddProductInWishlistForm;
import org.silluck.domain.order.domain.redis.Wishlist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/wishlist")
public class CustomerWishlistController {

    private final WishlistApplication wishlistApplication;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Wishlist> addProductInWishlist(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductInWishlistForm form) {
        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(wishlistApplication
                .addProductInWishlist(userVo.getId(), form));
    }

    @GetMapping
    public ResponseEntity<Wishlist> getWishlist(
            @RequestHeader(name = "X-AUTH-TOKEN") String token) {
        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(wishlistApplication.getWishlist(userVo.getId()));
    }

    @PutMapping
    private ResponseEntity<Wishlist> updateWishlist(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody Wishlist wishlist) {
        UserVo userVo = provider.getUserVo(token);
        return ResponseEntity.ok(wishlistApplication
                .updateWishlist(userVo.getId(), wishlist));
    }
}