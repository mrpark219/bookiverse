package me.park.rental.application.port.`in`

import java.time.LocalDate

interface MarkOverdueRentalsUseCase {

    fun markOverdueRentals(baseDate: LocalDate = LocalDate.now())
}
