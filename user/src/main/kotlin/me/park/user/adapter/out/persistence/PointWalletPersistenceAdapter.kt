package me.park.user.adapter.out.persistence

import me.park.user.application.port.out.PointWalletRepository
import me.park.user.domain.PointWallet
import org.springframework.stereotype.Repository

@Repository
class PointWalletPersistenceAdapter(
    private val jpaPointWalletRepository: JpaPointWalletRepository,
) : PointWalletRepository {

    override fun save(wallet: PointWallet): PointWallet {
        return jpaPointWalletRepository.save(wallet)
    }

    override fun findByUserId(userId: Long): PointWallet? {
        return jpaPointWalletRepository.findById(userId).orElse(null)
    }
}
