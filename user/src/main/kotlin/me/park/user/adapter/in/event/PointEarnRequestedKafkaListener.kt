package me.park.user.adapter.`in`.event

import me.park.user.application.command.EarnPointCommand
import me.park.user.application.event.PointEarnRequestedEvent
import me.park.user.application.port.`in`.PointUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class PointEarnRequestedKafkaListener(
    private val pointUseCase: PointUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [PointEarnRequestedEvent.TOPIC],
        groupId = "\${user.kafka.consumer.point-earn.group-id:user}",
    )
    fun listen(payload: String) {
        val event = objectMapper.readValue(payload, PointEarnRequestedEvent::class.java)
        log.info(
            "포인트 적립 요청 Kafka 이벤트를 수신했습니다. requestId={}, userId={}, amount={}",
            event.requestId,
            event.userId,
            event.amount,
        )
        pointUseCase.earnPoint(
            EarnPointCommand(
                userId = event.userId,
                amount = event.amount,
                reason = event.reason,
                referenceType = event.referenceType,
                referenceId = event.referenceId,
            ),
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(PointEarnRequestedKafkaListener::class.java)
    }
}
