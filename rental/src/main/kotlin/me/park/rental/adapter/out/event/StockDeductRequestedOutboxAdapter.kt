package me.park.rental.adapter.out.event

import me.park.rental.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.rental.adapter.out.persistence.OutBoxRecord
import me.park.rental.application.event.StockDeductRequestedEvent
import me.park.rental.application.port.out.StockDeductRequestedEventPort
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockDeductRequestedOutboxAdapter(
    private val jpaOutBoxRecordRepository: JpaOutBoxRecordRepository,
    private val objectMapper: ObjectMapper,
) : StockDeductRequestedEventPort {

    override fun save(event: StockDeductRequestedEvent) {
        jpaOutBoxRecordRepository.save(
            OutBoxRecord(
                id = event.eventId,
                eventType = StockDeductRequestedEvent.EVENT_TYPE,
                topic = StockDeductRequestedEvent.TOPIC,
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }
}
