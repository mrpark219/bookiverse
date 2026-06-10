package me.park.rental.application.port.out

import me.park.rental.domain.RentalItem

interface RentalItemRepository {

    fun findByStockDeductRequestId(stockDeductRequestId: String): RentalItem?
}
