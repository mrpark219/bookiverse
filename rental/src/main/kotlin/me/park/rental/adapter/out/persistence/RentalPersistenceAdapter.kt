package me.park.rental.adapter.out.persistence

import me.park.rental.application.port.out.RentalRepository
import me.park.rental.domain.Rental
import org.springframework.stereotype.Repository

@Repository
class RentalPersistenceAdapter(
    private val jpaRentalRepository: JpaRentalRepository,
) : RentalRepository {

    override fun findByUserId(userId: Long): Rental? {
        return jpaRentalRepository.findByUserId(userId)
    }

    override fun save(rental: Rental): Rental {
        return jpaRentalRepository.save(rental)
    }
}
