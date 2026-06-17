package me.park.user.adapter.`in`.web.response

import me.park.user.domain.User
import me.park.user.domain.UserStatus

data class UserResponse(
    val id: Long?,
    val name: String,
    val token: String,
    val status: UserStatus,
) {

    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                name = user.name,
                token = user.token,
                status = user.status,
            )
        }
    }
}
