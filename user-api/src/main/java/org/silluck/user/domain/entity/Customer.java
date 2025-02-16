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
public class Customer extends BaseEntity {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    // email 양식 Validation(검증) 어떻게 하면 좋을지 고민해보기
    private String email;
    private String nickname;
    private String password;
    // 폰 번호 Validation(검증) 어떻게 하면 좋을지 고민해보기
    private String phone;
    private String address;

    private LocalDateTime verifyExpiredAt;
    private String verifiedCode;
    private boolean verify;

    @Column(columnDefinition = "int default 0")
    private Integer balance;    //  현재 잔액 이중화하여 저장

    public static Customer from(SignUpForm form) {
        return Customer.builder()
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

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
