package me.park.book.adapter.out.persistence

import me.park.book.domain.Book
import org.springframework.data.jpa.repository.JpaRepository

interface JpaBookRepository : JpaRepository<Book, Long>
