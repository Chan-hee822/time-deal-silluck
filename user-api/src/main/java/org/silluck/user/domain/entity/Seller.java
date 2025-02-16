package org.silluck.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.silluck.user.domain.dto.request.SignUpForm;

import java.time.LocalDateTime;
import java.util.Locale;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class Seller extends BaseEntity {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String nickname;
    private String password;
    private String phone;
    private String address;

    private LocalDateTime verifyExpiredAt;
    private String verifiedCode;
    private boolean verify;

    public static Seller from(SignUpForm form) {
        return Seller.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT))
                .password(form.getPassword())
                .nickname(form.getNickname())
                .phone(form.getPhone())
                .address(form.getAddress())
                .verify(false)
                .build();
    }

    public void setVerifyExpiredAt(LocalDateTime verifyExpiredAt) {
        this.verifyExpiredAt = verifyExpiredAt;
    }

    public void setVerifiedCode(String verifiedCode) {
        this.verifiedCode = verifiedCode;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }
}
