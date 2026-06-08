package me.park.book.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "stock_deduct_records")
class StockDeductRecord(
    @Id
    @Column(nullable = false, length = 100)
    var requestId: String,

    @Column(nullable = false)
    var bookId: Long,

    @Column(nullable = false)
    var quantity: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: StockDeductRecordStatus,

    @Column(length = 500)
    var failureReason: String? = null,

    @Column(nullable = false)
    var processedAt: LocalDateTime = LocalDateTime.now(),
)
