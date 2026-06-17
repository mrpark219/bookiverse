package me.park.user.adapter.`in`.web.response

import me.park.user.application.response.PointBalance

data class PointBalanceResponse(
    val userId: Long,
    val balance: Long,
) {

    companion object {
        fun from(balance: PointBalance): PointBalanceResponse {
            return PointBalanceResponse(
                userId = balance.userId,
                balance = balance.balance,
            )
        }
    }
}
