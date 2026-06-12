package me.park.rental.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.min

data class LateFeePolicy(
    val dailyPoint: Long,
    val maxChargeDays: Long,
) {

    init {
        require(dailyPoint > 0) { "일일 연체 포인트는 1 이상이어야 합니다. dailyPoint=$dailyPoint" }
        require(maxChargeDays > 0) { "최대 연체료 부과 일수는 1 이상이어야 합니다. maxChargeDays=$maxChargeDays" }
    }

    fun calculate(dueDate: LocalDate, baseDate: LocalDate): Long {
        if (!baseDate.isAfter(dueDate)) {
            return 0L
        }

        val overdueDays = ChronoUnit.DAYS.between(dueDate, baseDate)
        return min(overdueDays, maxChargeDays) * dailyPoint
    }

    companion object {
        val DEFAULT = LateFeePolicy(
            dailyPoint = 100L,
            maxChargeDays = 3L,
        )
    }
}
