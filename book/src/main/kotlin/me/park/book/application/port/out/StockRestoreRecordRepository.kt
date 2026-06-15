package me.park.book.application.port.out

import me.park.book.domain.StockRestoreRecord

interface StockRestoreRecordRepository {

    fun existsByRequestId(requestId: String): Boolean

    fun save(record: StockRestoreRecord): StockRestoreRecord
}
