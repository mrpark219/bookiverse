package me.park.book.application.port.out

import me.park.book.application.event.StockDeductFailedEvent
import me.park.book.application.event.StockDeductedEvent

interface StockDeductResultEventPort {

    fun save(event: StockDeductedEvent)

    fun save(event: StockDeductFailedEvent)
}
