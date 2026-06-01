package me.park.rental.application.port.out

import me.park.rental.domain.Rental

interface RentalRepository {

    fun findByUserId(userId: Long): Rental?

    fun save(rental: Rental): Rental
}
