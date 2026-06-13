package me.park.rental.adapter.out.persistence

import io.mockk.every
import io.mockk.mockk
import me.park.rental.domain.Rental
import me.park.rental.domain.RentalItemStatus
import org.junit.jupiter.api.DisplayName
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class RentalPersistenceAdapterTest {

    @Test
    @DisplayName("연체 대상 대출 조회를 RENTED 상태와 기준일 조건으로 위임한다")
    fun findRentalsHavingOverdueItems() {
        // given
        val baseDate = LocalDate.of(2026, 6, 3)
        val rentals = listOf(Rental.create(userId = 1L))
        val jpaRentalRepository = mockk<JpaRentalRepository>()
        every {
            jpaRentalRepository.findDistinctByRentalItemsStatusAndRentalItemsDueDateBefore(
                status = RentalItemStatus.RENTED,
                baseDate = baseDate,
            )
        } returns rentals
        val adapter = RentalPersistenceAdapter(jpaRentalRepository)

        // when
        val result = adapter.findRentalsHavingOverdueItems(baseDate)

        // then
        assertEquals(rentals, result)
    }
}
