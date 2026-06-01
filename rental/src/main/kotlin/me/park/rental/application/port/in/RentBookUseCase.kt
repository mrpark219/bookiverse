package me.park.rental.application.port.`in`

import me.park.rental.application.command.RentBookCommand
import me.park.rental.domain.RentalItem

interface RentBookUseCase {

    fun rentBook(command: RentBookCommand): RentalItem
}
