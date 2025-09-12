package com.fleet.management.be.modules.auth.infrastructure.graphql

import com.fleet.management.be.modules.auth.application.LoginUser
import com.fleet.management.be.modules.auth.application.LoginUserCommand
import com.fleet.management.be.modules.auth.application.RegisterUser
import com.fleet.management.be.modules.auth.application.RegisterUserCommand
import com.fleet.management.be.modules.auth.domain.Role
import com.fleet.management.be.modules.auth.domain.User
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

data class AuthPayload(val token: String, val user: User)

@Controller
class AuthResolver(
    private val registerUser: RegisterUser,
    private val loginUser: LoginUser
) {
    @MutationMapping
    fun register(@Argument username: String, @Argument password: String, @Argument role: Role?): AuthPayload {
        val result = registerUser.handle(RegisterUserCommand(username, password, role))
        return AuthPayload(result.token, result.user)
    }

    @MutationMapping
    fun login(@Argument username: String, @Argument password: String): AuthPayload {
        val r = loginUser.handle(LoginUserCommand(username, password))
        return AuthPayload(r.token, User(id = r.id, username = r.username, password = "", role = Role.valueOf(r.role)))
    }
}