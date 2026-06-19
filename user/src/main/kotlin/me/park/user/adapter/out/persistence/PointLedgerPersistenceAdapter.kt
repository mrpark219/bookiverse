package me.park.user.adapter.out.persistence

import me.park.user.application.port.out.PointLedgerRepository
import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType
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

    override fun existsByTypeAndReferenceTypeAndReferenceId(
        type: PointLedgerType,
        referenceType: String,
        referenceId: String,
    ): Boolean {
        return jpaPointLedgerRepository.existsByTypeAndReferenceTypeAndReferenceId(
            type = type,
            referenceType = referenceType,
            referenceId = referenceId,
        )
    }
}
