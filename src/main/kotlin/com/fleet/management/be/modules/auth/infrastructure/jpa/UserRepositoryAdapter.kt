package com.fleet.management.be.modules.auth.infrastructure.jpa

import com.fleet.management.be.modules.auth.application.ports.UserRepositoryPort
import com.fleet.management.be.modules.auth.domain.User
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserRepositoryAdapter(
    private val repo: JpaUserRepository
) : UserRepositoryPort {
    override fun save(user: User): User = repo.save(user)
    override fun existsByUsername(username: String): Boolean = repo.existsByUsername(username)
    override fun findByUsername(username: String): Optional<User> = repo.findByUsername(username)
}
