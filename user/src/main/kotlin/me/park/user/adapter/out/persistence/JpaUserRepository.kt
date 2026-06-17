package me.park.user.adapter.out.persistence

import me.park.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long> {

    fun findByToken(token: String): User?
}
