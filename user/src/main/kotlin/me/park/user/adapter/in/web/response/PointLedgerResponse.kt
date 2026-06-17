package me.park.user.adapter.`in`.web.response

import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType
import java.time.LocalDateTime

data class PointLedgerResponse(
    val id: Long?,
    val userId: Long,
    val type: PointLedgerType,
    val amount: Long,
    val balanceAfter: Long,
    val reason: String,
    val referenceType: String?,
    val referenceId: String?,
    val occurredAt: LocalDateTime,
) {

    companion object {
        fun from(ledger: PointLedger): PointLedgerResponse {
            return PointLedgerResponse(
                id = ledger.id,
                userId = ledger.userId,
                type = ledger.type,
                amount = ledger.amount,
                balanceAfter = ledger.balanceAfter,
                reason = ledger.reason,
                referenceType = ledger.referenceType,
                referenceId = ledger.referenceId,
                occurredAt = ledger.occurredAt,
            )
        }
    }
}
