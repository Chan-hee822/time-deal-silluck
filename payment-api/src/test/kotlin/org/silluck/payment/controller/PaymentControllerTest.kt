package org.silluck.payment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.CoreMatchers.equalTo
import org.silluck.payment.service.PayServiceResponse
import org.silluck.payment.service.PaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import kotlin.test.Test

@WebMvcTest(PaymentController::class)
@WithMockUser
internal class PaymentControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockkBean
    private lateinit var paymentService: PaymentService

    private val mapper = ObjectMapper()

    @Test
    fun `결제 요청 - 성공 응답`() {
        //given
        every {
            paymentService.pay(any())
        } returns PayServiceResponse(
            payUserId = "p1",
            amount = 101L,
            transactionId = "transactionId",
            transactedAt = LocalDateTime.now()
        )
        //when
        //then
        mockMvc.post("/payment") {
            headers {
                this.contentType = MediaType.APPLICATION_JSON
                this.accept = listOf(MediaType.APPLICATION_JSON)
                this["X-USER-ID"] = "1234"
            }
            content = mapper.writeValueAsString(
                PayRequest(
                    payUserId = "p1",
                    amount = 100,
                    merchantTransactionId = "m1",
                    orderTitle = "orderTitle"
                )
            )
            with(csrf())
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.payUserId", equalTo("p1")) }
            content { jsonPath("$.amount", equalTo(101)) }
            content { jsonPath("$.transactionId", equalTo("transactionId")) }
        }.andDo { print() }
    }

}