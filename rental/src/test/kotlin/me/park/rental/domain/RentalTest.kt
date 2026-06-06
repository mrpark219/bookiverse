package me.park.rental.domain

import org.junit.jupiter.api.DisplayName
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

class RentalTest {

    @Test
    @DisplayName("사용자 ID로 대출을 생성한다")
    fun createRentalByUserId() {
        // when
        val rental = Rental.create(userId = 1L)

        // then
        assertEquals(1L, rental.userId)
        assertEquals(RentalStatus.RENT_AVAILABLE, rental.rentalStatus)
        assertEquals(0L, rental.lateFee)
        assertEquals(emptyList(), rental.rentalItems)
    }

    @Test
    @DisplayName("대출 상태가 불가능하면 예외를 던진다")
    fun throwExceptionWhenRentalStatusIsUnavailable() {
        // given
        val rental = Rental(
            userId = 1L,
            rentalStatus = RentalStatus.RENT_UNAVAILABLE,
            lateFee = 0L,
        )

        // when
        val exception = assertFailsWith<RentUnavailableException> {
            rental.checkRentalAvailable()
        }

        // then
        assertEquals("연체 상태입니다. 연체료를 정산 후 도서를 대출하실 수 있습니다.", exception.message)
    }

    @Test
    @DisplayName("대출 중이거나 대기 중인 도서가 5개 이상이면 예외를 던진다")
    fun throwExceptionWhenActiveRentalItemCountIsFive() {
        // given
        val rental = Rental.create(userId = 1L)

        repeat(4) {
            rental.rentalItems.add(
                rentalItem(
                    rental = rental,
                    status = RentalItemStatus.RENTED,
                ),
            )
        }
        rental.rentalItems.add(
            rentalItem(
                rental = rental,
                status = RentalItemStatus.PENDING,
            ),
        )

        // when
        val exception = assertFailsWith<RentUnavailableException> {
            rental.checkRentalAvailable()
        }

        // then
        assertEquals("대출 가능한 도서의 수는 ${Rental.MAX_RENTAL_ITEM_COUNT}권 입니다.", exception.message)
    }

    @Test
    @DisplayName("반납된 도서는 대출 중인 도서 개수에서 제외한다")
    fun excludeReturnedItemsFromRentedItemCount() {
        // given
        val rental = Rental.create(userId = 1L)

        repeat(4) {
            rental.rentalItems.add(
                rentalItem(
                    rental = rental,
                    status = RentalItemStatus.RENTED,
                ),
            )
        }
        rental.rentalItems.add(
            rentalItem(
                rental = rental,
                status = RentalItemStatus.RETURNED,
            ),
        )

        // when
        rental.checkRentalAvailable()
    }

    @Test
    @DisplayName("실패한 도서는 대출 중인 도서 개수에서 제외한다")
    fun excludeFailedItemsFromRentedItemCount() {
        // given
        val rental = Rental.create(userId = 1L)

        repeat(4) {
            rental.rentalItems.add(
                rentalItem(
                    rental = rental,
                    status = RentalItemStatus.RENTED,
                ),
            )
        }
        rental.rentalItems.add(
            rentalItem(
                rental = rental,
                status = RentalItemStatus.FAILED,
            ),
        )

        // when
        rental.checkRentalAvailable()
    }

    @Test
    @DisplayName("도서 대출 요청을 대기 상태로 접수한다")
    fun rentBook() {
        // given
        val rental = Rental.create(userId = 1L)
        val today = LocalDate.now()
        val stockDeductRequestId = "11111111-1111-1111-1111-111111111111"

        // when
        val rentalItem = rental.rentBook(
            bookId = 1L,
            bookTitle = "오브젝트",
            stockDeductRequestId = stockDeductRequestId,
        )

        // then
        assertEquals(1L, rentalItem.bookId)
        assertEquals("오브젝트", rentalItem.bookTitle)
        assertEquals(stockDeductRequestId, rentalItem.stockDeductRequestId)
        assertEquals(RentalItemStatus.PENDING, rentalItem.status)
        assertEquals(today, rentalItem.rentedDate)
        assertEquals(today.plusDays(Rental.RENTAL_PERIOD_DAYS), rentalItem.dueDate)
        assertNull(rentalItem.returnedDate)
        assertEquals(listOf(rentalItem), rental.rentalItems)
    }

    @Test
    @DisplayName("대출 가능 여부를 확인한 후 도서를 대출한다")
    fun checkRentalAvailableBeforeRentBook() {
        // given
        val rental = Rental(
            userId = 1L,
            rentalStatus = RentalStatus.RENT_UNAVAILABLE,
            lateFee = 0L,
        )

        // when
        assertFailsWith<RentUnavailableException> {
            rental.rentBook(
                bookId = 1L,
                bookTitle = "오브젝트",
                stockDeductRequestId = "11111111-1111-1111-1111-111111111111",
            )
        }

        // then
        assertEquals(emptyList(), rental.rentalItems)
    }

    @Test
    @DisplayName("도서를 반납한다")
    fun returnBook() {
        // given
        val rental = Rental.create(userId = 1L)
        val rentalItem = rentalItem(
            rental = rental,
            status = RentalItemStatus.RENTED,
        )
        rental.rentalItems.add(rentalItem)
        val today = LocalDate.now()

        // when
        rental.returnBook(bookId = 1L)

        // then
        assertEquals(RentalItemStatus.RETURNED, rentalItem.status)
        assertEquals(today, rentalItem.returnedDate)
        assertFalse(rentalItem.isCurrentlyRented())
    }

    private fun rentalItem(
        rental: Rental,
        status: RentalItemStatus,
    ): RentalItem {
        return RentalItem(
            rental = rental,
            bookId = 1L,
            bookTitle = "오브젝트",
            rentedDate = LocalDate.of(2026, 5, 1),
            dueDate = LocalDate.of(2026, 5, 15),
            status = status,
            stockDeductRequestId = "11111111-1111-1111-1111-111111111111",
        )
    }
}
