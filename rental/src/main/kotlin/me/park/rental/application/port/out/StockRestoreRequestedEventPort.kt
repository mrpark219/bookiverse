package me.park.rental.application.port.out

import me.park.rental.application.event.StockRestoreRequestedEvent

interface StockRestoreRequestedEventPort {

    fun save(event: StockRestoreRequestedEvent)
}
