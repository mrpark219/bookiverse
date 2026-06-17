package me.park.user.application.port.out

import me.park.user.domain.PointLedger

interface PointLedgerRepository {

    fun save(ledger: PointLedger): PointLedger

    fun findByUserId(userId: Long): List<PointLedger>
}
