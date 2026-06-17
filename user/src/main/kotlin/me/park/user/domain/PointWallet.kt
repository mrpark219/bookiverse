package me.park.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class PointWallet(
    @Id
    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var balance: Long,

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    fun earn(amount: Long) {
        validatePositiveAmount(amount)
        balance += amount
        updatedAt = LocalDateTime.now()
    }

    fun use(amount: Long) {
        validatePositiveAmount(amount)
        if (balance < amount) {
            throw InsufficientPointException("포인트 잔액이 부족합니다. balance=$balance, amount=$amount")
        }
        balance -= amount
        updatedAt = LocalDateTime.now()
    }

    private fun validatePositiveAmount(amount: Long) {
        require(amount > 0) { "포인트는 1 이상이어야 합니다. amount=$amount" }
    }

    companion object {
        fun create(userId: Long): PointWallet {
            return PointWallet(
                userId = userId,
                balance = 0L,
            )
        }
    }
}
