package me.park.book.adapter.out.event

import me.park.book.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.book.adapter.out.persistence.OutBoxRecord
import me.park.book.application.event.StockDeductFailedEvent
import me.park.book.application.event.StockDeductedEvent
import me.park.book.application.port.out.StockDeductResultEventPort
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockDeductResultOutboxAdapter(
    private val jpaOutBoxRecordRepository: JpaOutBoxRecordRepository,
    private val objectMapper: ObjectMapper,
) : StockDeductResultEventPort {

    override fun save(event: StockDeductedEvent) {
        jpaOutBoxRecordRepository.save(
            OutBoxRecord(
                id = event.eventId,
                eventType = StockDeductedEvent.EVENT_TYPE,
                topic = StockDeductedEvent.TOPIC,
                messageKey = event.bookId.toString(),
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }

    override fun save(event: StockDeductFailedEvent) {
        jpaOutBoxRecordRepository.save(
            OutBoxRecord(
                id = event.eventId,
                eventType = StockDeductFailedEvent.EVENT_TYPE,
                topic = StockDeductFailedEvent.TOPIC,
                messageKey = event.bookId.toString(),
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }
}
