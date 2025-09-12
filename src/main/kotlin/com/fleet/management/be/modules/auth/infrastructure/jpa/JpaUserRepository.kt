package com.fleet.management.be.modules.auth.infrastructure.jpa

import com.fleet.management.be.modules.auth.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaUserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
}