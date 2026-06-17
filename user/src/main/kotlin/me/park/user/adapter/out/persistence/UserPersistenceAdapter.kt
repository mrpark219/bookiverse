package me.park.user.adapter.out.persistence

import me.park.user.application.port.out.UserRepository
import me.park.user.domain.User
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val jpaUserRepository: JpaUserRepository,
) : UserRepository {

    override fun save(user: User): User {
        return jpaUserRepository.save(user)
    }

    override fun findById(userId: Long): User? {
        return jpaUserRepository.findById(userId).orElse(null)
    }

    override fun findByToken(token: String): User? {
        return jpaUserRepository.findByToken(token)
    }
}
