package org.silluck.payment.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.silluck.payment.service.PayServiceRequest
import org.silluck.payment.service.PayServiceResponse
import org.silluck.payment.service.PaymentService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/payment")
class PaymentController(
    private val paymentService: PaymentService  // Been 찾아 주입
) {
    @PostMapping
    fun pay(
        @RequestHeader(name = "X-USER-ID") userId: Long,
        @Valid @RequestBody
        form: PayRequest
    ): PayResponse = PayResponse.from(
        paymentService.pay(form.toPayServiceRequest())
    )
}

data class PayRequest(
    @field: NotBlank            // field가 없으면 생성자에 들어갈 때만 체크를 함
    val payUserId: String,
    @field: Min(1)
    val amount: Long,
    @field: NotBlank
    val merchantTransactionId: String,
    @field: NotBlank
    val orderTitle: String,
) {
    fun toPayServiceRequest() = PayServiceRequest(
        payUserId = payUserId,
        amount = amount,
        merchantTransactionId = merchantTransactionId,
        orderTitle = orderTitle
    )
}

data class PayResponse(
    val payUserId: String,
    val amount: Long,
    val transactionId: String,
    val transactedAt: LocalDateTime,
) {
    companion object {
        fun from(payServiceResponse: PayServiceResponse) =
            PayResponse(
                payUserId = payServiceResponse.payUserId,
                amount = payServiceResponse.amount,
                transactionId = payServiceResponse.transactionId,
                transactedAt = payServiceResponse.transactedAt
            )
    }
}

