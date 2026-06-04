package me.park.book.domain

class InsufficientStockException(
    bookId: Long,
    quantity: Long,
    availableQuantity: Long,
) : RuntimeException(
    "차감 가능한 재고가 부족합니다. bookId=$bookId, quantity=$quantity, availableQuantity=$availableQuantity"
)
