package me.park.user.application.port.out

import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType

interface PointLedgerRepository {

    fun save(ledger: PointLedger): PointLedger

    fun findByUserId(userId: Long): List<PointLedger>

    fun existsByTypeAndReferenceTypeAndReferenceId(
        type: PointLedgerType,
        referenceType: String,
        referenceId: String,
    ): Boolean
}
