package me.park.rental.service

import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItem
import me.park.rental.repository.RentalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RentalService(
    private val rentalRepository: RentalRepository
) {

    @Transactional
    fun rentBook(userId: Long, bookId: Long, bookTitle: String): RentalItem {
        val rental = rentalRepository.findByUserId(userId)
            ?: rentalRepository.save(Rental.create(userId))

        val rentalItem = rental.rentBook(bookId, bookTitle)

        // TODO 도서 대출 이벤트 발송

        return rentalItem
    }
}