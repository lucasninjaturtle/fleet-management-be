package com.fleet.management.be.modules.seed.infrastructure.graphql

import com.fleet.management.be.modules.seed.service.SeedService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping

@Controller
class SeedResolver(
    private val seedService: SeedService
) {
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    fun seed(@Argument kind: String?): Boolean {
        when (kind?.lowercase() ?: "full") {
            "full" -> seedService.seedFull()
            else -> seedService.seedFull()
        }
        return true
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    fun resetDb(): Boolean {
        seedService.reset()
        return true
    }
}
