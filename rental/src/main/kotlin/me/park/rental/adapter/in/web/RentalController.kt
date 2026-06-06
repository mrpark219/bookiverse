package me.park.rental.adapter.`in`.web

import me.park.rental.adapter.`in`.web.response.RentBookResponse
import me.park.rental.adapter.`in`.web.response.RentalItemResponse
import me.park.rental.application.command.RentBookCommand
import me.park.rental.application.command.ReturnBookCommand
import me.park.rental.application.port.`in`.RentBookUseCase
import me.park.rental.application.port.`in`.ReturnBookUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rentals")
class RentalController(
    private val rentBookUseCase: RentBookUseCase,
    private val returnBookUseCase: ReturnBookUseCase,
) {

    @PostMapping("/{userId}/books/{bookId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun rentBook(
        @PathVariable userId: Long,
        @PathVariable bookId: Long,
    ): RentBookResponse {
        val rentalItem = rentBookUseCase.rentBook(
            RentBookCommand(
                userId = userId,
                bookId = bookId,
            ),
        )

        return RentBookResponse.from(rentalItem)
    }

    @PostMapping("/{userId}/items/{bookId}/return")
    fun returnBook(
        @PathVariable userId: Long,
        @PathVariable bookId: Long,
    ): RentalItemResponse {
        val rentalItem = returnBookUseCase.returnBook(
            ReturnBookCommand(
                userId = userId,
                bookId = bookId,
            ),
        )

        return RentalItemResponse.from(rentalItem)
    }
}
