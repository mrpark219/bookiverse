package me.park.rental.adapter.`in`.web.response

import me.park.rental.domain.RentalItem
import me.park.rental.domain.RentalItemStatus
import java.time.LocalDate

data class RentalItemResponse(
    val bookId: Long,
    val bookTitle: String,
    val rentedDate: LocalDate,
    val dueDate: LocalDate,
    val returnedDate: LocalDate?,
    val status: RentalItemStatus,
    val lateFee: Long,
) {

    companion object {
        fun from(rentalItem: RentalItem): RentalItemResponse {
            return RentalItemResponse(
                bookId = rentalItem.bookId,
                bookTitle = rentalItem.bookTitle,
                rentedDate = rentalItem.rentedDate,
                dueDate = rentalItem.dueDate,
                returnedDate = rentalItem.returnedDate,
                status = rentalItem.status,
                lateFee = rentalItem.lateFee,
            )
        }
    }
}
