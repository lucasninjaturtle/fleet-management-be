package com.fleet.management.be.modules.auth.graphql

import com.fleet.management.be.common.TestData
import com.fleet.management.be.config.TestSecurityConfig
import com.fleet.management.be.modules.auth.infrastructure.graphql.AuthResolver
import com.fleet.management.be.modules.auth.infrastructure.jpa.JpaUserRepository
import com.fleet.management.be.modules.auth.infrastructure.security.JwtService
import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import java.util.*

@GraphQlTest(controllers = [AuthResolver::class])
class AuthResolverMeGraphQLTest {

    @Autowired lateinit var graphQlTester: GraphQlTester

    @MockBean lateinit var userRepo: JpaUserRepository
    @MockBean lateinit var vehicleQueries: VehicleQueryPort
    @MockBean lateinit var passwordEncoder: PasswordEncoder
    @MockBean lateinit var jwtService: JwtService



    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun `me returns user and assigned vehicles`() {
        val u = TestData.user(id = 2L, username = "user")
        Mockito.`when`(userRepo.findByUsername("user")).thenReturn(Optional.of(u))
        Mockito.`when`(vehicleQueries.findByAssignedUser(2L)).thenReturn(listOf(TestData.vehicle(id = 1L, assignedUserId = 2L)))
        Mockito.`when`(vehicleQueries.countByAssignedUserAndStatus(2L, VehicleStatus.ACTIVE)).thenReturn(1)
        Mockito.`when`(vehicleQueries.countByAssignedUserAndStatus(2L, VehicleStatus.INACTIVE)).thenReturn(0)

        graphQlTester.document("""
            query {
              me {
                user { id username role }
                vehiclesAssigned { id plateNumber }
                vehiclesActiveCount
                vehiclesInactiveCount
              }
            }
        """.trimIndent())
            .execute()
            .path("me.user.username").entity(String::class.java).isEqualTo("user")
            .path("me.vehiclesAssigned[0].id").entity(String::class.java).isEqualTo("1")
            .path("me.vehiclesActiveCount").entity(Int::class.java).isEqualTo(1)
    }
}
