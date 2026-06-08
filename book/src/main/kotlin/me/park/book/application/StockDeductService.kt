package me.park.book.application

import me.park.book.application.command.DeductStockCommand
import me.park.book.application.event.StockDeductFailedEvent
import me.park.book.application.event.StockDeductedEvent
import me.park.book.application.port.`in`.DeductStockUseCase
import me.park.book.application.port.out.InventoryRepository
import me.park.book.application.port.out.StockDeductRecordRepository
import me.park.book.application.port.out.StockDeductResultEventPort
import me.park.book.domain.StockDeductRecord
import me.park.book.domain.StockDeductRecordStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class StockDeductService(
    private val inventoryRepository: InventoryRepository,
    private val stockDeductRecordRepository: StockDeductRecordRepository,
    private val stockDeductResultEventPort: StockDeductResultEventPort,
) : DeductStockUseCase {

    @Transactional
    override fun deductStock(command: DeductStockCommand) {
        if (stockDeductRecordRepository.existsByRequestId(command.requestId)) {
            return
        }

        if (command.quantity < 1) {
            saveFailed(command, "차감 수량은 1 이상이어야 합니다. quantity=${command.quantity}")
            return
        }

        if (inventoryRepository.deduct(command.bookId, command.quantity)) {
            saveDeducted(command)
        } else {
            saveFailed(
                command = command,
                reason = "차감 가능한 재고가 부족하거나 재고 정보를 찾을 수 없습니다. bookId=${command.bookId}, quantity=${command.quantity}",
            )
        }
    }

    private fun saveDeducted(command: DeductStockCommand) {
        val occurredAt = LocalDateTime.now()
        stockDeductRecordRepository.save(
            StockDeductRecord(
                requestId = command.requestId,
                bookId = command.bookId,
                quantity = command.quantity,
                status = StockDeductRecordStatus.DEDUCTED,
                processedAt = occurredAt,
            ),
        )
        stockDeductResultEventPort.save(
            StockDeductedEvent(
                eventId = UUID.randomUUID().toString(),
                requestId = command.requestId,
                userId = command.userId,
                bookId = command.bookId,
                quantity = command.quantity,
                occurredAt = occurredAt,
            ),
        )
    }

    private fun saveFailed(command: DeductStockCommand, reason: String) {
        val occurredAt = LocalDateTime.now()
        stockDeductRecordRepository.save(
            StockDeductRecord(
                requestId = command.requestId,
                bookId = command.bookId,
                quantity = command.quantity,
                status = StockDeductRecordStatus.FAILED,
                failureReason = reason,
                processedAt = occurredAt,
            ),
        )
        stockDeductResultEventPort.save(
            StockDeductFailedEvent(
                eventId = UUID.randomUUID().toString(),
                requestId = command.requestId,
                userId = command.userId,
                bookId = command.bookId,
                quantity = command.quantity,
                reason = reason,
                occurredAt = occurredAt,
            ),
        )
    }
}
