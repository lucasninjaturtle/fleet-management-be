package com.fleet.management.be.modules.vehicle.graphql

import com.fleet.management.be.common.TestData
import com.fleet.management.be.modules.vehicle.application.GetVehicleById
import com.fleet.management.be.modules.vehicle.application.ListVehicles
import com.fleet.management.be.modules.auth.infrastructure.jpa.JpaUserRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser

@GraphQlTest // sin controllers: dejamos que escanee, pero mockeamos lo que falta
@ImportAutoConfiguration(
    exclude = [
        SecurityAutoConfiguration::class,
        SecurityFilterAutoConfiguration::class
    ]
)
class VehicleResolverGraphQLTest {

    @Autowired
    lateinit var graphQlTester: GraphQlTester

    // ==== Mocks necesarios para el resolver de vehículos ====
    @MockBean
    lateinit var listVehicles: ListVehicles

    @MockBean
    lateinit var getVehicleById: GetVehicleById

    // ==== Mock para destrabar el AuthResolver que exige JpaUserRepository ====
    @MockBean
    lateinit var jpaUserRepository: JpaUserRepository

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `vehicles returns list`() {
        val v1 = TestData.vehicle(id = 1L)
        val v2 = TestData.vehicle(id = 2L)
        Mockito.`when`(listVehicles.handle()).thenReturn(listOf(v1, v2))

        graphQlTester
            .document(
                """
                query {
                  vehicles {
                    id
                  }
                }
                """.trimIndent()
            )
            .execute()
            .path("vehicles[0].id").entity(String::class.java).isEqualTo("1")
            .path("vehicles[1].id").entity(String::class.java).isEqualTo("2")
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `vehicle by id`() {
        val v = TestData.vehicle(id = 7L)
        Mockito.`when`(getVehicleById.handle(7L)).thenReturn(v)

        graphQlTester
            .document(
                """
                query {
                  vehicle(id: 7) {
                    id
                  }
                }
                """.trimIndent()
            )
            .execute()
            .path("vehicle.id").entity(String::class.java).isEqualTo("7")
    }
}
