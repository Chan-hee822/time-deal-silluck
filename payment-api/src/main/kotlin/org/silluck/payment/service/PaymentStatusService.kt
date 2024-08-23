package org.silluck.payment.service

import org.silluck.payment.common.OrderStatus
import org.silluck.payment.common.TransactionStatus
import org.silluck.payment.common.TransactionStatus.FAILURE
import org.silluck.payment.common.TransactionStatus.SUCCESS
import org.silluck.payment.common.TransactionType.PAYMENT
import org.silluck.payment.domain.Order
import org.silluck.payment.domain.OrderTransaction
import org.silluck.payment.exception.ErrorCode
import org.silluck.payment.exception.ErrorCode.*
import org.silluck.payment.exception.PaymentException
import org.silluck.payment.repository.OrderRepository
import org.silluck.payment.repository.OrderTransactionRepository
import org.silluck.payment.repository.PaymentUserRepository
import org.silluck.payment.util.generateOrderId
import org.silluck.payment.util.generateTransactionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 결제의 상태를 저장. 요청, 성공, 실패
 */
@Service
class PaymentStatusService(
    private val paymentUserRepository: PaymentUserRepository,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository
) {

    @Transactional
    fun savePayRequest(
        payUserId: String,
        orderTitle: String,
        merchantTransactionId: String,
        amount: Long
    ): Long {
        // order와 orderTransaction 저장
        val paymentUser = paymentUserRepository.findByPayUserId(payUserId)
            ?: throw PaymentException(INVALID_REQUEST, "사용자 없음 : $payUserId")

        val order = orderRepository.save(
            Order(
                orderId = generateOrderId(),
                paymentUser = paymentUser,
                orderStatus = OrderStatus.CREATED,
                orderTitle = orderTitle,
                orderAmount = amount
            )
        )

        orderTransactionRepository.save(
            OrderTransaction(
                transactionId = generateTransactionId(),
                order = order,
                transactionType = PAYMENT,
                transactionStatue = TransactionStatus.RESERVE,
                transactionAmount = amount,
                merchantTransactionId = merchantTransactionId,
                description = orderTitle
            )
        )

        return order.id ?: throw PaymentException(INTERNAL_SERVER_ERROR)
    }

    @Transactional
    fun saveAsSuccess(
        orderId: Long, payMethodTransactionId: String
    ): Pair<String, LocalDateTime> {
        val order = gerOrderByOrderId(orderId)
            .apply {
                orderStatus = OrderStatus.PAID
                paidAmount = orderAmount
            }

        val orderTransaction =
            getOrderTransactionByOrder(order).apply {
                transactionStatue = SUCCESS
                this.payMethodTransactionId = payMethodTransactionId
                transactedAt = LocalDateTime.now()
            }
        return Pair(
            orderTransaction.transactionId,
            orderTransaction.transactedAt
                ?: throw PaymentException(INTERNAL_SERVER_ERROR)
        )
    }

    fun saveAsFailure(orderId: Long, errorCode: ErrorCode) {
        val order = gerOrderByOrderId(orderId)
            .apply {
                orderStatus = OrderStatus.FAILED
            }

        val orderTransaction =
            getOrderTransactionByOrder(order).apply {
                transactionStatue = FAILURE
                failureCode = errorCode.name
                description = errorCode.errorMessage
            }
    }

    private fun gerOrderByOrderId(orderId: Long): Order =
        orderRepository.findById(orderId)
            .orElseThrow { throw PaymentException(ORDER_NOT_FOUND) }

    private fun getOrderTransactionByOrder(order: Order) =
        orderTransactionRepository.findByOrderAndTransactionType(
            order = order,
            transactionType = PAYMENT
        ).first()
}