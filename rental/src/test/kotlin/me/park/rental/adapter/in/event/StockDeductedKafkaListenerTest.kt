package me.park.rental.adapter.`in`.event

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.park.rental.application.event.StockDeductedEvent
import me.park.rental.application.port.`in`.StockDeductedUseCase
import org.junit.jupiter.api.DisplayName
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test

class StockDeductedKafkaListenerTest {

    @Test
    @DisplayName("재고 차감 성공 Kafka 메시지를 유스케이스로 전달한다")
    fun listenStockDeductedEvent() {
        // given
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val event = StockDeductedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
        )
        val stockDeductedUseCase = mockk<StockDeductedUseCase>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.readValue(payload, StockDeductedEvent::class.java) } returns event
        every { stockDeductedUseCase.handleStockDeducted(event) } just Runs
        val listener = StockDeductedKafkaListener(
            stockDeductedUseCase = stockDeductedUseCase,
            objectMapper = objectMapper,
        )

        // when
        listener.listenDeducted(payload)

        // then
        verify { stockDeductedUseCase.handleStockDeducted(event) }
    }
}
