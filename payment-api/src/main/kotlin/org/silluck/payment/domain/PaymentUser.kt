package org.silluck.payment.domain

import jakarta.persistence.Entity

@Entity
class PaymentUser(
    var payUserId: String,
    val customerId: Long,
    val nickname: String
) : BaseEntity()