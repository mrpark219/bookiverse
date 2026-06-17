package me.park.user.adapter.out.persistence

import me.park.user.domain.PointWallet
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPointWalletRepository : JpaRepository<PointWallet, Long>
