package me.park.rental.adapter.`in`.event

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.park.rental.application.event.StockDeductFailedEvent
import me.park.rental.application.port.`in`.StockDeductFailedUseCase
import org.junit.jupiter.api.DisplayName
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test

class StockDeductFailedKafkaListenerTest {

    @Test
    @DisplayName("재고 차감 실패 Kafka 메시지를 유스케이스로 전달한다")
    fun listenStockDeductFailedEvent() {
        // given
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val event = StockDeductFailedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            reason = "재고 부족",
            occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
        )
        val stockDeductFailedUseCase = mockk<StockDeductFailedUseCase>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.readValue(payload, StockDeductFailedEvent::class.java) } returns event
        every { stockDeductFailedUseCase.handleStockDeductFailed(event) } just Runs
        val listener = StockDeductFailedKafkaListener(
            stockDeductFailedUseCase = stockDeductFailedUseCase,
            objectMapper = objectMapper,
        )

        // when
        listener.listenFailed(payload)

        // then
        verify { stockDeductFailedUseCase.handleStockDeductFailed(event) }
    }
}
