package me.park.rental.adapter.out.persistence

import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItemStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface JpaRentalRepository : JpaRepository<Rental, Long> {

    fun findByUserId(userId: Long): Rental?

    fun findDistinctByRentalItemsStatusAndRentalItemsDueDateBefore(
        status: RentalItemStatus,
        baseDate: LocalDate,
    ): List<Rental>
}
