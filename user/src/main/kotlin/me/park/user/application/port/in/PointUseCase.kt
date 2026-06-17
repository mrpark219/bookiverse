package me.park.user.application.port.`in`

import me.park.user.application.command.EarnPointCommand
import me.park.user.application.command.UsePointCommand
import me.park.user.application.response.PointBalance
import me.park.user.domain.PointLedger

interface PointUseCase {

    fun earnPoint(command: EarnPointCommand): PointBalance

    fun usePoint(command: UsePointCommand): PointBalance

    fun getPointBalance(userId: Long): PointBalance

    fun getPointLedgers(userId: Long): List<PointLedger>
}
