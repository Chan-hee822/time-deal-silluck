package org.silluck.payment.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import java.time.LocalDateTime

@FeignClient(
    name = "user-api",
    path = "/customer"
)
interface UserClient {
    @GetMapping("/getInfo")
    fun getCustomerInfo(
        @RequestHeader(name = "X-USER-ID") userId: String,
    ): CustomerDTO

    @PostMapping("/balance")
    fun changeBalance(
        @RequestHeader(name = "X-USER-ID") userId: String,
        @RequestBody form: ChangeBalanceForm
    ): CustomerBalanceHistoryDTO
}

data class CustomerDTO(
    val id: Long?,
    val email: String,
    val balance: Int?,
)

data class ChangeBalanceForm(
    val from: String,
    val message: String,
    val money: Long
)

data class CustomerBalanceHistoryDTO(
    val id: Long,
    val customerId: Long,
    // 변경된 돈
    val changeMoney: Int,
    // 해당 시점 잔액
    val currentMoney: Int,
    // 누구로 부터 이벤트 발생
    val fromMessage: String,
    val description: String,
    // transaction
    val transactionType: TransactionType,
    val transactionId: String,
    val transactedAt: LocalDateTime,
)

enum class TransactionType {
    DEPOSIT, WITHDRAWAL
}
