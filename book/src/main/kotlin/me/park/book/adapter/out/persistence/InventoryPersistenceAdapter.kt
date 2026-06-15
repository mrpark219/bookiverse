package me.park.book.adapter.out.persistence

import me.park.book.application.port.out.InventoryRepository
import org.springframework.stereotype.Repository

@Repository
class InventoryPersistenceAdapter(
    private val jpaInventoryRepository: JpaInventoryRepository,
) : InventoryRepository {

    override fun deduct(bookId: Long, quantity: Long): Boolean {
        return jpaInventoryRepository.deductAvailableQuantity(
            bookId = bookId,
            quantity = quantity,
        ) == 1
    }

    override fun restore(bookId: Long, quantity: Long): Boolean {
        return jpaInventoryRepository.restoreAvailableQuantity(
            bookId = bookId,
            quantity = quantity,
        ) == 1
    }
}
