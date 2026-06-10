package me.park.rental.application.port.`in`

import me.park.rental.application.event.StockDeductFailedEvent

interface StockDeductFailedUseCase {

    fun handleStockDeductFailed(event: StockDeductFailedEvent)
}
