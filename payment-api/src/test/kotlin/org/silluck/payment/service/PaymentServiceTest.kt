package org.silluck.payment.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.silluck.payment.exception.ErrorCode
import org.silluck.payment.exception.PaymentException
import java.time.LocalDateTime
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
internal class PaymentServiceTest {

    @RelaxedMockK
    lateinit var paymentStatusService: PaymentStatusService

    @MockK
    lateinit var balanceService: BalanceService

    @InjectMockKs
    lateinit var paymentService: PaymentService

    @Test
    fun `결제 성공`() {
        //given
        val request = PayServiceRequest(
            payUserId = "payUserId",
            amount = 100L,
            merchantTransactionId = "merchantTransactionId",
            orderTitle = "orderTitle"
        )
        every {
            paymentStatusService.savePayRequest(any(), any(), any(), any())
        } returns 1L
        every {
            balanceService.useBalance(any())
        } returns "payMethodTransactionId"
        every {
            paymentStatusService.saveAsSuccess(any(), any())
        } returns Pair("TransactionId", LocalDateTime.MIN)
        //when
        val result = paymentService.pay(request)
        //then
//        assertEquals(100L, result.amount)
        result.amount shouldBe 100L
        result.payUserId shouldBe "payUserId"
        // 호출 빈도
        verify(exactly = 1) {
            paymentStatusService.saveAsSuccess(any(), any())
        }
        verify(exactly = 0) {
            paymentStatusService.saveAsFailure(any(), any())
        }
    }

    @Test
    fun `결제 실패 - 잔액 부족`() {
        //given
        val request = PayServiceRequest(
            payUserId = "payUserId",
            amount = 1000000L,
            merchantTransactionId = "merchantTransactionId",
            orderTitle = "orderTitle"
        )
        every {
            paymentStatusService.savePayRequest(any(), any(), any(), any())
        } returns 1L
        every {
            balanceService.useBalance(any())
        } throws PaymentException(ErrorCode.NOT_ENOUGH_BALANCE)
        every {
            paymentStatusService.saveAsFailure(any(), any())
        } returns Unit // 자바에서 void와 비슷

        //when
        val result = shouldThrow<PaymentException> {
            paymentService.pay(request)
        }
        //then
        result.errorCode shouldBe ErrorCode.NOT_ENOUGH_BALANCE

        verify(exactly = 0) {
            paymentStatusService.saveAsSuccess(any(), any())
        }
        verify(exactly = 1) {
            paymentStatusService.saveAsFailure(any(), any())
        }
    }
}