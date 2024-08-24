package org.silluck.payment.service

import org.silluck.payment.exception.ErrorCode
import org.silluck.payment.exception.PaymentException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RefundService(
    private val refundStatusService: RefundStatusService,
    private val balanceService: BalanceService
) {
    fun refund(
        refundServiceRequest: RefundServiceRequest
    ): RefundServiceResponse {
        // 거래를 상태로 저장
        // 요청 (요청됨) 저장
        val refundTransactionId = refundStatusService.saveRefundRequest(
            originalTransactionId = refundServiceRequest.transactionId,
            merchantRefundId = refundServiceRequest.refundId,
            refundAmount = refundServiceRequest.refundAmount,
            refundReason = refundServiceRequest.refundReason
        )
        return try {
            // 잔액 사용 취소 요청
            val refundBalanceTransactionId =
                balanceService.cancelBalance(refundTransactionId)

            // 성공 : 거래 성공으로 저장
            val (transactionId, transactionAt) = refundStatusService.saveAsSuccess(
                refundTransactionId,
                refundBalanceTransactionId
            )

            RefundServiceResponse(
                refundTransactionId = transactionId,
                refundAmount = refundServiceRequest.refundAmount,
                refundedAt = transactionAt
            )
        } catch (e: Exception) {
            // 실패 : 거래 실패로 저장
            refundStatusService.saveAsFailure(
                refundTransactionId,
                getErrorCode(e)
            )
            throw e
        }
    }

    fun getErrorCode(e: Exception) = if (e is PaymentException) e.errorCode
    else ErrorCode.INTERNAL_SERVER_ERROR
}

data class RefundServiceRequest(
    val transactionId: String,
    val refundId: String,
    val refundAmount: Long,
    val refundReason: String
)

data class RefundServiceResponse(
    val refundTransactionId: String,
    val refundAmount: Long,
    val refundedAt: LocalDateTime,
)