package me.park.rental.application

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.slot
import io.mockk.verify
import me.park.rental.application.command.RentBookCommand
import me.park.rental.application.command.ReturnBookCommand
import me.park.rental.application.event.StockDeductFailedEvent
import me.park.rental.application.event.StockDeductRequestedEvent
import me.park.rental.application.event.StockDeductedEvent
import me.park.rental.application.event.StockRestoreRequestedEvent
import me.park.rental.application.event.PointEarnRequestedEvent
import me.park.rental.application.port.out.BookInfo
import me.park.rental.application.port.out.BookQueryPort
import me.park.rental.application.port.out.PointEarnRequestedEventPort
import me.park.rental.application.port.out.RentalItemRepository
import me.park.rental.application.port.out.RentalRepository
import me.park.rental.application.port.out.StockDeductRequestedEventPort
import me.park.rental.application.port.out.StockRestoreRequestedEventPort
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItem
import me.park.rental.domain.RentalItemStatus
import me.park.rental.domain.RentalStatus
import org.junit.jupiter.api.DisplayName
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RentalServiceTest {

    @Test
    @DisplayName("기존 대출 정보가 있으면 해당 대출 정보로 도서를 대출한다")
    fun rentBookWithExistingRental() {
        // given
        val rental = Rental.create(userId = 1L)
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockDeductRequestedEvent = slot<StockDeductRequestedEvent>()
        every { rentalRepository.findByUserId(1L) } returns rental
        every { bookQueryPort.getBook(10L) } returns BookInfo(
            id = 10L,
            title = "오브젝트",
        )
        every { stockDeductRequestedEventPort.save(capture(stockDeductRequestedEvent)) } just Runs
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        val rentalItem = rentalService.rentBook(
            RentBookCommand(
                userId = 1L,
                bookId = 10L,
            ),
        )

        // then
        assertSame(rental, rentalItem.rental)
        assertEquals(10L, rentalItem.bookId)
        assertEquals("오브젝트", rentalItem.bookTitle)
        UUID.fromString(rentalItem.stockDeductRequestId)
        assertEquals(RentalItemStatus.PENDING, rentalItem.status)
        assertEquals(listOf(rentalItem), rental.rentalItems)
        assertStockDeductRequestedEvent(
            event = stockDeductRequestedEvent.captured,
            requestId = rentalItem.stockDeductRequestId,
        )
    }

    @Test
    @DisplayName("대출 정보가 없으면 새 대출 정보를 저장하고 도서를 대출한다")
    fun rentBookWithNewRental() {
        // given
        val savedRental = slot<Rental>()
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockDeductRequestedEvent = slot<StockDeductRequestedEvent>()
        every { rentalRepository.findByUserId(1L) } returns null
        every { rentalRepository.save(capture(savedRental)) } answers { savedRental.captured }
        every { bookQueryPort.getBook(10L) } returns BookInfo(
            id = 10L,
            title = "오브젝트",
        )
        every { stockDeductRequestedEventPort.save(capture(stockDeductRequestedEvent)) } just Runs
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        val rentalItem = rentalService.rentBook(
            RentBookCommand(
                userId = 1L,
                bookId = 10L,
            ),
        )

        // then
        assertEquals(1L, savedRental.captured.userId)
        assertSame(savedRental.captured, rentalItem.rental)
        assertEquals(10L, rentalItem.bookId)
        assertEquals("오브젝트", rentalItem.bookTitle)
        UUID.fromString(rentalItem.stockDeductRequestId)
        assertEquals(RentalItemStatus.PENDING, rentalItem.status)
        assertEquals(listOf(rentalItem), savedRental.captured.rentalItems)
        assertStockDeductRequestedEvent(
            event = stockDeductRequestedEvent.captured,
            requestId = rentalItem.stockDeductRequestId,
        )
    }

    @Test
    @DisplayName("재고 차감 성공 이벤트를 받으면 대출 항목을 확정한다")
    fun handleStockDeducted() {
        // given
        val requestId = "11111111-1111-1111-1111-111111111111"
        val rental = Rental.create(userId = 1L)
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = requestId,
        )
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        every { rentalItemRepository.findByStockDeductRequestId(requestId) } returns rentalItem
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.handleStockDeducted(
            StockDeductedEvent(
                eventId = "22222222-2222-2222-2222-222222222222",
                requestId = requestId,
                userId = 1L,
                bookId = 10L,
                quantity = 1L,
                occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
            ),
        )

        // then
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
    }

    @Test
    @DisplayName("재고 차감 성공 이벤트를 받으면 대출 확정 포인트 적립 요청 이벤트를 저장한다")
    fun handleStockDeductedSavesPointEarnRequestedEvent() {
        // given
        val requestId = "11111111-1111-1111-1111-111111111111"
        val rental = Rental.create(userId = 1L)
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = requestId,
        )
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>()
        val pointEarnRequestedEvent = slot<PointEarnRequestedEvent>()
        every { rentalItemRepository.findByStockDeductRequestId(requestId) } returns rentalItem
        every { pointEarnRequestedEventPort.save(capture(pointEarnRequestedEvent)) } just Runs
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.handleStockDeducted(
            StockDeductedEvent(
                eventId = "22222222-2222-2222-2222-222222222222",
                requestId = requestId,
                userId = 1L,
                bookId = 10L,
                quantity = 1L,
                occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
            ),
        )

        // then
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
        UUID.fromString(pointEarnRequestedEvent.captured.eventId)
        assertEquals(requestId, pointEarnRequestedEvent.captured.requestId)
        assertEquals(1L, pointEarnRequestedEvent.captured.userId)
        assertEquals(100L, pointEarnRequestedEvent.captured.amount)
        assertEquals("도서 대출 적립", pointEarnRequestedEvent.captured.reason)
        assertEquals("RENTAL", pointEarnRequestedEvent.captured.referenceType)
        assertEquals(requestId, pointEarnRequestedEvent.captured.referenceId)
    }

    @Test
    @DisplayName("대출 확정 포인트보다 연체료가 크면 포인트 적립 요청 없이 연체료를 먼저 상환한다")
    fun handleStockDeductedSettlesLateFeeWithoutPointEarnEvent() {
        // given
        val requestId = "11111111-1111-1111-1111-111111111111"
        val rental = Rental(
            userId = 1L,
            rentalStatus = RentalStatus.RENT_RESTRICTED,
            lateFee = 300L,
        )
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = requestId,
        )
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        every { rentalItemRepository.findByStockDeductRequestId(requestId) } returns rentalItem
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.handleStockDeducted(
            StockDeductedEvent(
                eventId = "22222222-2222-2222-2222-222222222222",
                requestId = requestId,
                userId = 1L,
                bookId = 10L,
                quantity = 1L,
                occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
            ),
        )

        // then
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
        assertEquals(200L, rental.lateFee)
        assertEquals(RentalStatus.RENT_RESTRICTED, rental.rentalStatus)
        verify(exactly = 0) { pointEarnRequestedEventPort.save(any()) }
    }

    @Test
    @DisplayName("대출 확정 포인트가 연체료보다 크면 연체료 상환 후 남은 포인트만 적립 요청한다")
    fun handleStockDeductedEarnsRemainingPointAfterLateFeeSettlement() {
        // given
        val requestId = "11111111-1111-1111-1111-111111111111"
        val rental = Rental(
            userId = 1L,
            rentalStatus = RentalStatus.RENT_RESTRICTED,
            lateFee = 50L,
        )
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = requestId,
        )
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>()
        val pointEarnRequestedEvent = slot<PointEarnRequestedEvent>()
        every { rentalItemRepository.findByStockDeductRequestId(requestId) } returns rentalItem
        every { pointEarnRequestedEventPort.save(capture(pointEarnRequestedEvent)) } just Runs
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.handleStockDeducted(
            StockDeductedEvent(
                eventId = "22222222-2222-2222-2222-222222222222",
                requestId = requestId,
                userId = 1L,
                bookId = 10L,
                quantity = 1L,
                occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
            ),
        )

        // then
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
        assertEquals(0L, rental.lateFee)
        assertEquals(RentalStatus.RENT_AVAILABLE, rental.rentalStatus)
        assertEquals(50L, pointEarnRequestedEvent.captured.amount)
        assertEquals("도서 대출 적립", pointEarnRequestedEvent.captured.reason)
        assertEquals("RENTAL", pointEarnRequestedEvent.captured.referenceType)
        assertEquals(requestId, pointEarnRequestedEvent.captured.referenceId)
    }

    @Test
    @DisplayName("이미 확정된 대출 항목의 재고 차감 성공 이벤트는 포인트를 다시 적립하지 않는다")
    fun doNotEarnPointWhenRentalItemAlreadyConfirmed() {
        // given
        val requestId = "11111111-1111-1111-1111-111111111111"
        val rental = Rental.create(userId = 1L)
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = requestId,
        )
        rentalItem.confirmRent()
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        every { rentalItemRepository.findByStockDeductRequestId(requestId) } returns rentalItem
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.handleStockDeducted(
            StockDeductedEvent(
                eventId = "22222222-2222-2222-2222-222222222222",
                requestId = requestId,
                userId = 1L,
                bookId = 10L,
                quantity = 1L,
                occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
            ),
        )

        // then
        assertEquals(RentalItemStatus.RENTED, rentalItem.status)
        verify(exactly = 0) { pointEarnRequestedEventPort.save(any()) }
    }

    @Test
    @DisplayName("재고 차감 실패 이벤트를 받으면 대출 항목을 실패 처리한다")
    fun handleStockDeductFailed() {
        // given
        val requestId = "11111111-1111-1111-1111-111111111111"
        val rental = Rental.create(userId = 1L)
        val rentalItem = rental.rentBook(
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = requestId,
        )
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        every { rentalItemRepository.findByStockDeductRequestId(requestId) } returns rentalItem
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.handleStockDeductFailed(
            StockDeductFailedEvent(
                eventId = "22222222-2222-2222-2222-222222222222",
                requestId = requestId,
                userId = 1L,
                bookId = 10L,
                quantity = 1L,
                reason = "재고 부족",
                occurredAt = LocalDateTime.of(2026, 6, 9, 10, 0),
            ),
        )

        // then
        assertEquals(RentalItemStatus.FAILED, rentalItem.status)
    }

    @Test
    @DisplayName("반납 예정일이 지난 대출을 연체 처리한다")
    fun markOverdueRentals() {
        // given
        val baseDate = LocalDate.of(2026, 6, 3)
        val rental = Rental.create(userId = 1L)
        val rentalItem = rentalItem(
            rental = rental,
            status = RentalItemStatus.RENTED,
            dueDate = LocalDate.of(2026, 6, 1),
        )
        rental.rentalItems.add(rentalItem)
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        every { rentalRepository.findRentalsHavingOverdueItems(baseDate) } returns listOf(rental)
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        rentalService.markOverdueRentals(baseDate)

        // then
        assertEquals(RentalItemStatus.OVERDUE, rentalItem.status)
        assertEquals(200L, rentalItem.lateFee)
        assertEquals(200L, rental.lateFee)
        assertEquals(RentalStatus.RENT_UNAVAILABLE, rental.rentalStatus)
    }

    @Test
    @DisplayName("도서를 반납하면 재고 복구 요청 이벤트를 저장한다")
    fun returnBookSavesStockRestoreRequestedEvent() {
        // given
        val rental = Rental.create(userId = 1L)
        val rentalItem = rentalItem(
            rental = rental,
            status = RentalItemStatus.RENTED,
            dueDate = LocalDate.of(2026, 6, 30),
        )
        rental.rentalItems.add(rentalItem)
        val rentalRepository = mockk<RentalRepository>()
        val rentalItemRepository = mockk<RentalItemRepository>()
        val bookQueryPort = mockk<BookQueryPort>()
        val stockDeductRequestedEventPort = mockk<StockDeductRequestedEventPort>()
        val stockRestoreRequestedEventPort = mockk<StockRestoreRequestedEventPort>()
        val stockRestoreRequestedEvent = slot<StockRestoreRequestedEvent>()
        val pointEarnRequestedEventPort = mockk<PointEarnRequestedEventPort>(relaxed = true)
        every { rentalRepository.findByUserId(1L) } returns rental
        every { stockRestoreRequestedEventPort.save(capture(stockRestoreRequestedEvent)) } just Runs
        val rentalService = RentalService(
            rentalRepository,
            rentalItemRepository,
            bookQueryPort,
            stockDeductRequestedEventPort,
            stockRestoreRequestedEventPort,
            pointEarnRequestedEventPort,
        )

        // when
        val returnedItem = rentalService.returnBook(
            ReturnBookCommand(
                userId = 1L,
                bookId = 10L,
            ),
        )

        // then
        assertSame(rentalItem, returnedItem)
        assertEquals(RentalItemStatus.RETURNED, returnedItem.status)
        assertStockRestoreRequestedEvent(stockRestoreRequestedEvent.captured)
    }

    private fun assertStockDeductRequestedEvent(
        event: StockDeductRequestedEvent,
        requestId: String,
    ) {
        UUID.fromString(event.eventId)
        assertEquals(requestId, event.requestId)
        assertEquals(1L, event.userId)
        assertEquals(10L, event.bookId)
        assertEquals(1L, event.quantity)
    }

    private fun assertStockRestoreRequestedEvent(event: StockRestoreRequestedEvent) {
        UUID.fromString(event.eventId)
        UUID.fromString(event.requestId)
        assertEquals(1L, event.userId)
        assertEquals(10L, event.bookId)
        assertEquals(1L, event.quantity)
    }

    private fun rentalItem(
        rental: Rental,
        status: RentalItemStatus,
        dueDate: LocalDate,
        stockDeductRequestId: String = "11111111-1111-1111-1111-111111111111",
    ): RentalItem {
        return RentalItem(
            rental = rental,
            bookId = 10L,
            bookTitle = "오브젝트",
            stockDeductRequestId = stockDeductRequestId,
            rentedDate = LocalDate.of(2026, 5, 1),
            dueDate = dueDate,
            status = status,
        )
    }
}
