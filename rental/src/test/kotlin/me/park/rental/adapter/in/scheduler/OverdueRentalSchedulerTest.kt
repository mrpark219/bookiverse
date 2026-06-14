package me.park.rental.adapter.`in`.scheduler

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import me.park.rental.application.port.`in`.MarkOverdueRentalsUseCase
import org.junit.jupiter.api.DisplayName
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OverdueRentalSchedulerTest {

    @Test
    @DisplayName("매일 새벽 연체 대출 만료 처리를 실행하도록 스케줄을 설정한다")
    fun configureOverdueRentalSchedule() {
        // when
        val scheduled = OverdueRentalScheduler::class.memberFunctions
            .first { it.name == "markOverdueRentals" }
            .findAnnotation<Scheduled>()

        // then
        assertNotNull(scheduled)
        assertEquals("\${rental.overdue.scheduler.cron:0 5 0 * * *}", scheduled.cron)
        assertEquals("Asia/Seoul", scheduled.zone)
    }

    @Test
    @DisplayName("스케줄러가 연체 대출 만료 처리 유스케이스를 호출한다")
    fun markOverdueRentals() {
        // given
        val markOverdueRentalsUseCase = mockk<MarkOverdueRentalsUseCase>()
        every { markOverdueRentalsUseCase.markOverdueRentals(any<LocalDate>()) } just Runs
        val scheduler = OverdueRentalScheduler(markOverdueRentalsUseCase)

        // when
        scheduler.markOverdueRentals()

        // then
        verify(exactly = 1) {
            markOverdueRentalsUseCase.markOverdueRentals(any<LocalDate>())
        }
    }
}
