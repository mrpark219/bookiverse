package me.park.book.application.command

data class RestoreStockCommand(
    val requestId: String,
    val userId: Long,
    val bookId: Long,
    val quantity: Long,
)
