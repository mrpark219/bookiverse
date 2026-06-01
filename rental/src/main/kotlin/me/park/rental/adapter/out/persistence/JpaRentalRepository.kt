package me.park.rental.adapter.out.persistence

import me.park.rental.domain.Rental
import org.springframework.data.jpa.repository.JpaRepository

interface JpaRentalRepository : JpaRepository<Rental, Long> {

    fun findByUserId(userId: Long): Rental?
}
