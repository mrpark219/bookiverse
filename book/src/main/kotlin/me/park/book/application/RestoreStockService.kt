package me.park.book.application

import me.park.book.application.command.RestoreStockCommand
import me.park.book.application.port.`in`.RestoreStockUseCase
import me.park.book.application.port.out.InventoryRepository
import me.park.book.application.port.out.StockRestoreRecordRepository
import me.park.book.domain.StockRestoreRecord
import me.park.book.domain.StockRestoreRecordStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class RestoreStockService(
    private val inventoryRepository: InventoryRepository,
    private val stockRestoreRecordRepository: StockRestoreRecordRepository,
) : RestoreStockUseCase {

    @Transactional
    override fun restoreStock(command: RestoreStockCommand) {
        if (stockRestoreRecordRepository.existsByRequestId(command.requestId)) {
            return
        }

        if (command.quantity < 1) {
            saveFailed(command, "복구 수량은 1 이상이어야 합니다. quantity=${command.quantity}")
            return
        }

        if (inventoryRepository.restore(command.bookId, command.quantity)) {
            saveRestored(command)
        } else {
            saveFailed(
                command = command,
                reason = "복구 가능한 재고 정보를 찾을 수 없거나 전체 재고 수량을 초과합니다. bookId=${command.bookId}, quantity=${command.quantity}",
            )
        }
    }

    private fun saveRestored(command: RestoreStockCommand) {
        stockRestoreRecordRepository.save(
            StockRestoreRecord(
                requestId = command.requestId,
                bookId = command.bookId,
                quantity = command.quantity,
                status = StockRestoreRecordStatus.RESTORED,
                processedAt = LocalDateTime.now(),
            ),
        )
    }

    private fun saveFailed(command: RestoreStockCommand, reason: String) {
        stockRestoreRecordRepository.save(
            StockRestoreRecord(
                requestId = command.requestId,
                bookId = command.bookId,
                quantity = command.quantity,
                status = StockRestoreRecordStatus.FAILED,
                failureReason = reason,
                processedAt = LocalDateTime.now(),
            ),
        )
    }
}
