package me.park.user.adapter.out.persistence

import me.park.user.application.port.out.PointLedgerRepository
import me.park.user.domain.PointLedger
import org.springframework.stereotype.Repository

@Repository
class PointLedgerPersistenceAdapter(
    private val jpaPointLedgerRepository: JpaPointLedgerRepository,
) : PointLedgerRepository {

    override fun save(ledger: PointLedger): PointLedger {
        return jpaPointLedgerRepository.save(ledger)
    }

    override fun findByUserId(userId: Long): List<PointLedger> {
        return jpaPointLedgerRepository.findByUserIdOrderByOccurredAtDesc(userId)
    }
}
