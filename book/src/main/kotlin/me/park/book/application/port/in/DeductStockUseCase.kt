package me.park.book.application.port.`in`

import me.park.book.application.command.DeductStockCommand

interface DeductStockUseCase {

    fun deductStock(command: DeductStockCommand)
}
