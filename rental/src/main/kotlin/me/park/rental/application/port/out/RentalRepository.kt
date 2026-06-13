package me.park.rental.application.port.out

import me.park.rental.domain.Rental
import java.time.LocalDate

interface RentalRepository {

    fun findByUserId(userId: Long): Rental?

    fun findRentalsHavingOverdueItems(baseDate: LocalDate): List<Rental>

    fun save(rental: Rental): Rental
}
