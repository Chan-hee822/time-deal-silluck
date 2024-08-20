package org.silluck.user.controller;

import lombok.RequiredArgsConstructor;
import org.silluck.user.config.JwtAuthenticationProvider;
import org.silluck.user.domain.common.UserVo;
import org.silluck.user.domain.entity.Seller;
import org.silluck.user.domain.dto.response.SellerDTO;
import org.silluck.user.exception.CustomException;
import org.silluck.user.exception.ErrorCode;
import org.silluck.user.service.seller.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

    private final JwtAuthenticationProvider provider;
    private final SellerService sellerService;

    @GetMapping("/getInfo")
    public ResponseEntity<SellerDTO> getInfo(
            @RequestHeader(name = "X-AUTH-TOKEN") String token) {

        UserVo userVo = provider.getUserVo(token);
        Seller seller = sellerService.findByIdAndEmail(
                userVo.getId(), userVo.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return ResponseEntity.ok(SellerDTO.from(seller));
    }
}
