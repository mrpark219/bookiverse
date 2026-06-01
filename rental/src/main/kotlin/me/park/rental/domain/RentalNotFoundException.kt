package me.park.rental.domain

class RentalNotFoundException(
    message: String,
) : RuntimeException(message)
