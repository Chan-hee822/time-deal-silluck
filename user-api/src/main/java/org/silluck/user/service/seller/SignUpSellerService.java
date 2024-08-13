package org.silluck.user.service.seller;

import lombok.RequiredArgsConstructor;
import org.silluck.user.domain.dto.request.SignUpForm;
import org.silluck.user.domain.entity.Seller;
import org.silluck.user.exception.CustomException;
import org.silluck.user.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.silluck.user.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SignUpSellerService {

    private final SellerRepository sellerRepository;

    public Seller signUp(SignUpForm form) {
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isEmailExist(String email) {
        return sellerRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        // 이미 인증을 완료한 경우
        if (seller.isVerify()) {
            throw new CustomException(ALREADY_VERIFIED);
        }
        // 인증코드가 다를 경우
        else if (!seller.getVerifiedCode().equals(code)) {
            throw new CustomException(WRONG_VERIFICATION);
        }
        // 설정한 인증 시간이 지난 경우
        else if (seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(EXPIRED_CODE);
        }

        seller.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeSellerValidationEmail(Long sellerId, String verificationCode) {
        Optional<Seller> sellerOptional = sellerRepository.findById(sellerId);

        if (sellerOptional.isPresent()) {
            Seller seller = sellerOptional.get();
            seller.setVerifiedCode(verificationCode);
            seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return seller.getVerifyExpiredAt();
        }

        throw new CustomException(NOT_FOUND_USER);
    }
}