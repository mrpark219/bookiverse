package me.park.rental.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
class RentalItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    var rental: Rental,

    @Column(nullable = false)
    var bookId: Long,

    @Column(nullable = false)
    var bookTitle: String,

    @Column(nullable = false, unique = true)
    var stockDeductRequestId: String,

    @Column(nullable = false)
    var rentedDate: LocalDate,

    @Column(nullable = false)
    var dueDate: LocalDate,

    @Column
    var returnedDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: RentalItemStatus,

    @Column(nullable = false)
    var lateFee: Long = 0L,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null

    fun isCurrentlyRented(): Boolean {
        return status == RentalItemStatus.RENTED || status == RentalItemStatus.OVERDUE
    }

    fun countsTowardRentalLimit(): Boolean {
        return status == RentalItemStatus.PENDING || isCurrentlyRented()
    }

    fun markOverdue(baseDate: LocalDate, lateFeePolicy: LateFeePolicy): Long {
        if (status != RentalItemStatus.RENTED && status != RentalItemStatus.OVERDUE) {
            return 0L
        }

        val calculatedLateFee = lateFeePolicy.calculate(
            dueDate = dueDate,
            baseDate = baseDate,
        )
        if (calculatedLateFee == 0L) {
            return 0L
        }

        status = RentalItemStatus.OVERDUE
        val previousLateFee = lateFee
        lateFee = maxOf(previousLateFee, calculatedLateFee)

        return lateFee - previousLateFee
    }

    fun returnBook(returnedDate: LocalDate, lateFeePolicy: LateFeePolicy): Long {
        val calculatedLateFee = lateFeePolicy.calculate(
            dueDate = dueDate,
            baseDate = returnedDate,
        )
        val previousLateFee = lateFee
        lateFee = maxOf(previousLateFee, calculatedLateFee)
        status = RentalItemStatus.RETURNED
        this.returnedDate = returnedDate

        return lateFee - previousLateFee
    }

    fun confirmRent() {
        if (status == RentalItemStatus.PENDING) {
            status = RentalItemStatus.RENTED
        }
    }

    fun failRent() {
        if (status == RentalItemStatus.PENDING) {
            status = RentalItemStatus.FAILED
        }
    }
}
