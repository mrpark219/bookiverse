package me.park.rental.adapter.`in`.event

import me.park.rental.application.event.StockDeductedEvent
import me.park.rental.application.port.`in`.StockDeductedUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockDeductedKafkaListener(
    private val stockDeductedUseCase: StockDeductedUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [StockDeductedEvent.TOPIC],
        groupId = "\${rental.kafka.consumer.stock-deduct-result.group-id:rental}",
    )
    fun listenDeducted(payload: String) {
        val event = objectMapper.readValue(payload, StockDeductedEvent::class.java)
        log.info(
            "재고 차감 성공 Kafka 이벤트를 수신했습니다. requestId={}, userId={}, bookId={}, quantity={}",
            event.requestId,
            event.userId,
            event.bookId,
            event.quantity,
        )
        stockDeductedUseCase.handleStockDeducted(event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockDeductedKafkaListener::class.java)
    }
}
