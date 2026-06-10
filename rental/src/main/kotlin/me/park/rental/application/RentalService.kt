package me.park.rental.application

import me.park.rental.application.command.RentBookCommand
import me.park.rental.application.command.ReturnBookCommand
import me.park.rental.application.event.StockDeductFailedEvent
import me.park.rental.application.event.StockDeductRequestedEvent
import me.park.rental.application.event.StockDeductedEvent
import me.park.rental.application.port.`in`.RentBookUseCase
import me.park.rental.application.port.`in`.ReturnBookUseCase
import me.park.rental.application.port.`in`.StockDeductFailedUseCase
import me.park.rental.application.port.`in`.StockDeductedUseCase
import me.park.rental.application.port.out.BookQueryPort
import me.park.rental.application.port.out.RentalItemRepository
import me.park.rental.application.port.out.RentalRepository
import me.park.rental.application.port.out.StockDeductRequestedEventPort
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItem
import me.park.rental.domain.RentalNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class RentalService(
    private val rentalRepository: RentalRepository,
    private val rentalItemRepository: RentalItemRepository,
    private val bookQueryPort: BookQueryPort,
    private val stockDeductRequestedEventPort: StockDeductRequestedEventPort,
) : RentBookUseCase, ReturnBookUseCase, StockDeductedUseCase, StockDeductFailedUseCase {

    @Transactional
    override fun rentBook(command: RentBookCommand): RentalItem {
        val book = bookQueryPort.getBook(command.bookId)
        val rental = rentalRepository.findByUserId(command.userId)
            ?: rentalRepository.save(Rental.create(command.userId))
        val stockDeductRequestId = UUID.randomUUID().toString()

        val rentalItem = rental.rentBook(
            bookId = book.id,
            bookTitle = book.title,
            stockDeductRequestId = stockDeductRequestId,
        )

        stockDeductRequestedEventPort.save(
            StockDeductRequestedEvent(
                eventId = UUID.randomUUID().toString(),
                requestId = stockDeductRequestId,
                userId = command.userId,
                bookId = book.id,
                quantity = 1L,
                occurredAt = LocalDateTime.now(),
            ),
        )

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

    @Transactional
    override fun handleStockDeducted(event: StockDeductedEvent) {
        val rentalItem = rentalItemRepository.findByStockDeductRequestId(event.requestId)
            ?: throw RentalNotFoundException("대출 항목을 찾을 수 없습니다. stockDeductRequestId=${event.requestId}")

        rentalItem.confirmRent()
    }

    @Transactional
    override fun handleStockDeductFailed(event: StockDeductFailedEvent) {
        val rentalItem = rentalItemRepository.findByStockDeductRequestId(event.requestId)
            ?: throw RentalNotFoundException("대출 항목을 찾을 수 없습니다. stockDeductRequestId=${event.requestId}")

        rentalItem.failRent()
    }
}
