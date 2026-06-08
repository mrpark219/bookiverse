package me.park.book.application

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.park.book.application.command.DeductStockCommand
import me.park.book.application.event.StockDeductFailedEvent
import me.park.book.application.event.StockDeductedEvent
import me.park.book.application.port.out.InventoryRepository
import me.park.book.application.port.out.StockDeductRecordRepository
import me.park.book.application.port.out.StockDeductResultEventPort
import me.park.book.domain.StockDeductRecord
import me.park.book.domain.StockDeductRecordStatus
import org.junit.jupiter.api.DisplayName
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StockDeductServiceTest {

    @Test
    @DisplayName("재고 차감 요청이 성공하면 재고를 차감하고 성공 결과 이벤트를 저장한다")
    fun deductStock() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockDeductRecordRepository = mockk<StockDeductRecordRepository>()
        val stockDeductResultEventPort = mockk<StockDeductResultEventPort>()
        val stockDeductRecord = slot<StockDeductRecord>()
        val stockDeductedEvent = slot<StockDeductedEvent>()
        every { stockDeductRecordRepository.existsByRequestId(REQUEST_ID) } returns false
        every { inventoryRepository.deduct(bookId = 10L, quantity = 1L) } returns true
        every { stockDeductRecordRepository.save(capture(stockDeductRecord)) } answers { stockDeductRecord.captured }
        every { stockDeductResultEventPort.save(capture(stockDeductedEvent)) } just Runs
        val stockDeductService = StockDeductService(
            inventoryRepository = inventoryRepository,
            stockDeductRecordRepository = stockDeductRecordRepository,
            stockDeductResultEventPort = stockDeductResultEventPort,
        )

        // when
        stockDeductService.deductStock(command())

        // then
        assertEquals(REQUEST_ID, stockDeductRecord.captured.requestId)
        assertEquals(10L, stockDeductRecord.captured.bookId)
        assertEquals(1L, stockDeductRecord.captured.quantity)
        assertEquals(StockDeductRecordStatus.DEDUCTED, stockDeductRecord.captured.status)
        assertEquals(null, stockDeductRecord.captured.failureReason)
        assertNotNull(stockDeductRecord.captured.processedAt)

        UUID.fromString(stockDeductedEvent.captured.eventId)
        assertEquals(REQUEST_ID, stockDeductedEvent.captured.requestId)
        assertEquals(1L, stockDeductedEvent.captured.userId)
        assertEquals(10L, stockDeductedEvent.captured.bookId)
        assertEquals(1L, stockDeductedEvent.captured.quantity)
        assertNotNull(stockDeductedEvent.captured.occurredAt)
    }

    @Test
    @DisplayName("재고 차감 요청이 실패하면 실패 처리 이력과 실패 결과 이벤트를 저장한다")
    fun failWhenStockIsInsufficient() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockDeductRecordRepository = mockk<StockDeductRecordRepository>()
        val stockDeductResultEventPort = mockk<StockDeductResultEventPort>()
        val stockDeductRecord = slot<StockDeductRecord>()
        val stockDeductFailedEvent = slot<StockDeductFailedEvent>()
        every { stockDeductRecordRepository.existsByRequestId(REQUEST_ID) } returns false
        every { inventoryRepository.deduct(bookId = 10L, quantity = 1L) } returns false
        every { stockDeductRecordRepository.save(capture(stockDeductRecord)) } answers { stockDeductRecord.captured }
        every { stockDeductResultEventPort.save(capture(stockDeductFailedEvent)) } just Runs
        val stockDeductService = StockDeductService(
            inventoryRepository = inventoryRepository,
            stockDeductRecordRepository = stockDeductRecordRepository,
            stockDeductResultEventPort = stockDeductResultEventPort,
        )

        // when
        stockDeductService.deductStock(command())

        // then
        val reason = "차감 가능한 재고가 부족하거나 재고 정보를 찾을 수 없습니다. bookId=10, quantity=1"
        assertEquals(REQUEST_ID, stockDeductRecord.captured.requestId)
        assertEquals(StockDeductRecordStatus.FAILED, stockDeductRecord.captured.status)
        assertEquals(reason, stockDeductRecord.captured.failureReason)

        UUID.fromString(stockDeductFailedEvent.captured.eventId)
        assertEquals(REQUEST_ID, stockDeductFailedEvent.captured.requestId)
        assertEquals(1L, stockDeductFailedEvent.captured.userId)
        assertEquals(10L, stockDeductFailedEvent.captured.bookId)
        assertEquals(1L, stockDeductFailedEvent.captured.quantity)
        assertEquals(reason, stockDeductFailedEvent.captured.reason)
        assertNotNull(stockDeductFailedEvent.captured.occurredAt)
    }

    @Test
    @DisplayName("이미 처리한 요청 ID면 재고를 다시 차감하지 않는다")
    fun skipDuplicateRequest() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockDeductRecordRepository = mockk<StockDeductRecordRepository>()
        val stockDeductResultEventPort = mockk<StockDeductResultEventPort>()
        every { stockDeductRecordRepository.existsByRequestId(REQUEST_ID) } returns true
        val stockDeductService = StockDeductService(
            inventoryRepository = inventoryRepository,
            stockDeductRecordRepository = stockDeductRecordRepository,
            stockDeductResultEventPort = stockDeductResultEventPort,
        )

        // when
        stockDeductService.deductStock(command())

        // then
        verify(exactly = 0) { inventoryRepository.deduct(any(), any()) }
        verify(exactly = 0) { stockDeductRecordRepository.save(any()) }
        verify(exactly = 0) { stockDeductResultEventPort.save(any<StockDeductedEvent>()) }
        verify(exactly = 0) { stockDeductResultEventPort.save(any<StockDeductFailedEvent>()) }
    }

    @Test
    @DisplayName("차감 수량이 1보다 작으면 재고를 차감하지 않고 실패 결과 이벤트를 저장한다")
    fun failWhenQuantityIsInvalid() {
        // given
        val inventoryRepository = mockk<InventoryRepository>()
        val stockDeductRecordRepository = mockk<StockDeductRecordRepository>()
        val stockDeductResultEventPort = mockk<StockDeductResultEventPort>()
        val stockDeductRecord = slot<StockDeductRecord>()
        val stockDeductFailedEvent = slot<StockDeductFailedEvent>()
        every { stockDeductRecordRepository.existsByRequestId(REQUEST_ID) } returns false
        every { stockDeductRecordRepository.save(capture(stockDeductRecord)) } answers { stockDeductRecord.captured }
        every { stockDeductResultEventPort.save(capture(stockDeductFailedEvent)) } just Runs
        val stockDeductService = StockDeductService(
            inventoryRepository = inventoryRepository,
            stockDeductRecordRepository = stockDeductRecordRepository,
            stockDeductResultEventPort = stockDeductResultEventPort,
        )

        // when
        stockDeductService.deductStock(command(quantity = 0L))

        // then
        val reason = "차감 수량은 1 이상이어야 합니다. quantity=0"
        verify(exactly = 0) { inventoryRepository.deduct(any(), any()) }
        assertEquals(StockDeductRecordStatus.FAILED, stockDeductRecord.captured.status)
        assertEquals(reason, stockDeductRecord.captured.failureReason)
        assertEquals(reason, stockDeductFailedEvent.captured.reason)
    }

    private fun command(quantity: Long = 1L): DeductStockCommand {
        return DeductStockCommand(
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
