package me.park.rental.adapter.out.event

import me.park.rental.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.rental.adapter.out.persistence.OutBoxRecord
import me.park.rental.application.event.StockRestoreRequestedEvent
import me.park.rental.application.port.out.StockRestoreRequestedEventPort
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockRestoreRequestedOutboxAdapter(
    private val jpaOutBoxRecordRepository: JpaOutBoxRecordRepository,
    private val objectMapper: ObjectMapper,
) : StockRestoreRequestedEventPort {

    override fun save(event: StockRestoreRequestedEvent) {
        jpaOutBoxRecordRepository.save(
            OutBoxRecord(
                id = event.eventId,
                eventType = StockRestoreRequestedEvent.EVENT_TYPE,
                topic = StockRestoreRequestedEvent.TOPIC,
                messageKey = event.bookId.toString(),
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }
}
