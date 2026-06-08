package me.park.book.application.port.out

import me.park.book.domain.StockDeductRecord

interface StockDeductRecordRepository {

    fun existsByRequestId(requestId: String): Boolean

    fun save(stockDeductRecord: StockDeductRecord): StockDeductRecord
}
