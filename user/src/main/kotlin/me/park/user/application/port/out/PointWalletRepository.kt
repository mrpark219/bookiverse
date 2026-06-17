package me.park.user.application.port.out

import me.park.user.domain.PointWallet

interface PointWalletRepository {

    fun save(wallet: PointWallet): PointWallet

    fun findByUserId(userId: Long): PointWallet?
}
