package me.park.rental.adapter.`in`.event

import me.park.rental.application.event.StockDeductFailedEvent
import me.park.rental.application.port.`in`.StockDeductFailedUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockDeductFailedKafkaListener(
    private val stockDeductFailedUseCase: StockDeductFailedUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [StockDeductFailedEvent.TOPIC],
        groupId = "\${rental.kafka.consumer.stock-deduct-result.group-id:rental}",
    )
    fun listenFailed(payload: String) {
        val event = objectMapper.readValue(payload, StockDeductFailedEvent::class.java)
        log.info(
            "재고 차감 실패 Kafka 이벤트를 수신했습니다. requestId={}, userId={}, bookId={}, quantity={}, reason={}",
            event.requestId,
            event.userId,
            event.bookId,
            event.quantity,
            event.reason,
        )
        stockDeductFailedUseCase.handleStockDeductFailed(event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockDeductFailedKafkaListener::class.java)
    }
}
