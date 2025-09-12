package com.fleet.management.be.modules.auth.infrastructure.security

import com.fleet.management.be.modules.auth.application.PasswordHasher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class SpringPasswordHasher(
    private val encoder: PasswordEncoder
) : PasswordHasher {
    override fun encode(raw: String): String = encoder.encode(raw)
    override fun matches(raw: String, encoded: String): Boolean = encoder.matches(raw, encoded)
}