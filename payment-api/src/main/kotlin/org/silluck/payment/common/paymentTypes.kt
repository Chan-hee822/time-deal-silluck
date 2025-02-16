package org.silluck.payment.common

enum class OrderStatus {
    CREATED, FAILED, PAID, CANCELED, PARTIAL_REFUNDED, REFUNDED
}

enum class TransactionType {
    PAYMENT, REFUND, CANCEL
}

enum class TransactionStatus {
    RESERVE, SUCCESS, FAILURE
}