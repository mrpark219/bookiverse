package me.park.user.domain

import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UserTest {

    @Test
    @DisplayName("사용자를 생성하면 활성 상태와 토큰을 가진다")
    fun createUser() {
        // when
        val user = User.create(name = "park")

        // then
        assertEquals("park", user.name)
        assertEquals(UserStatus.ACTIVE, user.status)
        assertFalse(user.token.isBlank())
    }
}
