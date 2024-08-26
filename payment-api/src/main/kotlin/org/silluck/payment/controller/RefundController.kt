package org.silluck.payment.controller

import jakarta.validation.Valid
import org.silluck.payment.service.RefundService
import org.silluck.payment.service.RefundServiceRequest
import org.silluck.payment.service.RefundServiceResponse
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/payment")
class RefundController(
    private val refundService: RefundService
) {
    @PostMapping("/refund")
    fun refund(
        @RequestHeader(name = "X-USER-ID") userId: Long,
        @Valid @RequestBody
        form: RefundRequest
    ): RefundResponse = RefundResponse.from(
        refundService.refund(form.toRefundServiceRequest())
    )
}

data class RefundRequest(
    // 원 거래 (환불할 거래)
    val transactionId: String,
    val refundId: String,
    val refundAmount: Long,
    val refundReason: String
) {
    fun toRefundServiceRequest() = RefundServiceRequest(
        transactionId = transactionId,
        refundId = refundId,
        refundAmount = refundAmount,
        refundReason = refundReason
    )
}

data class RefundResponse(
    val refundTransactionId: String,
    val refundAmount: Long,
    val refundedAt: LocalDateTime,
) {
    companion object {
        fun from(refundServiceResponse: RefundServiceResponse) =
            RefundResponse(
                refundTransactionId = refundServiceResponse.refundTransactionId,
                refundAmount = refundServiceResponse.refundAmount,
                refundedAt = refundServiceResponse.refundedAt
            )
    }
}

