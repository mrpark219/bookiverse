package me.park.book.adapter.out.persistence

import me.park.book.application.port.out.BookRepository
import me.park.book.domain.Book
import org.springframework.stereotype.Repository

@Repository
class BookPersistenceAdapter(
    private val jpaBookRepository: JpaBookRepository,
) : BookRepository {

    override fun findById(bookId: Long): Book? {
        return jpaBookRepository.findById(bookId).orElse(null)
    }
}
