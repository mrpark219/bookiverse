package me.park.user.application.port.out

import me.park.user.domain.User

interface UserRepository {

    fun save(user: User): User

    fun findById(userId: Long): User?

    fun findByToken(token: String): User?
}
