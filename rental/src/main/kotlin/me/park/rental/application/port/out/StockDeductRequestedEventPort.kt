package me.park.rental.application.port.out

import me.park.rental.application.event.StockDeductRequestedEvent

interface StockDeductRequestedEventPort {

    fun save(event: StockDeductRequestedEvent)
}
