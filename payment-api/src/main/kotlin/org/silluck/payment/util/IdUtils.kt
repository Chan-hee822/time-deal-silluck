package org.silluck.payment.util

import java.util.*

fun generateOrderId() = "PO" + generateUUID()    // PO = Payment Order
fun generateTransactionId() =
    "PT" + generateUUID()    // PT = Payment Transaction

fun generateRefundTransactionId() =
    "RT" + generateUUID()    // RT = Refund Transaction

private fun generateUUID() = UUID.randomUUID().toString().replace("-", "")