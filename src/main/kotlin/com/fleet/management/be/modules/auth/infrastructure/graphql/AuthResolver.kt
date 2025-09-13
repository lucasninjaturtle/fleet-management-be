package com.fleet.management.be.modules.auth.infrastructure.graphql

import com.fleet.management.be.modules.auth.domain.Role
import com.fleet.management.be.modules.auth.domain.User
import com.fleet.management.be.modules.auth.infrastructure.jpa.JpaUserRepository
import com.fleet.management.be.modules.auth.infrastructure.security.JwtService
import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller

data class AuthPayload(val token: String, val user: User)

data class Me(
    val user: User,
    val vehiclesAssigned: List<Vehicle>,
    val vehiclesActiveCount: Int,
    val vehiclesInactiveCount: Int
)

@Controller
class AuthResolver(
    private val userRepo: JpaUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val vehicleQueries: VehicleQueryPort
) {
    @MutationMapping
    fun register(
        @Argument username: String,
        @Argument password: String,
        @Argument role: Role
    ): AuthPayload {
        if (userRepo.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }
        val saved = userRepo.save(
            User(
                username = username,
                password = passwordEncoder.encode(password),
                role = role
            )
        )
        val token = jwtService.generate(saved.username, listOf(saved.role.name))
        return AuthPayload(token, saved)
    }

    @MutationMapping
    fun login(
        @Argument username: String,
        @Argument password: String
    ): AuthPayload {
        val user = userRepo.findByUsername(username)
            .orElseThrow { BadCredentialsException("Invalid credentials") }

        if (!passwordEncoder.matches(password, user.password)) {
            throw BadCredentialsException("Invalid credentials")
        }

        val token = jwtService.generate(user.username, listOf(user.role.name))
        return AuthPayload(token, user)
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    fun me(): Me {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw org.springframework.security.access.AccessDeniedException("Unauthenticated")

        val username = when (val p = auth.principal) {
            is org.springframework.security.core.userdetails.UserDetails -> p.username
            is String -> p
            else -> auth.name
        } ?: throw org.springframework.security.access.AccessDeniedException("Unauthenticated")

        val user = userRepo.findByUsername(username)
            .orElseThrow { IllegalStateException("User $username not found") }

        val uid = user.id ?: throw IllegalStateException("Invalid user id")

        val vehicles = vehicleQueries.findByAssignedUser(uid)
        val active = vehicleQueries.countByAssignedUserAndStatus(uid, VehicleStatus.ACTIVE).toInt()
        val inactive = vehicleQueries.countByAssignedUserAndStatus(uid, VehicleStatus.INACTIVE).toInt()

        return Me(
            user = user,
            vehiclesAssigned = vehicles,
            vehiclesActiveCount = active,
            vehiclesInactiveCount = inactive
        )
    }
}
