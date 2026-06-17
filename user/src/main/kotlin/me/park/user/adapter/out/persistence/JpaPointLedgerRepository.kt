package me.park.user.adapter.out.persistence

import me.park.user.domain.PointLedger
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPointLedgerRepository : JpaRepository<PointLedger, Long> {

    fun findByUserIdOrderByOccurredAtDesc(userId: Long): List<PointLedger>
}
