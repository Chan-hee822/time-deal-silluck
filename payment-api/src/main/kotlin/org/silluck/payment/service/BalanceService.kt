package org.silluck.payment.service

import org.silluck.payment.client.ChangeBalanceForm
import org.silluck.payment.client.UserClient
import org.silluck.payment.common.TransactionType.PAYMENT
import org.silluck.payment.exception.ErrorCode.INTERNAL_SERVER_ERROR
import org.silluck.payment.exception.ErrorCode.ORDER_NOT_FOUND
import org.silluck.payment.exception.PaymentException
import org.silluck.payment.repository.OrderRepository
import org.silluck.payment.repository.OrderTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BalanceService(
    private val userClient: UserClient,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository
) {
    fun useBalance(orderId: Long): String {
        //계좌 사용 요청
        val order = orderRepository.findById(orderId)
            .orElseThrow { throw PaymentException(ORDER_NOT_FOUND) }

        return userClient.changeBalance(
            userId = order.paymentUser.customerId.toString(),
            form = ChangeBalanceForm(
                from = order.paymentUser.nickname,
                message = "출금",
                money = -order.orderAmount
            )
        ).transactionId
    }

    @Transactional
    fun cancelBalance(refundTransactionId: Long): String {
        val refundTransaction =
            orderTransactionRepository.findById(refundTransactionId)
                .orElseThrow { throw PaymentException(INTERNAL_SERVER_ERROR) }

        /**
         * 1. refundTransactionId -> Order 조회
         * 2. Order -> paymentTransaction
         * 3. paymentTransction.payMethodTransacitonId (원거래의)
         */
        val order = refundTransaction.order
        val paymentTransaction =
            orderTransactionRepository.findByOrderAndTransactionType(
                order,
                PAYMENT
            ).first()

        return userClient.changeBalance(
            userId = refundTransaction.order.paymentUser.customerId.toString(),
            form = ChangeBalanceForm(
                from = paymentTransaction.payMethodTransactionId
                    ?: throw PaymentException(INTERNAL_SERVER_ERROR), // 원거래의 transactionId
                message = "환불 금액 입금",
                money = refundTransaction.transactionAmount
            )
        ).transactionId
    }
}