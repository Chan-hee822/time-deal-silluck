package org.silluck.payment.service

import org.silluck.payment.exception.ErrorCode
import org.silluck.payment.exception.PaymentException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PaymentService(
    private val paymentStatusService: PaymentStatusService,
    private val balanceService: BalanceService
) {
    fun pay(
        payServiceRequest: PayServiceRequest
    ): PayServiceResponse {
        // 거래를 상태로 저장
        // 요청 (요청됨) 저장
        val orderId = paymentStatusService.savePayRequest(
            payUserId = payServiceRequest.payUserId,
            orderTitle = payServiceRequest.orderTitle,
            merchantTransactionId = payServiceRequest.merchantTransactionId,
            amount = payServiceRequest.amount
        )
        return try {
            // 잔액 사용 요청
            val payMethodTransactionId = balanceService.useBalance(orderId)

            // 성공 : 거래 성공으로 저장
            val (transactionId, transactionAt) = paymentStatusService.saveAsSuccess(
                orderId,
                payMethodTransactionId
            )

            PayServiceResponse(
                payUserId = payServiceRequest.payUserId,
                amount = payServiceRequest.amount,
                transactionId = transactionId,
                transactedAt = transactionAt
            )
        } catch (e: Exception) {
            // 실패 : 거래 실패로 저장
            val errorCode = if (e is PaymentException) e.errorCode
            else ErrorCode.INTERNAL_SERVER_ERROR

            paymentStatusService.saveAsFailure(orderId, errorCode)
            throw e
        }
    }
}

data class PayServiceRequest(
    val payUserId: String,
    val amount: Long,
    val merchantTransactionId: String,
    val orderTitle: String,
)

data class PayServiceResponse(
    val payUserId: String,
    val amount: Long,
    val transactionId: String,
    val transactedAt: LocalDateTime,
)