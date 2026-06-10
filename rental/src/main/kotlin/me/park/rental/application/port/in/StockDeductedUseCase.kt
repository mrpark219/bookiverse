package me.park.rental.application.port.`in`

import me.park.rental.application.event.StockDeductedEvent

interface StockDeductedUseCase {

    fun handleStockDeducted(event: StockDeductedEvent)
}
