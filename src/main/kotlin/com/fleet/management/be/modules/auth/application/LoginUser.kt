package com.fleet.management.be.modules.auth.application

import com.fleet.management.be.modules.auth.application.ports.UserRepositoryPort
import org.springframework.stereotype.Service

data class LoginUserCommand(val username: String, val password: String)

@Service
class LoginUser(
    private val users: UserRepositoryPort,
    private val hasher: PasswordHasher,
    private val tokens: TokenService
) {
    data class Result(val token: String, val username: String, val role: String, val id: Long?)

    fun handle(cmd: LoginUserCommand): Result {
        val user = users.findByUsername(cmd.username).orElseThrow { IllegalArgumentException("Invalid credentials") }
        if (!hasher.matches(cmd.password, user.password)) throw IllegalArgumentException("Invalid credentials")
        val token = tokens.generate(user.username, listOf(user.role.name))
        return Result(token, user.username, user.role.name, user.id)
    }
}
