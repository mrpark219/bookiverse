package me.park.rental.adapter.`in`.web.request

import me.park.rental.application.command.RentBookCommand

data class RentBookRequest(
    val bookId: Long,
    val bookTitle: String,
) {

    fun toCommand(userId: Long): RentBookCommand {
        return RentBookCommand(
            userId = userId,
            bookId = bookId,
            bookTitle = bookTitle,
        )
    }
}
