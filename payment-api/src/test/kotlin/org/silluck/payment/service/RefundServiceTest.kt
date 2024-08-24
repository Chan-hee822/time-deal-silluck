package org.silluck.payment.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.silluck.payment.exception.ErrorCode.EXCEED_REFUNDABLE_AMOUNT
import org.silluck.payment.exception.PaymentException
import java.time.LocalDateTime

class RefundServiceTest : BehaviorSpec({
    val refundStatusService = mockk<RefundStatusService>(relaxed = true)
    val balanceService = mockk<BalanceService>()

    val refundService = RefundService(refundStatusService, balanceService)

    // 명시적으로 강제적으로 해줌
    Given("환불 요청이 정상적으로 저장됨") {
        // 공통적으로 사용 하는 부분
        val request = RefundServiceRequest(
            transactionId = "originalTransactionId",
            refundId = "merchantRefundId",
            refundAmount = 100,
            refundReason = "refundReason"
        )
        every {
            refundStatusService.saveRefundRequest(any(), any(), any(), any())
        } returns 1L

        // happy case
        When("계좌 시스템이 정상적으로 환불") {
            every {
                balanceService.cancelBalance(any())
            } returns "balanceTransactionId"
            every {
                refundStatusService.saveAsSuccess(any(), any())
            } returns Pair("refundTransactionId", LocalDateTime.now())
            val result = refundService.refund(request)

            Then("트랜잭션ID, 금액이 응답으로 옴") {
                result.refundTransactionId shouldBe "refundTransactionId"
                result.refundAmount shouldBe 100
            }

            Then("saveAsSuccess 호출됨, saveAsFailure 미호출됨") {
                verify(exactly = 1) {
                    refundStatusService.saveAsSuccess(any(), any())
                }

                verify(exactly = 0) {
                    refundStatusService.saveAsFailure(any(), any())
                }
            }
        }

        // unhappy case
        When("계좌 시스템 환불이 실패함") {
            every {
                balanceService.cancelBalance(any())
            } throws PaymentException(EXCEED_REFUNDABLE_AMOUNT)

            val result = shouldThrow<PaymentException> {
                refundService.refund(request)
            }

            Then("실패를 저장함") {
                result.errorCode shouldBe EXCEED_REFUNDABLE_AMOUNT
                verify(exactly = 1) {
                    refundStatusService.saveAsFailure(any(), any())
                }
            }
        }
    }
})
