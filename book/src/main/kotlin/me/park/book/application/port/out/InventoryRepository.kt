package me.park.book.application.port.out

interface InventoryRepository {

    fun deduct(bookId: Long, quantity: Long): Boolean
}
