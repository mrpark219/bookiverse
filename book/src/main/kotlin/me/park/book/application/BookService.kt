package me.park.book.application

import me.park.book.application.port.`in`.GetBookUseCase
import me.park.book.application.port.out.BookRepository
import me.park.book.application.query.GetBookQuery
import me.park.book.domain.Book
import me.park.book.domain.BookNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
) : GetBookUseCase {

    @Transactional(readOnly = true)
    override fun getBook(query: GetBookQuery): Book {
        return bookRepository.findById(query.bookId)
            ?: throw BookNotFoundException("도서 정보를 찾을 수 없습니다. bookId=${query.bookId}")
    }
}
