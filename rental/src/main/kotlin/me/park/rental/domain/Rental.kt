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
        if (isRentUnavailableBecauseOfOverdue()) {
            throw RentUnavailableException("연체 중인 도서를 반납 후 도서를 대출하실 수 있습니다.")
        }

        val rentalLimit = if (rentalStatus == RentalStatus.RENT_RESTRICTED || lateFee > 0L) {
            RESTRICTED_RENTAL_ITEM_COUNT
        } else {
            MAX_RENTAL_ITEM_COUNT
        }
        if (rentalItems.count { it.countsTowardRentalLimit() } >= rentalLimit) {
            val message = if (rentalLimit == RESTRICTED_RENTAL_ITEM_COUNT) {
                "연체료 정산 중에는 1권만 대출할 수 있습니다."
            } else {
                "대출 가능한 도서의 수는 ${MAX_RENTAL_ITEM_COUNT}권 입니다."
            }
            throw RentUnavailableException(message)
        }
    }

    private fun isRentUnavailableBecauseOfOverdue(): Boolean {
        return rentalStatus == RentalStatus.RENT_UNAVAILABLE || rentalItems.any { it.status == RentalItemStatus.OVERDUE }
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

    fun markOverdueItems(
        baseDate: LocalDate,
        lateFeePolicy: LateFeePolicy = LateFeePolicy.DEFAULT,
    ) {
        val lateFeeDelta = rentalItems.sumOf {
            it.markOverdue(
                baseDate = baseDate,
                lateFeePolicy = lateFeePolicy,
            )
        }
        lateFee += lateFeeDelta
        refreshRentalStatus()
    }

    fun returnBook(
        bookId: Long,
        returnedDate: LocalDate = LocalDate.now(),
        lateFeePolicy: LateFeePolicy = LateFeePolicy.DEFAULT,
    ): RentalItem {
        val rentalItem = rentalItems.first {
            it.bookId == bookId && it.isCurrentlyRented()
        }

        lateFee += rentalItem.returnBook(
            returnedDate = returnedDate,
            lateFeePolicy = lateFeePolicy,
        )
        refreshRentalStatus()

        return rentalItem
    }

    fun settleLateFeeWithPoint(point: Long): Long {
        require(point >= 0) { "정산 포인트는 0 이상이어야 합니다. point=$point" }

        if (lateFee == 0L) {
            return point
        }

        val paidPoint = minOf(lateFee, point)
        lateFee -= paidPoint
        refreshRentalStatus()

        return point - paidPoint
    }

    private fun refreshRentalStatus() {
        rentalStatus = when {
            hasUnreturnedOverdueItem() -> RentalStatus.RENT_UNAVAILABLE
            lateFee > 0L -> RentalStatus.RENT_RESTRICTED
            else -> RentalStatus.RENT_AVAILABLE
        }
    }

    private fun hasUnreturnedOverdueItem(): Boolean {
        return rentalItems.any { it.status == RentalItemStatus.OVERDUE }
    }

    companion object {
        internal const val MAX_RENTAL_ITEM_COUNT = 5
        internal const val RESTRICTED_RENTAL_ITEM_COUNT = 1
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
