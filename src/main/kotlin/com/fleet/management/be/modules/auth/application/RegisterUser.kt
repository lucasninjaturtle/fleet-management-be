package com.fleet.management.be.modules.auth.application

import com.fleet.management.be.common.errors.Conflict
import com.fleet.management.be.modules.auth.application.ports.UserRepositoryPort
import com.fleet.management.be.modules.auth.domain.Role
import com.fleet.management.be.modules.auth.domain.User
import org.springframework.stereotype.Service

data class RegisterUserCommand(val username: String, val password: String, val role: Role?)

@Service
class RegisterUser(
    private val users: UserRepositoryPort,
    private val hasher: PasswordHasher,
    private val tokens: TokenService
) {
    data class Result(val token: String, val user: User)

    fun handle(cmd: RegisterUserCommand): Result {
        if (users.existsByUsername(cmd.username)) throw Conflict("Username already exists")
        val user = users.save(
            User(username = cmd.username, password = hasher.encode(cmd.password), role = cmd.role ?: Role.USER)
        )
        val token = tokens.generate(user.username, listOf(user.role.name))
        return Result(token, user)
    }
}