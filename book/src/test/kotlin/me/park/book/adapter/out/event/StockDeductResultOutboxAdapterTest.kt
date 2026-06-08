package me.park.book.adapter.out.event

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import me.park.book.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.book.adapter.out.persistence.OutBoxRecord
import me.park.book.adapter.out.persistence.OutBoxRecordStatus
import me.park.book.application.event.StockDeductFailedEvent
import me.park.book.application.event.StockDeductedEvent
import org.junit.jupiter.api.DisplayName
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StockDeductResultOutboxAdapterTest {

    @Test
    @DisplayName("재고 차감 성공 결과 이벤트를 outbox record로 저장한다")
    fun saveStockDeductedEvent() {
        // given
        val event = StockDeductedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            occurredAt = LocalDateTime.of(2026, 6, 8, 10, 0),
        )
        val payload = """{"requestId":"11111111-1111-1111-1111-111111111111"}"""
        val outBoxRecord = slot<OutBoxRecord>()
        val jpaOutBoxRecordRepository = mockk<JpaOutBoxRecordRepository>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.writeValueAsString(event) } returns payload
        every { jpaOutBoxRecordRepository.save(capture(outBoxRecord)) } answers { outBoxRecord.captured }
        val adapter = StockDeductResultOutboxAdapter(
            jpaOutBoxRecordRepository = jpaOutBoxRecordRepository,
            objectMapper = objectMapper,
        )

        // when
        adapter.save(event)

        // then
        assertEquals(event.eventId, outBoxRecord.captured.id)
        assertEquals(StockDeductedEvent.EVENT_TYPE, outBoxRecord.captured.eventType)
        assertEquals(StockDeductedEvent.TOPIC, outBoxRecord.captured.topic)
        assertEquals(event.bookId.toString(), outBoxRecord.captured.messageKey)
        assertEquals(payload, outBoxRecord.captured.payload)
        assertEquals(OutBoxRecordStatus.PENDING, outBoxRecord.captured.status)
        assertNotNull(outBoxRecord.captured.createdAt)
        assertNull(outBoxRecord.captured.publishedAt)
    }

    @Test
    @DisplayName("재고 차감 실패 결과 이벤트를 outbox record로 저장한다")
    fun saveStockDeductFailedEvent() {
        // given
        val event = StockDeductFailedEvent(
            eventId = "33333333-3333-3333-3333-333333333333",
            requestId = "11111111-1111-1111-1111-111111111111",
            userId = 1L,
            bookId = 10L,
            quantity = 1L,
            reason = "차감 가능한 재고가 부족하거나 재고 정보를 찾을 수 없습니다. bookId=10, quantity=1",
            occurredAt = LocalDateTime.of(2026, 6, 8, 10, 0),
        )
        val payload = """{"reason":"failed"}"""
        val outBoxRecord = slot<OutBoxRecord>()
        val jpaOutBoxRecordRepository = mockk<JpaOutBoxRecordRepository>()
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.writeValueAsString(event) } returns payload
        every { jpaOutBoxRecordRepository.save(capture(outBoxRecord)) } answers { outBoxRecord.captured }
        val adapter = StockDeductResultOutboxAdapter(
            jpaOutBoxRecordRepository = jpaOutBoxRecordRepository,
            objectMapper = objectMapper,
        )

        // when
        adapter.save(event)

        // then
        assertEquals(event.eventId, outBoxRecord.captured.id)
        assertEquals(StockDeductFailedEvent.EVENT_TYPE, outBoxRecord.captured.eventType)
        assertEquals(StockDeductFailedEvent.TOPIC, outBoxRecord.captured.topic)
        assertEquals(event.bookId.toString(), outBoxRecord.captured.messageKey)
        assertEquals(payload, outBoxRecord.captured.payload)
        assertEquals(OutBoxRecordStatus.PENDING, outBoxRecord.captured.status)
        assertNotNull(outBoxRecord.captured.createdAt)
        assertNull(outBoxRecord.captured.publishedAt)
    }
}
