package me.park.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class PointLedger(
    @Column(nullable = false)
    var userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: PointLedgerType,

    @Column(nullable = false)
    var amount: Long,

    @Column(nullable = false)
    var balanceAfter: Long,

    @Column(nullable = false, length = 100)
    var reason: String,

    @Column(length = 100)
    var referenceType: String? = null,

    @Column(length = 100)
    var referenceId: String? = null,

    @Column(nullable = false)
    var occurredAt: LocalDateTime = LocalDateTime.now(),
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null

    companion object {
        fun earn(
            userId: Long,
            amount: Long,
            balanceAfter: Long,
            reason: String,
            referenceType: String?,
            referenceId: String?,
        ): PointLedger {
            return create(
                userId = userId,
                type = PointLedgerType.EARN,
                amount = amount,
                balanceAfter = balanceAfter,
                reason = reason,
                referenceType = referenceType,
                referenceId = referenceId,
            )
        }

        fun use(
            userId: Long,
            amount: Long,
            balanceAfter: Long,
            reason: String,
            referenceType: String?,
            referenceId: String?,
        ): PointLedger {
            return create(
                userId = userId,
                type = PointLedgerType.USE,
                amount = amount,
                balanceAfter = balanceAfter,
                reason = reason,
                referenceType = referenceType,
                referenceId = referenceId,
            )
        }

        private fun create(
            userId: Long,
            type: PointLedgerType,
            amount: Long,
            balanceAfter: Long,
            reason: String,
            referenceType: String?,
            referenceId: String?,
        ): PointLedger {
            require(amount > 0) { "포인트는 1 이상이어야 합니다. amount=$amount" }
            return PointLedger(
                userId = userId,
                type = type,
                amount = amount,
                balanceAfter = balanceAfter,
                reason = reason,
                referenceType = referenceType,
                referenceId = referenceId,
            )
        }
    }
}
