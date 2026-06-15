package me.park.book.adapter.out.persistence

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.park.book.domain.StockRestoreRecord
import me.park.book.domain.StockRestoreRecordStatus
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StockRestoreRecordPersistenceAdapterTest {

    @Test
    @DisplayName("requestId로 재고 복구 처리 이력 존재 여부를 조회한다")
    fun existsByRequestId() {
        // given
        val jpaStockRestoreRecordRepository = mockk<JpaStockRestoreRecordRepository>()
        every {
            jpaStockRestoreRecordRepository.existsById("11111111-1111-1111-1111-111111111111")
        } returns true
        val adapter = StockRestoreRecordPersistenceAdapter(jpaStockRestoreRecordRepository)

        // when
        val exists = adapter.existsByRequestId("11111111-1111-1111-1111-111111111111")

        // then
        assertTrue(exists)
    }

    @Test
    @DisplayName("requestId가 없으면 재고 복구 처리 이력이 없다고 응답한다")
    fun doesNotExistByRequestId() {
        // given
        val jpaStockRestoreRecordRepository = mockk<JpaStockRestoreRecordRepository>()
        every {
            jpaStockRestoreRecordRepository.existsById("11111111-1111-1111-1111-111111111111")
        } returns false
        val adapter = StockRestoreRecordPersistenceAdapter(jpaStockRestoreRecordRepository)

        // when
        val exists = adapter.existsByRequestId("11111111-1111-1111-1111-111111111111")

        // then
        assertFalse(exists)
    }

    @Test
    @DisplayName("재고 복구 처리 이력을 저장한다")
    fun save() {
        // given
        val record = StockRestoreRecord(
            requestId = "11111111-1111-1111-1111-111111111111",
            bookId = 10L,
            quantity = 1L,
            status = StockRestoreRecordStatus.RESTORED,
        )
        val jpaStockRestoreRecordRepository = mockk<JpaStockRestoreRecordRepository>()
        every { jpaStockRestoreRecordRepository.save(record) } returns record
        val adapter = StockRestoreRecordPersistenceAdapter(jpaStockRestoreRecordRepository)

        // when
        val saved = adapter.save(record)

        // then
        assertSame(record, saved)
        verify { jpaStockRestoreRecordRepository.save(record) }
    }
}
