package me.park.rental.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity
class Rental(
    @Column(nullable = false) var userId: Long,

    @Enumerated(EnumType.STRING) @Column(nullable = false) var rentalStatus: RentalStatus,

    @Column var lateFee: Long,

    @OneToMany(
        mappedBy = "rental",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    ) var rentalItems: MutableList<RentalItem> = mutableListOf(),
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null

    fun checkRentalAvailable() {
        if (rentalStatus == RentalStatus.RENT_UNAVAILABLE) {
            throw RentUnavailableException("연체 상태입니다. 연체료를 정산 후 도서를 대출하실 수 있습니다.")
        }

        if (rentalItems.count { it.countsTowardRentalLimit() } >= MAX_RENTAL_ITEM_COUNT) {
            throw RentUnavailableException("대출 가능한 도서의 수는 ${MAX_RENTAL_ITEM_COUNT}권 입니다.")
        }
    }

    fun rentBook(
        bookId: Long,
        bookTitle: String,
        stockDeductRequestId: String,
    ): RentalItem {
        checkRentalAvailable()

        val rentedDate = LocalDate.now()
        val rentalItem = RentalItem(
            rental = this,
            bookId = bookId,
            bookTitle = bookTitle,
            stockDeductRequestId = stockDeductRequestId,
            rentedDate = rentedDate,
            dueDate = rentedDate.plusDays(RENTAL_PERIOD_DAYS),
            status = RentalItemStatus.PENDING,
        )

        rentalItems.add(rentalItem)

        return rentalItem
    }

    fun returnBook(bookId: Long): RentalItem {
        val rentalItem = rentalItems.first {
            it.bookId == bookId && it.isCurrentlyRented()
        }

        rentalItem.status = RentalItemStatus.RETURNED
        rentalItem.returnedDate = LocalDate.now()

        return rentalItem
    }

    companion object {
        internal const val MAX_RENTAL_ITEM_COUNT = 5
        internal const val RENTAL_PERIOD_DAYS = 14L

        fun create(userId: Long): Rental {
            return Rental(
                userId = userId,
                rentalStatus = RentalStatus.RENT_AVAILABLE,
                lateFee = 0L,
            )
        }
    }
}
