package me.park.user.adapter.`in`.web

import me.park.user.application.command.CreateUserCommand
import me.park.user.application.command.EarnPointCommand
import me.park.user.application.command.UsePointCommand
import me.park.user.application.port.`in`.CreateUserUseCase
import me.park.user.application.port.`in`.GetUserUseCase
import me.park.user.application.port.`in`.PointUseCase
import me.park.user.application.response.PointBalance
import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType
import me.park.user.domain.User
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var createUserUseCase: CreateUserUseCase

    @MockitoBean
    private lateinit var getUserUseCase: GetUserUseCase

    @MockitoBean
    private lateinit var pointUseCase: PointUseCase

    @Test
    @DisplayName("사용자를 생성한다")
    fun createUser() {
        // given
        val user = User.create(name = "park")
        user.id = 1L
        given(createUserUseCase.createUser(CreateUserCommand("park"))).willReturn(user)

        // when
        val resultActions = mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"park"}"""),
        )

        // then
        resultActions
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("park"))
            .andExpect(jsonPath("$.token").value(user.token))
            .andExpect(jsonPath("$.status").value(user.status.name))
    }

    @Test
    @DisplayName("토큰으로 사용자를 조회한다")
    fun getUserByToken() {
        // given
        val user = User.create(name = "park")
        user.id = 1L
        given(getUserUseCase.getUserByToken(user.token)).willReturn(user)

        // when
        val resultActions = mockMvc.perform(
            get("/users/token/{token}", user.token),
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.token").value(user.token))
    }

    @Test
    @DisplayName("포인트를 적립한다")
    fun earnPoint() {
        // given
        val command = EarnPointCommand(
            userId = 1L,
            amount = 100L,
            reason = "대출 적립",
            referenceType = "RENTAL",
            referenceId = "rental-1",
        )
        given(pointUseCase.earnPoint(command)).willReturn(PointBalance(userId = 1L, balance = 100L))

        // when
        val resultActions = mockMvc.perform(
            post("/users/{userId}/points/earn", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "amount": 100,
                      "reason": "대출 적립",
                      "referenceType": "RENTAL",
                      "referenceId": "rental-1"
                    }
                    """.trimIndent(),
                ),
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.balance").value(100))
    }

    @Test
    @DisplayName("포인트를 사용한다")
    fun usePoint() {
        // given
        val command = UsePointCommand(
            userId = 1L,
            amount = 40L,
            reason = "연체료 상환",
            referenceType = null,
            referenceId = null,
        )
        given(pointUseCase.usePoint(command)).willReturn(PointBalance(userId = 1L, balance = 60L))

        // when
        val resultActions = mockMvc.perform(
            post("/users/{userId}/points/use", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"amount":40,"reason":"연체료 상환"}"""),
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.balance").value(60))
    }

    @Test
    @DisplayName("포인트 원장을 조회한다")
    fun getPointLedgers() {
        // given
        val ledger = PointLedger.earn(
            userId = 1L,
            amount = 100L,
            balanceAfter = 100L,
            reason = "대출 적립",
            referenceType = null,
            referenceId = null,
        )
        ledger.id = 10L
        given(pointUseCase.getPointLedgers(1L)).willReturn(listOf(ledger))

        // when
        val resultActions = mockMvc.perform(
            get("/users/{userId}/points/ledgers", 1L),
        )

        // then
        resultActions
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(10))
            .andExpect(jsonPath("$[0].type").value(PointLedgerType.EARN.name))
            .andExpect(jsonPath("$[0].amount").value(100))
            .andExpect(jsonPath("$[0].balanceAfter").value(100))
    }
}
