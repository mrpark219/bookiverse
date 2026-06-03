package me.park.rental.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.park.rental.application.command.RentBookCommand
import me.park.rental.application.port.out.BookInfo
import me.park.rental.application.port.out.BookQueryPort
import me.park.rental.application.port.out.RentalRepository
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItemStatus
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RentalServiceTest {

    @Test
    @DisplayName("기존 대출 정보가 있으면 해당 대출 정보로 도서를 대출한다")
    fun rentBookWithExistingRental() {
        val rental = Rental.create(userId = 1L)
        val rentalRepository = mockk<RentalRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        every { rentalRepository.findByUserId(1L) } returns rental
        every { bookQueryPort.getBook(10L) } returns BookInfo(
            id = 10L,
            title = "오브젝트",
        )
        val rentalService = RentalService(rentalRepository, bookQueryPort)

        val rentalItem = rentalService.rentBook(
            RentBookCommand(
                userId = 1L,
                bookId = 10L,
            ),
        )

        verify(exactly = 1) { bookQueryPort.getBook(10L) }
        verify(exactly = 1) { rentalRepository.findByUserId(1L) }
        verify(exactly = 0) { rentalRepository.save(any<Rental>()) }
        assertSame(rental, rentalItem.rental)
        assertEquals(10L, rentalItem.bookId)
        assertEquals("오브젝트", rentalItem.bookTitle)
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
        assertEquals(listOf(rentalItem), rental.rentalItems)
    }

    @Test
    @DisplayName("대출 정보가 없으면 새 대출 정보를 저장하고 도서를 대출한다")
    fun rentBookWithNewRental() {
        val savedRental = slot<Rental>()
        val rentalRepository = mockk<RentalRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        every { rentalRepository.findByUserId(1L) } returns null
        every { rentalRepository.save(capture(savedRental)) } answers { savedRental.captured }
        every { bookQueryPort.getBook(10L) } returns BookInfo(
            id = 10L,
            title = "오브젝트",
        )
        val rentalService = RentalService(rentalRepository, bookQueryPort)

        val rentalItem = rentalService.rentBook(
            RentBookCommand(
                userId = 1L,
                bookId = 10L,
            ),
        )

        verify(exactly = 1) { bookQueryPort.getBook(10L) }
        verify(exactly = 1) { rentalRepository.findByUserId(1L) }
        verify(exactly = 1) { rentalRepository.save(any<Rental>()) }
        assertEquals(1L, savedRental.captured.userId)
        assertSame(savedRental.captured, rentalItem.rental)
        assertEquals(10L, rentalItem.bookId)
        assertEquals("오브젝트", rentalItem.bookTitle)
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
        assertEquals(listOf(rentalItem), savedRental.captured.rentalItems)
    }
}
