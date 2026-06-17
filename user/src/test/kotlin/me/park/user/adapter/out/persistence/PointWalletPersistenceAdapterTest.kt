package me.park.user.adapter.out.persistence

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.park.user.domain.PointWallet
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

class PointWalletPersistenceAdapterTest {

    @Test
    fun savePointWallet() {
        // given
        val wallet = PointWallet.create(userId = 1L)
        val jpaPointWalletRepository = mockk<JpaPointWalletRepository>()
        every { jpaPointWalletRepository.save(wallet) } returns wallet
        val adapter = PointWalletPersistenceAdapter(jpaPointWalletRepository)

        // when
        val savedWallet = adapter.save(wallet)

        // then
        assertEquals(wallet, savedWallet)
        verify { jpaPointWalletRepository.save(wallet) }
    }

    @Test
    fun findPointWalletByUserId() {
        // given
        val wallet = PointWallet.create(userId = 1L)
        val jpaPointWalletRepository = mockk<JpaPointWalletRepository>()
        every { jpaPointWalletRepository.findById(1L) } returns Optional.of(wallet)
        val adapter = PointWalletPersistenceAdapter(jpaPointWalletRepository)

        // when
        val foundWallet = adapter.findByUserId(1L)

        // then
        assertEquals(wallet, foundWallet)
    }
}
