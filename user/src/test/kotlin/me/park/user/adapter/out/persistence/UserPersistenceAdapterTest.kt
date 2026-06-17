package me.park.user.adapter.out.persistence

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.park.user.domain.User
import kotlin.test.Test
import kotlin.test.assertEquals

class UserPersistenceAdapterTest {

    @Test
    fun saveUser() {
        // given
        val user = User.create(name = "park")
        val jpaUserRepository = mockk<JpaUserRepository>()
        every { jpaUserRepository.save(user) } returns user
        val adapter = UserPersistenceAdapter(jpaUserRepository)

        // when
        val savedUser = adapter.save(user)

        // then
        assertEquals(user, savedUser)
        verify { jpaUserRepository.save(user) }
    }

    @Test
    fun findUserByToken() {
        // given
        val user = User.create(name = "park")
        val jpaUserRepository = mockk<JpaUserRepository>()
        every { jpaUserRepository.findByToken(user.token) } returns user
        val adapter = UserPersistenceAdapter(jpaUserRepository)

        // when
        val foundUser = adapter.findByToken(user.token)

        // then
        assertEquals(user, foundUser)
    }
}
