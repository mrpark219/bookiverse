package me.park.book.domain

class BookNotFoundException(
    message: String,
) : RuntimeException(message)
