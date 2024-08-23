package org.silluck.payment.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne
import org.silluck.payment.common.TransactionStatus
import org.silluck.payment.common.TransactionType
import java.time.LocalDateTime

@Entity
class OrderTransaction(
    val transactionId: String,
    @ManyToOne
    val order: Order,
    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,       // 결제, 취소, 망취소
    @Enumerated(EnumType.STRING)
    var transactionStatue: TransactionStatus,   // 생성, 성공, 실패
    val transactionAmount: Long,                // 거래금액
    val merchantTransactionId: String,          // 판매자
    var payMethodTransactionId: String? = null, // 거래방법
    var transactedAt: LocalDateTime? = null,
    var failureCode: String? = null,
    var description: String? = null
) : BaseEntity()
