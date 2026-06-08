package me.park.book.adapter.out.event

import me.park.book.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.book.adapter.out.persistence.OutBoxRecord
import me.park.book.adapter.out.persistence.OutBoxRecordStatus
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutBoxRecordPublisher(
    private val jpaOutBoxRecordRepository: JpaOutBoxRecordRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @Scheduled(fixedDelayString = "\${book.outbox.publisher.fixed-delay-ms:1000}")
    @Transactional
    fun publishPendingRecords() {
        val records = jpaOutBoxRecordRepository.findByStatusOrderByCreatedAtAsc(OutBoxRecordStatus.PENDING)

        records.forEach { record ->
            publish(record)
        }
    }

    private fun publish(record: OutBoxRecord) {
        runCatching {
            logPublishAttempt(record)
            kafkaTemplate.send(record.toProducerRecord()).get()
        }.onSuccess {
            record.markPublished()
            logPublishSuccess(record)
        }.onFailure { exception ->
            logPublishFailure(record, exception)
        }
    }

    private fun OutBoxRecord.toProducerRecord(): ProducerRecord<String, String> {
        return ProducerRecord(topic, messageKey, payload)
    }

    private fun logPublishAttempt(record: OutBoxRecord) {
        log.info(
            "Kafka 메시지 발행을 시도합니다. id={}, eventType={}, topic={}, key={}",
            record.id,
            record.eventType,
            record.topic,
            record.messageKey,
        )
    }

    private fun logPublishSuccess(record: OutBoxRecord) {
        log.info(
            "Kafka 메시지 발행에 성공했습니다. id={}, eventType={}, topic={}, key={}",
            record.id,
            record.eventType,
            record.topic,
            record.messageKey,
        )
    }

    private fun logPublishFailure(record: OutBoxRecord, exception: Throwable) {
        log.warn(
            "Kafka 메시지 발행에 실패했습니다. id={}, eventType={}, topic={}, key={}",
            record.id,
            record.eventType,
            record.topic,
            record.messageKey,
            exception,
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(OutBoxRecordPublisher::class.java)
    }
}
