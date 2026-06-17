package me.park.user.domain

import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PointWalletTest {

    @Test
    @DisplayName("포인트를 적립하면 잔액이 증가한다")
    fun earnPoint() {
        // given
        val wallet = PointWallet.create(userId = 1L)

        // when
        wallet.earn(100L)

        // then
        assertEquals(100L, wallet.balance)
    }

    @Test
    @DisplayName("포인트를 사용하면 잔액이 감소한다")
    fun usePoint() {
        // given
        val wallet = PointWallet.create(userId = 1L)
        wallet.earn(100L)

        // when
        wallet.use(40L)

        // then
        assertEquals(60L, wallet.balance)
    }

    @Test
    @DisplayName("잔액보다 많은 포인트를 사용할 수 없다")
    fun failWhenPointBalanceIsInsufficient() {
        // given
        val wallet = PointWallet.create(userId = 1L)
        wallet.earn(30L)

        // when & then
        val exception = assertFailsWith<InsufficientPointException> {
            wallet.use(40L)
        }
        assertEquals("포인트 잔액이 부족합니다. balance=30, amount=40", exception.message)
    }

    @Test
    @DisplayName("0 이하의 포인트는 적립할 수 없다")
    fun failWhenEarnAmountIsNotPositive() {
        // given
        val wallet = PointWallet.create(userId = 1L)

        // when & then
        val exception = assertFailsWith<IllegalArgumentException> {
            wallet.earn(0L)
        }
        assertEquals("포인트는 1 이상이어야 합니다. amount=0", exception.message)
    }

    @Test
    @DisplayName("0 이하의 포인트는 사용할 수 없다")
    fun failWhenUseAmountIsNotPositive() {
        // given
        val wallet = PointWallet.create(userId = 1L)

        // when & then
        val exception = assertFailsWith<IllegalArgumentException> {
            wallet.use(0L)
        }
        assertEquals("포인트는 1 이상이어야 합니다. amount=0", exception.message)
    }
}
