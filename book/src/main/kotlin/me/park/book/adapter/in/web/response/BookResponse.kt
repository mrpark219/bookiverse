package me.park.book.adapter.`in`.web.response

import me.park.book.domain.Book

data class BookResponse(
    val id: Long?,
    val title: String,
    val author: String,
    val description: String?,
) {

    companion object {
        fun from(book: Book): BookResponse {
            return BookResponse(
                id = book.id,
                title = book.title,
                author = book.author,
                description = book.description,
            )
        }
    }
}
