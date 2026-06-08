package me.park.book.adapter.out.persistence

import me.park.book.application.port.out.StockDeductRecordRepository
import me.park.book.domain.StockDeductRecord
import org.springframework.stereotype.Repository

@Repository
class StockDeductRecordPersistenceAdapter(
    private val jpaStockDeductRecordRepository: JpaStockDeductRecordRepository,
) : StockDeductRecordRepository {

    override fun existsByRequestId(requestId: String): Boolean {
        return jpaStockDeductRecordRepository.existsById(requestId)
    }

    override fun save(stockDeductRecord: StockDeductRecord): StockDeductRecord {
        return jpaStockDeductRecordRepository.save(stockDeductRecord)
    }
}
