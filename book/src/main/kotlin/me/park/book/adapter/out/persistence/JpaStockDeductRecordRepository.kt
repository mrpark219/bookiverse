package me.park.book.adapter.out.persistence

import me.park.book.domain.StockDeductRecord
import org.springframework.data.jpa.repository.JpaRepository

interface JpaStockDeductRecordRepository : JpaRepository<StockDeductRecord, String>
