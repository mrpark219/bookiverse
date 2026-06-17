package me.park.user.adapter.out.persistence

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.park.user.domain.PointLedger
import kotlin.test.Test
import kotlin.test.assertEquals

class PointLedgerPersistenceAdapterTest {

    @Test
    fun savePointLedger() {
        // given
        val ledger = PointLedger.earn(
            userId = 1L,
            amount = 100L,
            balanceAfter = 100L,
            reason = "대출 적립",
            referenceType = null,
            referenceId = null,
        )
        val jpaPointLedgerRepository = mockk<JpaPointLedgerRepository>()
        every { jpaPointLedgerRepository.save(ledger) } returns ledger
        val adapter = PointLedgerPersistenceAdapter(jpaPointLedgerRepository)

        // when
        val savedLedger = adapter.save(ledger)

        // then
        assertEquals(ledger, savedLedger)
        verify { jpaPointLedgerRepository.save(ledger) }
    }

    @Test
    fun findPointLedgersByUserId() {
        // given
        val ledgers = listOf(
            PointLedger.earn(
                userId = 1L,
                amount = 100L,
                balanceAfter = 100L,
                reason = "대출 적립",
                referenceType = null,
                referenceId = null,
            ),
        )
        val jpaPointLedgerRepository = mockk<JpaPointLedgerRepository>()
        every { jpaPointLedgerRepository.findByUserIdOrderByOccurredAtDesc(1L) } returns ledgers
        val adapter = PointLedgerPersistenceAdapter(jpaPointLedgerRepository)

        // when
        val foundLedgers = adapter.findByUserId(1L)

        // then
        assertEquals(ledgers, foundLedgers)
    }
}
