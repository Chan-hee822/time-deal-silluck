package org.silluck.payment.domain

import jakarta.persistence.*
import org.silluck.payment.common.OrderStatus

@Entity
@Table(name = "orders")
class Order(
    val orderId: String,
    @ManyToOne
    val paymentUser: PaymentUser,
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus,
    val orderTitle: String,
    val orderAmount: Long,
    var paidAmount: Long = 0,
    var refundedAmount: Long = 0
) : BaseEntity()
