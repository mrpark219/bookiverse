package me.park.rental.adapter.out.event

import me.park.rental.adapter.out.persistence.JpaOutBoxRecordRepository
import me.park.rental.adapter.out.persistence.OutBoxRecord
import me.park.rental.application.event.PointEarnRequestedEvent
import me.park.rental.application.port.out.PointEarnRequestedEventPort
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class PointEarnRequestedOutboxAdapter(
    private val jpaOutBoxRecordRepository: JpaOutBoxRecordRepository,
    private val objectMapper: ObjectMapper,
) : PointEarnRequestedEventPort {

    override fun save(event: PointEarnRequestedEvent) {
        jpaOutBoxRecordRepository.save(
            OutBoxRecord(
                id = event.eventId,
                eventType = PointEarnRequestedEvent.EVENT_TYPE,
                topic = PointEarnRequestedEvent.TOPIC,
                messageKey = event.userId.toString(),
                payload = objectMapper.writeValueAsString(event),
            ),
        )
    }
}
