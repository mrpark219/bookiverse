package me.park.book.application.event

import java.time.LocalDateTime

data class StockDeductRequestedEvent(
    val eventId: String,
    val requestId: String,
    val userId: Long,
    val bookId: Long,
    val quantity: Long,
    val occurredAt: LocalDateTime,
) {

    companion object {
        const val EVENT_TYPE = "StockDeductRequested"
        const val TOPIC = "stock-deduct-requested"
    }
}
