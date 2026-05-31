package me.park.rental.domain

class RentUnavailableException(
    message: String,
) : RuntimeException(message)
