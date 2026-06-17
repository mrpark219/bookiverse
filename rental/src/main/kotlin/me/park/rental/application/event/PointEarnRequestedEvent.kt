package me.park.rental.application.event

import java.time.LocalDateTime

data class PointEarnRequestedEvent(
    val eventId: String,
    val requestId: String,
    val userId: Long,
    val amount: Long,
    val reason: String,
    val referenceType: String,
    val referenceId: String,
    val occurredAt: LocalDateTime,
) {

    companion object {
        const val EVENT_TYPE = "PointEarnRequested"
        const val TOPIC = "point-earn-requested"
    }
}
