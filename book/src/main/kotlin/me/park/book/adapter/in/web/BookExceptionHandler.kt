package me.park.book.adapter.`in`.web

import me.park.book.adapter.`in`.web.response.ErrorResponse
import me.park.book.domain.BookNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BookExceptionHandler {

    @ExceptionHandler(BookNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleBookNotFoundException(exception: BookNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message)
    }
}
