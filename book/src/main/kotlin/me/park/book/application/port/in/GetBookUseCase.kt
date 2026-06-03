package me.park.book.application.port.`in`

import me.park.book.application.query.GetBookQuery
import me.park.book.domain.Book

interface GetBookUseCase {

    fun getBook(query: GetBookQuery): Book
}
