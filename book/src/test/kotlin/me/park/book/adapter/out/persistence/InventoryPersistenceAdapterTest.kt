package me.park.book.adapter.out.persistence

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InventoryPersistenceAdapterTest {

    @Test
    @DisplayName("조건부 재고 차감 update count가 1이면 차감 성공으로 처리한다")
    fun deductInventory() {
        // given
        val jpaInventoryRepository = mockk<JpaInventoryRepository>()
        every {
            jpaInventoryRepository.deductAvailableQuantity(bookId = 10L, quantity = 1L)
        } returns 1
        val adapter = InventoryPersistenceAdapter(jpaInventoryRepository)

        // when
        val deducted = adapter.deduct(bookId = 10L, quantity = 1L)

        // then
        assertTrue(deducted)
    }

    @Test
    @DisplayName("조건부 재고 차감 update count가 0이면 차감 실패로 처리한다")
    fun failWhenInventoryUpdateCountIsZero() {
        // given
        val jpaInventoryRepository = mockk<JpaInventoryRepository>()
        every {
            jpaInventoryRepository.deductAvailableQuantity(bookId = 10L, quantity = 1L)
        } returns 0
        val adapter = InventoryPersistenceAdapter(jpaInventoryRepository)

        // when
        val deducted = adapter.deduct(bookId = 10L, quantity = 1L)

        // then
        assertFalse(deducted)
    }
}
