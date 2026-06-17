package me.park.user.adapter.`in`.web

import me.park.user.adapter.`in`.web.request.ChangePointRequest
import me.park.user.adapter.`in`.web.request.CreateUserRequest
import me.park.user.adapter.`in`.web.response.PointBalanceResponse
import me.park.user.adapter.`in`.web.response.PointLedgerResponse
import me.park.user.adapter.`in`.web.response.UserResponse
import me.park.user.application.command.CreateUserCommand
import me.park.user.application.command.EarnPointCommand
import me.park.user.application.command.UsePointCommand
import me.park.user.application.port.`in`.CreateUserUseCase
import me.park.user.application.port.`in`.GetUserUseCase
import me.park.user.application.port.`in`.PointUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val pointUseCase: PointUseCase,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        return UserResponse.from(
            createUserUseCase.createUser(
                CreateUserCommand(name = request.name),
            ),
        )
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): UserResponse {
        return UserResponse.from(getUserUseCase.getUser(userId))
    }

    @GetMapping("/token/{token}")
    fun getUserByToken(@PathVariable token: String): UserResponse {
        return UserResponse.from(getUserUseCase.getUserByToken(token))
    }

    @GetMapping("/{userId}/points")
    fun getPointBalance(@PathVariable userId: Long): PointBalanceResponse {
        return PointBalanceResponse.from(pointUseCase.getPointBalance(userId))
    }

    @PostMapping("/{userId}/points/earn")
    fun earnPoint(
        @PathVariable userId: Long,
        @RequestBody request: ChangePointRequest,
    ): PointBalanceResponse {
        return PointBalanceResponse.from(
            pointUseCase.earnPoint(
                EarnPointCommand(
                    userId = userId,
                    amount = request.amount,
                    reason = request.reason,
                    referenceType = request.referenceType,
                    referenceId = request.referenceId,
                ),
            ),
        )
    }

    @PostMapping("/{userId}/points/use")
    fun usePoint(
        @PathVariable userId: Long,
        @RequestBody request: ChangePointRequest,
    ): PointBalanceResponse {
        return PointBalanceResponse.from(
            pointUseCase.usePoint(
                UsePointCommand(
                    userId = userId,
                    amount = request.amount,
                    reason = request.reason,
                    referenceType = request.referenceType,
                    referenceId = request.referenceId,
                ),
            ),
        )
    }

    @GetMapping("/{userId}/points/ledgers")
    fun getPointLedgers(@PathVariable userId: Long): List<PointLedgerResponse> {
        return pointUseCase.getPointLedgers(userId).map(PointLedgerResponse::from)
    }
}
