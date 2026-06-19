package me.park.user.application

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.park.user.application.command.CreateUserCommand
import me.park.user.application.command.EarnPointCommand
import me.park.user.application.command.UsePointCommand
import me.park.user.application.port.out.PointLedgerRepository
import me.park.user.application.port.out.PointWalletRepository
import me.park.user.application.port.out.UserRepository
import me.park.user.domain.PointLedger
import me.park.user.domain.PointLedgerType
import me.park.user.domain.PointWallet
import me.park.user.domain.User
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserServiceTest {

    @Test
    @DisplayName("사용자를 생성하면 사용자와 빈 포인트 지갑을 저장한다")
    fun createUser() {
        // given
        val userRepository = mockk<UserRepository>()
        val pointWalletRepository = mockk<PointWalletRepository>()
        val pointLedgerRepository = mockk<PointLedgerRepository>()
        val wallet = slot<PointWallet>()
        every { userRepository.save(any()) } answers {
            firstArg<User>().apply { id = 1L }
        }
        every { pointWalletRepository.save(capture(wallet)) } answers { wallet.captured }
        val service = UserService(userRepository, pointWalletRepository, pointLedgerRepository)

        // when
        val user = service.createUser(CreateUserCommand(name = "park"))

        // then
        assertEquals(1L, user.id)
        assertEquals("park", user.name)
        assertNotNull(user.token)
        assertEquals(1L, wallet.captured.userId)
        assertEquals(0L, wallet.captured.balance)
    }

    @Test
    @DisplayName("포인트를 적립하면 지갑 잔액을 증가시키고 적립 원장을 저장한다")
    fun earnPoint() {
        // given
        val userRepository = mockk<UserRepository>()
        val pointWalletRepository = mockk<PointWalletRepository>()
        val pointLedgerRepository = mockk<PointLedgerRepository>()
        val wallet = PointWallet.create(userId = 1L)
        val ledger = slot<PointLedger>()
        every { pointWalletRepository.findByUserId(1L) } returns wallet
        every {
            pointLedgerRepository.existsByTypeAndReferenceTypeAndReferenceId(
                type = PointLedgerType.EARN,
                referenceType = "RENTAL",
                referenceId = "rental-1",
            )
        } returns false
        every { pointWalletRepository.save(wallet) } returns wallet
        every { pointLedgerRepository.save(capture(ledger)) } answers { ledger.captured }
        val service = UserService(userRepository, pointWalletRepository, pointLedgerRepository)

        // when
        val balance = service.earnPoint(
            EarnPointCommand(
                userId = 1L,
                amount = 100L,
                reason = "대출 적립",
                referenceType = "RENTAL",
                referenceId = "rental-1",
            ),
        )

        // then
        assertEquals(100L, balance.balance)
        assertEquals(100L, wallet.balance)
        assertEquals(PointLedgerType.EARN, ledger.captured.type)
        assertEquals(100L, ledger.captured.amount)
        assertEquals(100L, ledger.captured.balanceAfter)
        assertEquals("대출 적립", ledger.captured.reason)
    }

    @Test
    @DisplayName("같은 참조의 포인트 적립 원장이 있으면 중복 적립하지 않는다")
    fun skipDuplicateEarnPointByReference() {
        // given
        val userRepository = mockk<UserRepository>()
        val pointWalletRepository = mockk<PointWalletRepository>()
        val pointLedgerRepository = mockk<PointLedgerRepository>(relaxed = true)
        val wallet = PointWallet.create(userId = 1L)
        wallet.earn(100L)
        every { pointWalletRepository.findByUserId(1L) } returns wallet
        every {
            pointLedgerRepository.existsByTypeAndReferenceTypeAndReferenceId(
                type = PointLedgerType.EARN,
                referenceType = "RENTAL",
                referenceId = "rental-1",
            )
        } returns true
        val service = UserService(userRepository, pointWalletRepository, pointLedgerRepository)

        // when
        val balance = service.earnPoint(
            EarnPointCommand(
                userId = 1L,
                amount = 100L,
                reason = "대출 적립",
                referenceType = "RENTAL",
                referenceId = "rental-1",
            ),
        )

        // then
        assertEquals(100L, balance.balance)
        assertEquals(100L, wallet.balance)
        verify(exactly = 0) { pointWalletRepository.save(any()) }
        verify(exactly = 0) { pointLedgerRepository.save(any()) }
    }

    @Test
    @DisplayName("포인트를 사용하면 지갑 잔액을 감소시키고 사용 원장을 저장한다")
    fun usePoint() {
        // given
        val userRepository = mockk<UserRepository>()
        val pointWalletRepository = mockk<PointWalletRepository>()
        val pointLedgerRepository = mockk<PointLedgerRepository>()
        val wallet = PointWallet.create(userId = 1L)
        wallet.earn(100L)
        val ledger = slot<PointLedger>()
        every { pointWalletRepository.findByUserId(1L) } returns wallet
        every { pointWalletRepository.save(wallet) } returns wallet
        every { pointLedgerRepository.save(capture(ledger)) } answers { ledger.captured }
        val service = UserService(userRepository, pointWalletRepository, pointLedgerRepository)

        // when
        val balance = service.usePoint(
            UsePointCommand(
                userId = 1L,
                amount = 40L,
                reason = "연체료 상환",
                referenceType = null,
                referenceId = null,
            ),
        )

        // then
        assertEquals(60L, balance.balance)
        assertEquals(60L, wallet.balance)
        assertEquals(PointLedgerType.USE, ledger.captured.type)
        assertEquals(40L, ledger.captured.amount)
        assertEquals(60L, ledger.captured.balanceAfter)
    }

    @Test
    @DisplayName("사용자 토큰으로 사용자를 조회한다")
    fun getUserByToken() {
        // given
        val user = User.create(name = "park")
        val userRepository = mockk<UserRepository>()
        val pointWalletRepository = mockk<PointWalletRepository>()
        val pointLedgerRepository = mockk<PointLedgerRepository>()
        every { userRepository.findByToken(user.token) } returns user
        val service = UserService(userRepository, pointWalletRepository, pointLedgerRepository)

        // when
        val foundUser = service.getUserByToken(user.token)

        // then
        assertEquals(user, foundUser)
        verify { userRepository.findByToken(user.token) }
    }
}
