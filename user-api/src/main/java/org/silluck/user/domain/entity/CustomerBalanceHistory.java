package org.silluck.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;
import org.silluck.user.domain.common.TransactionType;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
@Entity
public class CustomerBalanceHistory extends BaseEntity {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
    private Customer customer;
    // 변경된 돈
    private Integer changeMoney;
    // 해당 시점 잔액
    private Integer currentMoney;
    // 누구로 부터 이벤트 발생
    private String fromMessage;
    private String description;
    // transaction
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private String transactionId;
    private LocalDateTime transactedAt;
}