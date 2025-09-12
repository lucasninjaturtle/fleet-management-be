package com.fleet.management.be.modules.auth.application.ports

import com.fleet.management.be.modules.auth.domain.User
import java.util.*

interface UserRepositoryPort {
    fun save(user: User): User
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): Optional<User>
}
