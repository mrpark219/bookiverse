package me.park.rental.adapter.out.persistence

import me.park.rental.application.port.out.RentalRepository
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItemStatus
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RentalPersistenceAdapter(
    private val jpaRentalRepository: JpaRentalRepository,
) : RentalRepository {

    override fun findByUserId(userId: Long): Rental? {
        return jpaRentalRepository.findByUserId(userId)
    }

    override fun findRentalsHavingOverdueItems(baseDate: LocalDate): List<Rental> {
        return jpaRentalRepository.findDistinctByRentalItemsStatusAndRentalItemsDueDateBefore(
            status = RentalItemStatus.RENTED,
            baseDate = baseDate,
        )
    }

    override fun save(rental: Rental): Rental {
        return jpaRentalRepository.save(rental)
    }
}
