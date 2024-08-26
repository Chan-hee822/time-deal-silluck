package org.silluck.payment.repository

import org.silluck.payment.common.TransactionType
import org.silluck.payment.domain.Order
import org.silluck.payment.domain.OrderTransaction
import org.silluck.payment.domain.PaymentUser
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentUserRepository : JpaRepository<PaymentUser, Long> {
    fun findByPayUserId(payUserId: String): PaymentUser?
}

interface OrderRepository : JpaRepository<Order, Long> {

}

interface OrderTransactionRepository : JpaRepository<OrderTransaction, Long> {
    fun findByOrderAndTransactionType(
        order: Order, transactionType: TransactionType
    ): List<OrderTransaction>

    fun findByTransactionId(transactionId: String): OrderTransaction?
}