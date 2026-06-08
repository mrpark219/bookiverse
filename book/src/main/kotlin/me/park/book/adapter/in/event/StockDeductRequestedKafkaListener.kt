package me.park.book.adapter.`in`.event

import me.park.book.application.command.DeductStockCommand
import me.park.book.application.event.StockDeductRequestedEvent
import me.park.book.application.port.`in`.DeductStockUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockDeductRequestedKafkaListener(
    private val deductStockUseCase: DeductStockUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [StockDeductRequestedEvent.TOPIC],
        groupId = "\${book.kafka.consumer.stock-deduct.group-id:book}",
    )
    fun listen(payload: String) {
        val event = objectMapper.readValue(payload, StockDeductRequestedEvent::class.java)
        log.info(
            "재고 차감 요청 Kafka 이벤트를 수신했습니다. requestId={}, userId={}, bookId={}, quantity={}",
            event.requestId,
            event.userId,
            event.bookId,
            event.quantity,
        )
        deductStockUseCase.deductStock(
            DeductStockCommand(
                requestId = event.requestId,
                userId = event.userId,
                bookId = event.bookId,
                quantity = event.quantity,
            ),
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockDeductRequestedKafkaListener::class.java)
    }
}
