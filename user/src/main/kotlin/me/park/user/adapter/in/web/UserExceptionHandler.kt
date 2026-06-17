package me.park.user.adapter.`in`.web

import me.park.user.adapter.`in`.web.response.ErrorResponse
import me.park.user.domain.InsufficientPointException
import me.park.user.domain.PointWalletNotFoundException
import me.park.user.domain.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFoundException(exception: UserNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message)
    }

    @ExceptionHandler(PointWalletNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handlePointWalletNotFoundException(exception: PointWalletNotFoundException): ErrorResponse {
        return ErrorResponse(exception.message)
    }

    @ExceptionHandler(InsufficientPointException::class, IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(exception: RuntimeException): ErrorResponse {
        return ErrorResponse(exception.message)
    }
}
