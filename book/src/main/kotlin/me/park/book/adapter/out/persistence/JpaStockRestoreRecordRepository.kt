package me.park.book.adapter.out.persistence

import me.park.book.domain.StockRestoreRecord
import org.springframework.data.jpa.repository.JpaRepository

interface JpaStockRestoreRecordRepository : JpaRepository<StockRestoreRecord, String>
