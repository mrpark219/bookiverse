package me.park.book.domain

import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InventoryTest {

    @Test
    @DisplayName("전체 재고 수량은 0 이상이어야 한다")
    fun throwExceptionWhenTotalQuantityIsNegative() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Inventory(
                bookId = 10L,
                totalQuantity = -1L,
                availableQuantity = 0L,
            )
        }

        assertEquals("전체 재고 수량은 0 이상이어야 합니다. totalQuantity=-1", exception.message)
    }

    @Test
    @DisplayName("가용 재고 수량은 0 이상이어야 한다")
    fun throwExceptionWhenAvailableQuantityIsNegative() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Inventory(
                bookId = 10L,
                totalQuantity = 1L,
                availableQuantity = -1L,
            )
        }

        assertEquals("가용 재고 수량은 0 이상이어야 합니다. availableQuantity=-1", exception.message)
    }

    @Test
    @DisplayName("가용 재고 수량은 전체 재고 수량보다 클 수 없다")
    fun throwExceptionWhenAvailableQuantityIsGreaterThanTotalQuantity() {
        val exception = assertFailsWith<IllegalArgumentException> {
            Inventory(
                bookId = 10L,다
                totalQuantity = 1L,
                availableQuantity = 2L,
            )
        }

        assertEquals(
            "가용 재고 수량은 전체 재고 수량보다 클 수 없습니다. availableQuantity=2, totalQuantity=1",
            exception.message,
        )
    }

    @Test
    @DisplayName("가용 재고가 충분하면 재고를 차감한다")
    fun deductAvailableQuantity() {
        val inventory = Inventory(
            bookId = 10L,
            totalQuantity = 3L,
            availableQuantity = 2L,
        )

        inventory.deduct(quantity = 1L)

        assertEquals(1L, inventory.availableQuantity)
    }

    @Test
    @DisplayName("가용 재고가 부족하면 예외를 던지고 재고를 변경하지 않는다")
    fun throwExceptionWhenAvailableQuantityIsInsufficient() {
        val inventory = Inventory(
            bookId = 10L,
            totalQuantity = 3L,
            availableQuantity = 1L,
        )

        val exception = assertFailsWith<InsufficientStockException> {
            inventory.deduct(quantity = 2L)
        }

        assertEquals(
            "차감 가능한 재고가 부족합니다. bookId=10, quantity=2, availableQuantity=1",
            exception.message,
        )
        assertEquals(1L, inventory.availableQuantity)
    }

    @Test
    @DisplayName("차감 수량은 1 이상이어야 한다")
    fun throwExceptionWhenDeductQuantityIsNotPositive() {
        val inventory = Inventory(
            bookId = 10L,
            totalQuantity = 3L,
            availableQuantity = 1L,
        )

        val exception = assertFailsWith<InvalidStockQuantityException> {
            inventory.deduct(quantity = 0L)
        }

        assertEquals("차감 수량은 1 이상이어야 합니다. quantity=0", exception.message)
        assertEquals(1L, inventory.availableQuantity)
    }
}
