package me.park.book.domain

class InvalidStockQuantityException(
    quantity: Long,
) : RuntimeException("차감 수량은 1 이상이어야 합니다. quantity=$quantity")
