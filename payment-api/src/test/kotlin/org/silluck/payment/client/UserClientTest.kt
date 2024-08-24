package org.silluck.payment.client

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest
class UserClientTest {

    @MockkBean
    private lateinit var userClient: UserClient

    @Test
    fun `잔액 사용`() {
        //given
        val changeBalanceForm = ChangeBalanceForm(
            from = "user",
            message = "출금",
            money = -1000
        )

        val expectedResponse = CustomerBalanceHistoryDTO(
            id = 1,
            customerId = 2,
            changeMoney = -1000,
            currentMoney = 100000,
            fromMessage = "user",
            description = "출금",
            transactionType = TransactionType.WITHDRAWAL,
            transactionId = "123123141",
            transactedAt = LocalDateTime.now()
        )

        every {
            userClient.changeBalance(
                "2",
                changeBalanceForm
            )
        } returns expectedResponse

        //when
        val changeBalanceDTO = userClient.changeBalance("2", changeBalanceForm)

        // then
        assert(changeBalanceDTO.changeMoney == expectedResponse.changeMoney)

        println(changeBalanceDTO.changeMoney)
    }
}
