package me.park.book.application.event

import java.time.LocalDateTime

data class StockRestoreRequestedEvent(
    val eventId: String,
    val requestId: String,
    val userId: Long,
    val bookId: Long,
    val quantity: Long,
    val occurredAt: LocalDateTime,
) {

    companion object {
        const val EVENT_TYPE = "StockRestoreRequested"
        const val TOPIC = "stock-restore-requested"
    }
}
