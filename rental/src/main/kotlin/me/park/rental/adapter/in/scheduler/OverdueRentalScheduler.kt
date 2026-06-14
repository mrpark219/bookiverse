package me.park.rental.adapter.`in`.scheduler

import me.park.rental.application.port.`in`.MarkOverdueRentalsUseCase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class OverdueRentalScheduler(
    private val markOverdueRentalsUseCase: MarkOverdueRentalsUseCase,
) {

    @Scheduled(
        cron = "\${rental.overdue.scheduler.cron:0 5 0 * * *}",
        zone = "Asia/Seoul",
    )
    fun markOverdueRentals() {
        markOverdueRentalsUseCase.markOverdueRentals(LocalDate.now())
    }
}
