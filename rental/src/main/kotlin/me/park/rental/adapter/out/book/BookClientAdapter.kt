package me.park.rental.adapter.out.book

import me.park.rental.application.port.out.BookInfo
import me.park.rental.application.port.out.BookQueryPort
import org.springframework.stereotype.Component

@Component
class BookClientAdapter(
    private val bookFeignClient: BookFeignClient,
) : BookQueryPort {

    override fun getBook(bookId: Long): BookInfo {
        val response = bookFeignClient.getBook(bookId)

        return BookInfo(
            id = response.id ?: bookId,
            title = response.title,
        )
    }
}
