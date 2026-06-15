package me.park.book.adapter.out.persistence

import me.park.book.application.port.out.StockRestoreRecordRepository
import me.park.book.domain.StockRestoreRecord
import org.springframework.stereotype.Repository

@Repository
class StockRestoreRecordPersistenceAdapter(
    private val jpaStockRestoreRecordRepository: JpaStockRestoreRecordRepository,
) : StockRestoreRecordRepository {

    override fun existsByRequestId(requestId: String): Boolean {
        return jpaStockRestoreRecordRepository.existsById(requestId)
    }

    override fun save(record: StockRestoreRecord): StockRestoreRecord {
        return jpaStockRestoreRecordRepository.save(record)
    }
}
