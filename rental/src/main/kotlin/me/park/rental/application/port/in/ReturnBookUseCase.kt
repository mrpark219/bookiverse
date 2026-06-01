package me.park.rental.application.port.`in`

import me.park.rental.application.command.ReturnBookCommand
import me.park.rental.domain.RentalItem

interface ReturnBookUseCase {

    fun returnBook(command: ReturnBookCommand): RentalItem
}
