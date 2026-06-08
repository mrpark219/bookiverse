package me.park.book.application.event

import java.time.LocalDateTime

data class StockDeductFailedEvent(
    val eventId: String,
    val requestId: String,
    val userId: Long,
    val bookId: Long,
    val quantity: Long,
    val reason: String,
    val occurredAt: LocalDateTime,
) {

    companion object {
        const val EVENT_TYPE = "StockDeductFailed"
        const val TOPIC = "stock-deduct-failed"
    }
}
