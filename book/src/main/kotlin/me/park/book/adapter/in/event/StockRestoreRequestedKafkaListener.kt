package me.park.book.adapter.`in`.event

import me.park.book.application.command.RestoreStockCommand
import me.park.book.application.event.StockRestoreRequestedEvent
import me.park.book.application.port.`in`.RestoreStockUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class StockRestoreRequestedKafkaListener(
    private val restoreStockUseCase: RestoreStockUseCase,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(
        topics = [StockRestoreRequestedEvent.TOPIC],
        groupId = "\${book.kafka.consumer.stock-restore.group-id:book}",
    )
    fun listen(payload: String) {
        val event = objectMapper.readValue(payload, StockRestoreRequestedEvent::class.java)
        log.info(
            "재고 복구 요청 Kafka 이벤트를 수신했습니다. requestId={}, userId={}, bookId={}, quantity={}",
            event.requestId,
            event.userId,
            event.bookId,
            event.quantity,
        )
        restoreStockUseCase.restoreStock(
            RestoreStockCommand(
                requestId = event.requestId,
                userId = event.userId,
                bookId = event.bookId,
                quantity = event.quantity,
            ),
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(StockRestoreRequestedKafkaListener::class.java)
    }
}
