package me.park.book.adapter.`in`.event

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.park.book.application.command.RestoreStockCommand
import me.park.book.application.event.StockRestoreRequestedEvent
import me.park.book.application.port.`in`.RestoreStockUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertContains

@ExtendWith(OutputCaptureExtension::class)
class StockRestoreRequestedKafkaListenerTest {

    @Test
    @DisplayName("재고 복구 요청 Kafka 메시지를 유스케이스로 전달한다")
    fun listenStockRestoreRequestedEvent() {
        // given
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val event = StockRestoreRequestedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            occurredAt = LocalDateTime.of(2026, 6, 14, 10, 0),
        )
        val restoreStockUseCase = mockk<RestoreStockUseCase>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.readValue(payload, StockRestoreRequestedEvent::class.java) } returns event
        every {
            restoreStockUseCase.restoreStock(
                RestoreStockCommand(
                    requestId = event.requestId,
                    userId = event.userId,
                    bookId = event.bookId,
                    quantity = event.quantity,
                ),
            )
        } just Runs
        val listener = StockRestoreRequestedKafkaListener(
            restoreStockUseCase = restoreStockUseCase,
            objectMapper = objectMapper,
        )

        // when
        listener.listen(payload)

        // then
        verify {
            restoreStockUseCase.restoreStock(
                RestoreStockCommand(
                    requestId = event.requestId,
                    userId = event.userId,
                    bookId = event.bookId,
                    quantity = event.quantity,
                ),
            )
        }
    }

    @Test
    @DisplayName("재고 복구 요청 Kafka 이벤트를 받으면 수신 로그를 남긴다")
    fun logWhenStockRestoreRequestedEventReceived(output: CapturedOutput) {
        // given
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val event = StockRestoreRequestedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            occurredAt = LocalDateTime.of(2026, 6, 14, 10, 0),
        )
        val restoreStockUseCase = mockk<RestoreStockUseCase>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.readValue(payload, StockRestoreRequestedEvent::class.java) } returns event
        every {
            restoreStockUseCase.restoreStock(any())
        } just Runs
        val listener = StockRestoreRequestedKafkaListener(
            restoreStockUseCase = restoreStockUseCase,
            objectMapper = objectMapper,
        )

        // when
        listener.listen(payload)

        // then
        assertContains(output.all, "재고 복구 요청 Kafka 이벤트를 수신했습니다.")
        assertContains(output.all, "requestId=${event.requestId}")
        assertContains(output.all, "userId=${event.userId}")
        assertContains(output.all, "bookId=${event.bookId}")
        assertContains(output.all, "quantity=${event.quantity}")
    }
}
