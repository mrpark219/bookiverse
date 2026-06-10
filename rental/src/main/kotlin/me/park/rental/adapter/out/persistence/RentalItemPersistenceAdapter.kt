package me.park.rental.adapter.out.persistence

import me.park.rental.application.port.out.RentalItemRepository
import me.park.rental.domain.RentalItem
import org.springframework.stereotype.Repository

@Repository
class RentalItemPersistenceAdapter(
    private val jpaRentalItemRepository: JpaRentalItemRepository,
) : RentalItemRepository {

    override fun findByStockDeductRequestId(stockDeductRequestId: String): RentalItem? {
        return jpaRentalItemRepository.findByStockDeductRequestId(stockDeductRequestId)
    }
}
