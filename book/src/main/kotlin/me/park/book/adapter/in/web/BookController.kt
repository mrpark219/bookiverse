package me.park.book.adapter.`in`.web

import me.park.book.adapter.`in`.web.response.BookResponse
import me.park.book.application.port.`in`.GetBookUseCase
import me.park.book.application.query.GetBookQuery
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(
    private val getBookUseCase: GetBookUseCase,
) {

    @GetMapping("/{bookId}")
    fun getBook(
        @PathVariable bookId: Long,
    ): BookResponse {
        val book = getBookUseCase.getBook(GetBookQuery(bookId))

        return BookResponse.from(book)
    }
}
