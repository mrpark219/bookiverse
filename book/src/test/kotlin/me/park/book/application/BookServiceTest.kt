package me.park.book.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.park.book.application.port.out.BookRepository
import me.park.book.application.query.GetBookQuery
import me.park.book.domain.Book
import me.park.book.domain.BookNotFoundException
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class BookServiceTest {

    @Test
    @DisplayName("도서 ID로 도서 정보를 조회한다")
    fun getBook() {
        val book = Book(
            title = "오브젝트",
            author = "조영호",
            description = "객체지향 설계에 관한 책",
        )
        book.id = 10L
        val bookRepository = mockk<BookRepository>()
        every { bookRepository.findById(10L) } returns book
        val bookService = BookService(bookRepository)

        val foundBook = bookService.getBook(GetBookQuery(bookId = 10L))

        verify(exactly = 1) { bookRepository.findById(10L) }
        assertSame(book, foundBook)
    }

    @Test
    @DisplayName("도서 정보가 없으면 예외가 발생한다")
    fun getBookNotFound() {
        val bookRepository = mockk<BookRepository>()
        every { bookRepository.findById(10L) } returns null
        val bookService = BookService(bookRepository)

        val exception = assertFailsWith<BookNotFoundException> {
            bookService.getBook(GetBookQuery(bookId = 10L))
        }

        verify(exactly = 1) { bookRepository.findById(10L) }
        assertEquals("도서 정보를 찾을 수 없습니다. bookId=10", exception.message)
    }
}
