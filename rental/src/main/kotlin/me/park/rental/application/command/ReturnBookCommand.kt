package me.park.rental.application.command

data class ReturnBookCommand(
    val userId: Long,
    val bookId: Long,
)
