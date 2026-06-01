package me.park.rental.adapter.`in`.web

import me.park.rental.adapter.`in`.web.response.ErrorResponse
import me.park.rental.domain.RentUnavailableException
import me.park.rental.domain.RentalNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RentalExceptionHandler {

    @ExceptionHandler(RentUnavailableException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleRentUnavailableException(exception: RentUnavailableException): ErrorResponse {
        return ErrorResponse(exception.message)
    }

    @ExceptionHandler(RentalNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleRentalNotFoundException(exception: RentalNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message)
    }
}
