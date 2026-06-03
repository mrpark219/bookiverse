package me.park.book.application.port.out

import me.park.book.domain.Book

interface BookRepository {

    fun findById(bookId: Long): Book?
}
