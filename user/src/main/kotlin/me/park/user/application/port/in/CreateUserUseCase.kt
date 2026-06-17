package me.park.user.application.port.`in`

import me.park.user.application.command.CreateUserCommand
import me.park.user.domain.User

interface CreateUserUseCase {

    fun createUser(command: CreateUserCommand): User
}
