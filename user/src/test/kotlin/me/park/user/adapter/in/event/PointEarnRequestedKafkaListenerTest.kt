package me.park.user.adapter.`in`.event

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.park.user.application.command.EarnPointCommand
import me.park.user.application.event.PointEarnRequestedEvent
import me.park.user.application.port.`in`.PointUseCase
import me.park.user.application.response.PointBalance
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertContains

@ExtendWith(OutputCaptureExtension::class)
class PointEarnRequestedKafkaListenerTest {

    @Test
    @DisplayName("포인트 적립 요청 Kafka 메시지를 유스케이스로 전달한다")
    fun listenPointEarnRequestedEvent() {
        // given
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val event = PointEarnRequestedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            amount = 100L,
            reason = "도서 대출 적립",
            referenceType = "RENTAL",
            referenceId = "11111111-1111-1111-1111-111111111111",
            occurredAt = LocalDateTime.of(2026, 6, 17, 10, 0),
        )
        val pointUseCase = mockk<PointUseCase>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.readValue(payload, PointEarnRequestedEvent::class.java) } returns event
        every {
            pointUseCase.earnPoint(
                EarnPointCommand(
                    userId = event.userId,
                    amount = event.amount,
                    reason = event.reason,
                    referenceType = event.referenceType,
                    referenceId = event.referenceId,
                ),
            )
        } returns PointBalance(userId = event.userId, balance = 100L)
        val listener = PointEarnRequestedKafkaListener(
            pointUseCase = pointUseCase,
            objectMapper = objectMapper,
        )

        // when
        listener.listen(payload)

        // then
        verify {
            pointUseCase.earnPoint(
                EarnPointCommand(
                    userId = event.userId,
                    amount = event.amount,
                    reason = event.reason,
                    referenceType = event.referenceType,
                    referenceId = event.referenceId,
                ),
            )
        }
    }

    @Test
    @DisplayName("포인트 적립 요청 Kafka 이벤트를 받으면 수신 로그를 남긴다")
    fun logWhenPointEarnRequestedEventReceived(output: CapturedOutput) {
        // given
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val event = PointEarnRequestedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            amount = 100L,
            reason = "도서 대출 적립",
            referenceType = "RENTAL",
            referenceId = "11111111-1111-1111-1111-111111111111",
            occurredAt = LocalDateTime.of(2026, 6, 17, 10, 0),
        )
        val pointUseCase = mockk<PointUseCase>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.readValue(payload, PointEarnRequestedEvent::class.java) } returns event
        every { pointUseCase.earnPoint(any()) } returns PointBalance(userId = event.userId, balance = 100L)
        val listener = PointEarnRequestedKafkaListener(
            pointUseCase = pointUseCase,
            objectMapper = objectMapper,
        )

        // when
        listener.listen(payload)

        // then
        assertContains(output.all, "포인트 적립 요청 Kafka 이벤트를 수신했습니다.")
        assertContains(output.all, "requestId=${event.requestId}")
        assertContains(output.all, "userId=${event.userId}")
        assertContains(output.all, "amount=${event.amount}")
    }
}
