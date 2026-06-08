package me.park.rental.adapter.out.event

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import me.park.rental.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.rental.adapter.out.persistence.OutBoxRecord
import me.park.rental.adapter.out.persistence.OutBoxRecordStatus
import me.park.rental.application.event.StockDeductRequestedEvent
import org.junit.jupiter.api.DisplayName
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StockDeductRequestedOutboxAdapterTest {

    @Test
    @DisplayName("재고 차감 요청 이벤트를 outbox 이벤트로 저장한다")
    fun saveStockDeductRequestedEvent() {
        // given
        val event = StockDeductRequestedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            occurredAt = LocalDateTime.of(2026, 6, 6, 12, 0),
        )
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val outBoxRecord = slot<OutBoxRecord>()
        val jpaOutBoxRecordRepository = mockk<JpaOutBoxRecordRepository>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.writeValueAsString(event) } returns payload
        every { jpaOutBoxRecordRepository.save(capture(outBoxRecord)) } answers { outBoxRecord.captured }
        val adapter = StockDeductRequestedOutboxAdapter(
            jpaOutBoxRecordRepository = jpaOutBoxRecordRepository,
            objectMapper = objectMapper,
        )

        // when
        adapter.save(event)

        // then
        assertEquals(event.eventId, outBoxRecord.captured.id)
        assertFalse(OutBoxRecord::class.memberProperties.any { it.name == "aggregateId" })
        assertEquals(StockDeductRequestedEvent.EVENT_TYPE, outBoxRecord.captured.eventType)
        assertEquals(StockDeductRequestedEvent.TOPIC, outBoxRecord.captured.topic)
        assertEquals(event.bookId.toString(), outBoxRecord.captured.messageKey)
        assertEquals(payload, outBoxRecord.captured.payload)
        assertEquals(OutBoxRecordStatus.PENDING, outBoxRecord.captured.status)
        assertNotNull(outBoxRecord.captured.createdAt)
        assertNull(outBoxRecord.captured.publishedAt)
    }
}
