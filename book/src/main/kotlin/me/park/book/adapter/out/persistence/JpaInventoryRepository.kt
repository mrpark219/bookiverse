package me.park.book.adapter.out.persistence

import me.park.book.domain.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JpaInventoryRepository : JpaRepository<Inventory, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        update Inventory i
           set i.availableQuantity = i.availableQuantity - :quantity
         where i.bookId = :bookId
           and i.availableQuantity >= :quantity
        """,
    )
    fun deductAvailableQuantity(
        @Param("bookId") bookId: Long,
        @Param("quantity") quantity: Long,
    ): Int
}
