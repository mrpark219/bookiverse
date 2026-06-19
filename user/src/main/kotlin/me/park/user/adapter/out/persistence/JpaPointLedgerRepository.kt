package me.park.user.adapter.out.persistence

import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPointLedgerRepository : JpaRepository<PointLedger, Long> {

    fun findByUserIdOrderByOccurredAtDesc(userId: Long): List<PointLedger>

    fun existsByTypeAndReferenceTypeAndReferenceId(
        type: PointLedgerType,
        referenceType: String,
        referenceId: String,
    ): Boolean
}
