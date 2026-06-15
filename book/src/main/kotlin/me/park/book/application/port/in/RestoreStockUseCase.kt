package me.park.book.application.port.`in`

import me.park.book.application.command.RestoreStockCommand

interface RestoreStockUseCase {

    fun restoreStock(command: RestoreStockCommand)
}
