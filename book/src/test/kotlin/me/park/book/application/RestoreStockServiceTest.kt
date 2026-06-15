package me.park.book.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.park.book.application.command.RestoreStockCommand
import me.park.book.application.port.out.InventoryRepository
import me.park.book.application.port.out.StockRestoreRecordRepository
import me.park.book.domain.StockRestoreRecord
import me.park.book.domain.StockRestoreRecordStatus
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RestoreStockServiceTest {

    @Test
    @DisplayName("재고 복구 요청이 성공하면 재고를 복구하고 처리 이력을 저장한다")
    fun restoreStock() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockRestoreRecordRepository = mockk<StockRestoreRecordRepository>()
        val stockRestoreRecord = slot<StockRestoreRecord>()
        every { stockRestoreRecordRepository.existsByRequestId(REQUEST_ID) } returns false
        every { inventoryRepository.restore(bookId = 10L, quantity = 1L) } returns true
        every { stockRestoreRecordRepository.save(capture(stockRestoreRecord)) } answers { stockRestoreRecord.captured }
        val restoreStockService = RestoreStockService(
            inventoryRepository = inventoryRepository,
            stockRestoreRecordRepository = stockRestoreRecordRepository,
        )

        // when
        restoreStockService.restoreStock(command())

        // then
        assertEquals(REQUEST_ID, stockRestoreRecord.captured.requestId)
        assertEquals(10L, stockRestoreRecord.captured.bookId)
        assertEquals(1L, stockRestoreRecord.captured.quantity)
        assertEquals(StockRestoreRecordStatus.RESTORED, stockRestoreRecord.captured.status)
        assertEquals(null, stockRestoreRecord.captured.failureReason)
        assertNotNull(stockRestoreRecord.captured.processedAt)
    }

    @Test
    @DisplayName("이미 처리한 복구 요청 ID면 재고를 다시 복구하지 않는다")
    fun skipDuplicateRequest() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockRestoreRecordRepository = mockk<StockRestoreRecordRepository>()
        every { stockRestoreRecordRepository.existsByRequestId(REQUEST_ID) } returns true
        val restoreStockService = RestoreStockService(
            inventoryRepository = inventoryRepository,
            stockRestoreRecordRepository = stockRestoreRecordRepository,
        )

        // when
        restoreStockService.restoreStock(command())

        // then
        verify(exactly = 0) { inventoryRepository.restore(any(), any()) }
        verify(exactly = 0) { stockRestoreRecordRepository.save(any()) }
    }

    @Test
    @DisplayName("복구 수량이 1보다 작으면 재고를 복구하지 않고 실패 이력을 저장한다")
    fun failWhenQuantityIsInvalid() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockRestoreRecordRepository = mockk<StockRestoreRecordRepository>()
        val stockRestoreRecord = slot<StockRestoreRecord>()
        every { stockRestoreRecordRepository.existsByRequestId(REQUEST_ID) } returns false
        every { stockRestoreRecordRepository.save(capture(stockRestoreRecord)) } answers { stockRestoreRecord.captured }
        val restoreStockService = RestoreStockService(
            inventoryRepository = inventoryRepository,
            stockRestoreRecordRepository = stockRestoreRecordRepository,
        )

        // when
        restoreStockService.restoreStock(command(quantity = 0L))

        // then
        val reason = "복구 수량은 1 이상이어야 합니다. quantity=0"
        verify(exactly = 0) { inventoryRepository.restore(any(), any()) }
        assertEquals(StockRestoreRecordStatus.FAILED, stockRestoreRecord.captured.status)
        assertEquals(reason, stockRestoreRecord.captured.failureReason)
    }

    @Test
    @DisplayName("재고 복구 요청이 실패하면 실패 이력을 저장한다")
    fun failWhenInventoryCannotBeRestored() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockRestoreRecordRepository = mockk<StockRestoreRecordRepository>()
        val stockRestoreRecord = slot<StockRestoreRecord>()
        every { stockRestoreRecordRepository.existsByRequestId(REQUEST_ID) } returns false
        every { inventoryRepository.restore(bookId = 10L, quantity = 1L) } returns false
        every { stockRestoreRecordRepository.save(capture(stockRestoreRecord)) } answers { stockRestoreRecord.captured }
        val restoreStockService = RestoreStockService(
            inventoryRepository = inventoryRepository,
            stockRestoreRecordRepository = stockRestoreRecordRepository,
        )

        // when
        restoreStockService.restoreStock(command())

        // then
        val reason = "복구 가능한 재고 정보를 찾을 수 없거나 전체 재고 수량을 초과합니다. bookId=10, quantity=1"
        assertEquals(StockRestoreRecordStatus.FAILED, stockRestoreRecord.captured.status)
        assertEquals(reason, stockRestoreRecord.captured.failureReason)
    }

    private fun command(quantity: Long = 1L): RestoreStockCommand {
        return RestoreStockCommand(
            requestId = REQUEST_ID,
            userId = 1L,
            bookId = 10L,
            quantity = quantity,
        )
    }

    companion object {
        private const val REQUEST_ID = "11111111-1111-1111-1111-111111111111"
    }
}
