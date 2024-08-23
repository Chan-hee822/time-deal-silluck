package org.silluck.payment.service

import org.silluck.payment.client.ChangeBalanceForm
import org.silluck.payment.client.UserClient
import org.silluck.payment.exception.ErrorCode.ORDER_NOT_FOUND
import org.silluck.payment.exception.PaymentException
import org.silluck.payment.repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class BalanceService(
    private val userClient: UserClient,
    private val orderRepository: OrderRepository
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
}