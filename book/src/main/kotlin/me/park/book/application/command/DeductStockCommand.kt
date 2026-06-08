package me.park.book.application.command

data class DeductStockCommand(
    val requestId: String,
    val userId: Long,
    val bookId: Long,
    val quantity: Long,
)
