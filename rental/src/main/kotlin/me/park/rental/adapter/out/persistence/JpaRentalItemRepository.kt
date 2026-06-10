package me.park.rental.adapter.out.persistence

import me.park.rental.domain.RentalItem
import org.springframework.data.jpa.repository.JpaRepository

interface JpaRentalItemRepository : JpaRepository<RentalItem, Long> {

    fun findByStockDeductRequestId(stockDeductRequestId: String): RentalItem?
}
