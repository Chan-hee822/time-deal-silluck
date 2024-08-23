package org.silluck.payment.util

import java.util.*

fun generateOrderId() = "PO" + generateUUID()    // PO = Payment Order
fun generateTransactionId() = "PT" + generateUUID()    // PO = Payment Order

private fun generateUUID() = UUID.randomUUID().toString().replace("-", "")