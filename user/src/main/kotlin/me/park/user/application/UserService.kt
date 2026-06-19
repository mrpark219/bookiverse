package me.park.user.application

import me.park.user.application.command.CreateUserCommand
import me.park.user.application.command.EarnPointCommand
import me.park.user.application.command.UsePointCommand
import me.park.user.application.port.`in`.CreateUserUseCase
import me.park.user.application.port.`in`.GetUserUseCase
import me.park.user.application.port.`in`.PointUseCase
import me.park.user.application.port.out.PointLedgerRepository
import me.park.user.application.port.out.PointWalletRepository
import me.park.user.application.port.out.UserRepository
import me.park.user.application.response.PointBalance
import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType
import me.park.user.domain.PointWallet
import me.park.user.domain.PointWalletNotFoundException
import me.park.user.domain.User
import me.park.user.domain.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val pointWalletRepository: PointWalletRepository,
    private val pointLedgerRepository: PointLedgerRepository,
) : CreateUserUseCase, GetUserUseCase, PointUseCase {

    @Transactional
    override fun createUser(command: CreateUserCommand): User {
        val user = userRepository.save(User.create(command.name))
        pointWalletRepository.save(PointWallet.create(requireNotNull(user.id)))
        return user
    }

    @Transactional(readOnly = true)
    override fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            ?: throw UserNotFoundException("사용자를 찾을 수 없습니다. userId=$userId")
    }

    @Transactional(readOnly = true)
    override fun getUserByToken(token: String): User {
        return userRepository.findByToken(token)
            ?: throw UserNotFoundException("사용자를 찾을 수 없습니다. token=$token")
    }

    @Transactional
    override fun earnPoint(command: EarnPointCommand): PointBalance {
        val wallet = findWallet(command.userId)
        if (alreadyEarned(command)) {
            return PointBalance(userId = wallet.userId, balance = wallet.balance)
        }
        wallet.earn(command.amount)
        pointWalletRepository.save(wallet)
        pointLedgerRepository.save(
            PointLedger.earn(
                userId = command.userId,
                amount = command.amount,
                balanceAfter = wallet.balance,
                reason = command.reason,
                referenceType = command.referenceType,
                referenceId = command.referenceId,
            ),
        )
        return PointBalance(userId = wallet.userId, balance = wallet.balance)
    }

    private fun alreadyEarned(command: EarnPointCommand): Boolean {
        val referenceType = command.referenceType ?: return false
        val referenceId = command.referenceId ?: return false
        return pointLedgerRepository.existsByTypeAndReferenceTypeAndReferenceId(
            type = PointLedgerType.EARN,
            referenceType = referenceType,
            referenceId = referenceId,
        )
    }

    @Transactional
    override fun usePoint(command: UsePointCommand): PointBalance {
        val wallet = findWallet(command.userId)
        wallet.use(command.amount)
        pointWalletRepository.save(wallet)
        pointLedgerRepository.save(
            PointLedger.use(
                userId = command.userId,
                amount = command.amount,
                balanceAfter = wallet.balance,
                reason = command.reason,
                referenceType = command.referenceType,
                referenceId = command.referenceId,
            ),
        )
        return PointBalance(userId = wallet.userId, balance = wallet.balance)
    }

    @Transactional(readOnly = true)
    override fun getPointBalance(userId: Long): PointBalance {
        val wallet = findWallet(userId)
        return PointBalance(userId = wallet.userId, balance = wallet.balance)
    }

    @Transactional(readOnly = true)
    override fun getPointLedgers(userId: Long): List<PointLedger> {
        return pointLedgerRepository.findByUserId(userId)
    }

    private fun findWallet(userId: Long): PointWallet {
        return pointWalletRepository.findByUserId(userId)
            ?: throw PointWalletNotFoundException("포인트 지갑을 찾을 수 없습니다. userId=$userId")
    }
}
