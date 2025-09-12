package com.fleet.management.be.modules.auth.infrastructure.security

import com.fleet.management.be.modules.auth.infrastructure.jpa.JpaUserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val repo: JpaUserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val u = repo.findByUsername(username).orElseThrow { UsernameNotFoundException("User not found") }
        val auth = listOf(SimpleGrantedAuthority("ROLE_${u.role.name}"))
        return org.springframework.security.core.userdetails.User
            .withUsername(u.username).password(u.password).authorities(auth)
            .accountExpired(false).accountLocked(false).credentialsExpired(false).disabled(false).build()
    }
}
