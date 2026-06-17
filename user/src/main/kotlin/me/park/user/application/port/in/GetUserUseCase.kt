package me.park.user.application.port.`in`

import me.park.user.domain.User

interface GetUserUseCase {

    fun getUser(userId: Long): User

    fun getUserByToken(token: String): User
}
