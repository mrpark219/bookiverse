package me.park.rental.application

import me.park.rental.application.command.RentBookCommand
import me.park.rental.application.command.ReturnBookCommand
import me.park.rental.application.port.`in`.RentBookUseCase
import me.park.rental.application.port.`in`.ReturnBookUseCase
import me.park.rental.application.port.out.BookQueryPort
import me.park.rental.application.port.out.RentalRepository
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItem
import me.park.rental.domain.RentalNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RentalService(
    private val rentalRepository: RentalRepository,
    private val bookQueryPort: BookQueryPort,
) : RentBookUseCase, ReturnBookUseCase {

    @Transactional
    override fun rentBook(command: RentBookCommand): RentalItem {
        val book = bookQueryPort.getBook(command.bookId)
        val rental = rentalRepository.findByUserId(command.userId)
            ?: rentalRepository.save(Rental.create(command.userId))

        val rentalItem = rental.rentBook(book.id, book.title)

        // TODO 도서 대출 이벤트 발송

        return rentalItem
    }

    @Transactional
    override fun returnBook(command: ReturnBookCommand): RentalItem {
        val rental = rentalRepository.findByUserId(command.userId)
            ?: throw RentalNotFoundException("대출 정보를 찾을 수 없습니다. userId=${command.userId}")

        val returnItem = rental.returnBook(command.bookId)

        // TODO 도서 반납 이벤트 발송

        return returnItem
    }
}
