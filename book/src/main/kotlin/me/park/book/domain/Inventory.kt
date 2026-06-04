package me.park.book.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Inventory(
    @Column(nullable = false, unique = true)
    var bookId: Long,

    @Column(nullable = false)
    var totalQuantity: Long,

    @Column(nullable = false)
    var availableQuantity: Long,
) {

    init {
        require(totalQuantity >= 0) { "전체 재고 수량은 0 이상이어야 합니다. totalQuantity=$totalQuantity" }
        require(availableQuantity >= 0) { "가용 재고 수량은 0 이상이어야 합니다. availableQuantity=$availableQuantity" }
        require(availableQuantity <= totalQuantity) {
            "가용 재고 수량은 전체 재고 수량보다 클 수 없습니다. availableQuantity=$availableQuantity, totalQuantity=$totalQuantity"
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null

    fun deduct(quantity: Long) {
        if (quantity < 1) {
            throw InvalidStockQuantityException(quantity)
        }

        if (availableQuantity < quantity) {
            throw InsufficientStockException(
                bookId = bookId,
                quantity = quantity,
                availableQuantity = availableQuantity,
            )
        }

        availableQuantity -= quantity
    }
}
