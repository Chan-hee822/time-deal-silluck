package org.silluck.payment.service

import org.silluck.payment.common.OrderStatus
import org.silluck.payment.common.OrderStatus.*
import org.silluck.payment.common.TransactionStatus.*
import org.silluck.payment.common.TransactionType.REFUND
import org.silluck.payment.domain.Order
import org.silluck.payment.domain.OrderTransaction
import org.silluck.payment.exception.ErrorCode
import org.silluck.payment.exception.ErrorCode.*
import org.silluck.payment.exception.PaymentException
import org.silluck.payment.repository.OrderTransactionRepository
import org.silluck.payment.util.generateRefundTransactionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 환불의 상태를 저장. 요청, 성공, 실패
 */
@Service
class RefundStatusService(
    private val orderTransactionRepository: OrderTransactionRepository
) {

    @Transactional
    fun saveRefundRequest(
        originalTransactionId: String,
        merchantRefundId: String,
        refundAmount: Long,
        refundReason: String,
    ): Long {
        /**
         * 결제(orderTransaction) 체크
         * 환불이 가능한지 체크
         * 환불이 트랜잭션(orderTransaciton) 저장
         */
        val originalTransaction = (orderTransactionRepository
            .findByTransactionId(originalTransactionId)
            ?: throw PaymentException(ORDER_NOT_FOUND))

        val order = originalTransaction.order

        validationRefund(order, refundAmount)

        return orderTransactionRepository.save(
            OrderTransaction(
                transactionId = generateRefundTransactionId(),
                order = order,
                transactionType = REFUND,
                transactionStatue = RESERVE,
                transactionAmount = refundAmount,
                merchantTransactionId = merchantRefundId,
                description = refundReason
            )
        ).id ?: throw PaymentException(INTERNAL_SERVER_ERROR)
    }

    private fun validationRefund(order: Order, refundAmount: Long) {
        // 주문 상태가 지불인지 체크(환불할 상황인지)
        if (order.orderStatus !in listOf(PAID, PARTIAL_REFUNDED)) {
            throw PaymentException(CANNOT_REFUND)
        }
        // 환불할 금액이 결제 금액 보다 큰지 체크
        if (order.refundedAmount + refundAmount > order.paidAmount) {
            throw PaymentException(EXCEED_REFUNDABLE_AMOUNT)
        }
    }

    @Transactional
    fun saveAsSuccess(
        refundTransactionId: Long, refundBalanceTransactionId: String
    ): Pair<String, LocalDateTime> {
        val orderTransaction =
            orderTransactionRepository.findById(refundTransactionId)
                .orElseThrow { throw PaymentException(INTERNAL_SERVER_ERROR) }
                .apply {
                    transactionStatue = SUCCESS
                    this.payMethodTransactionId = refundBalanceTransactionId
                    transactedAt = LocalDateTime.now()
                }
        val order = orderTransaction.order
        val totalRefundedAmount = getTotalRefundedAmount(order)

        order.apply {
            orderStatus = getNewOrderStatus(this, totalRefundedAmount)
            refundedAmount = totalRefundedAmount
        }

        return Pair(
            orderTransaction.transactionId,
            orderTransaction.transactedAt
                ?: throw PaymentException(INTERNAL_SERVER_ERROR)
        )
    }

    // 부분으로 환불했는지 전체 환불 했는지 체크
    private fun getNewOrderStatus(
        order: Order,
        totalRefundedAmount: Long
    ): OrderStatus = if (order.orderAmount == totalRefundedAmount) REFUNDED
    else PARTIAL_REFUNDED

    private fun getTotalRefundedAmount(order: Order): Long =
        // ex) 100(성공), 200(성공), 300(실패)
        orderTransactionRepository.findByOrderAndTransactionType(
            order, REFUND
        ).filter { it.transactionStatue == SUCCESS } // 100, 200 만
            .sumOf { it.transactionAmount } // 그 값 더해줌 == 300

    fun saveAsFailure(refundTransactionId: Long, errorCode: ErrorCode) {
        orderTransactionRepository
            .findById(refundTransactionId)
            .orElseThrow { throw PaymentException(INTERNAL_SERVER_ERROR) }
            .apply {
                transactionStatue = FAILURE
                failureCode = errorCode.name
                description = errorCode.errorMessage
            }
    }
}