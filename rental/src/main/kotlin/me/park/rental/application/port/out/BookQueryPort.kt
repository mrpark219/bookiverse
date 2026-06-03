package me.park.rental.application.port.out

interface BookQueryPort {

    fun getBook(bookId: Long): BookInfo
}
