package me.park.rental.repository

import me.park.rental.domain.Rental
import org.springframework.data.jpa.repository.JpaRepository

interface RentalRepository : JpaRepository<Rental, Long> {
    fun findByUserId(userId: Long): Rental?
}