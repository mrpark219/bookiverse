package me.park.book.adapter.out.event

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import me.park.book.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.book.adapter.out.persistence.OutBoxRecord
import me.park.book.adapter.out.persistence.OutBoxRecordStatus
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.DisplayName
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OutBoxRecordPublisherTest {

    @Test
    @DisplayName("대기 중인 outbox record를 Kafka로 발행하고 발행 완료 상태로 변경한다")
    fun publishPendingRecords() {
        // given
        val record = OutBoxRecord(
            id = "22222222-2222-2222-2222-222222222222",
            eventType = "StockDeducted",
            topic = "stock-deducted",
            messageKey = "10",
            payload = """{"bookId":10}""",
        )
        val jpaOutBoxRecordRepository = mockk<JpaOutBoxRecordRepository>()
        val producerRecord = slot<ProducerRecord<String, String>>()
        val kafkaTemplate = mockk<KafkaTemplate<String, String>>()
        every {
            jpaOutBoxRecordRepository.findByStatusOrderByCreatedAtAsc(OutBoxRecordStatus.PENDING)
        } returns listOf(record)
        every {
            kafkaTemplate.send(capture(producerRecord))
        } returns CompletableFuture.completedFuture(mockk<SendResult<String, String>>())
        val publisher = OutBoxRecordPublisher(
            jpaOutBoxRecordRepository = jpaOutBoxRecordRepository,
            kafkaTemplate = kafkaTemplate,
        )

        // when
        publisher.publishPendingRecords()

        // then
        assertEquals(record.topic, producerRecord.captured.topic())
        assertEquals(record.messageKey, producerRecord.captured.key())
        assertEquals(record.payload, producerRecord.captured.value())
        assertEquals(OutBoxRecordStatus.PUBLISHED, record.status)
        assertNotNull(record.publishedAt)
    }

    @Test
    @DisplayName("Kafka 발행에 실패하면 outbox record를 대기 상태로 유지한다")
    fun keepPendingWhenPublishFails() {
        // given
        val record = OutBoxRecord(
            id = "22222222-2222-2222-2222-222222222222",
            eventType = "StockDeducted",
            topic = "stock-deducted",
            messageKey = null,
            payload = """{"bookId":10}""",
        )
        val jpaOutBoxRecordRepository = mockk<JpaOutBoxRecordRepository>()
        val producerRecord = slot<ProducerRecord<String, String>>()
        val kafkaTemplate = mockk<KafkaTemplate<String, String>>()
        val failedFuture = CompletableFuture<SendResult<String, String>>()
        failedFuture.completeExceptionally(RuntimeException("kafka unavailable"))
        every {
            jpaOutBoxRecordRepository.findByStatusOrderByCreatedAtAsc(OutBoxRecordStatus.PENDING)
        } returns listOf(record)
        every {
            kafkaTemplate.send(capture(producerRecord))
        } returns failedFuture
        val publisher = OutBoxRecordPublisher(
            jpaOutBoxRecordRepository = jpaOutBoxRecordRepository,
            kafkaTemplate = kafkaTemplate,
        )

        // when
        publisher.publishPendingRecords()

        // then
        assertEquals(record.topic, producerRecord.captured.topic())
        assertEquals(record.messageKey, producerRecord.captured.key())
        assertEquals(record.payload, producerRecord.captured.value())
        assertEquals(OutBoxRecordStatus.PENDING, record.status)
    }
}
