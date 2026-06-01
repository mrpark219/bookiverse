package me.park.rental.application.command

data class RentBookCommand(
    val userId: Long,
    val bookId: Long,
    val bookTitle: String,
)
