package me.park.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true, length = 100)
    var token: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: UserStatus,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    var id: Long? = null

    companion object {
        fun create(name: String): User {
            return User(
                name = name,
                token = UUID.randomUUID().toString(),
                status = UserStatus.ACTIVE,
            )
        }
    }
}
