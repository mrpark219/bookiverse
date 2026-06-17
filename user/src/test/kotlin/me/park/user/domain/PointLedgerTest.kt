package me.park.user.domain

import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals

class PointLedgerTest {

    @Test
    @DisplayName("포인트 적립 원장을 생성한다")
    fun createEarnLedger() {
        // when
        val ledger = PointLedger.earn(
            userId = 1L,
            amount = 100L,
            balanceAfter = 150L,
            reason = "대출 적립",
            referenceType = "RENTAL",
            referenceId = "rental-1",
        )

        // then
        assertEquals(1L, ledger.userId)
        assertEquals(PointLedgerType.EARN, ledger.type)
        assertEquals(100L, ledger.amount)
        assertEquals(150L, ledger.balanceAfter)
        assertEquals("대출 적립", ledger.reason)
        assertEquals("RENTAL", ledger.referenceType)
        assertEquals("rental-1", ledger.referenceId)
    }

    @Test
    @DisplayName("포인트 사용 원장을 생성한다")
    fun createUseLedger() {
        // when
        val ledger = PointLedger.use(
            userId = 1L,
            amount = 40L,
            balanceAfter = 60L,
            reason = "연체료 상환",
            referenceType = null,
            referenceId = null,
        )

        // then
        assertEquals(PointLedgerType.USE, ledger.type)
        assertEquals(40L, ledger.amount)
        assertEquals(60L, ledger.balanceAfter)
        assertEquals("연체료 상환", ledger.reason)
    }
}
