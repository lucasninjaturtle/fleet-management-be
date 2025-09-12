package com.fleet.management.be.modules.vehicle.graphql

import com.fleet.management.be.common.TestData
import com.fleet.management.be.config.TestSecurityConfig
import com.fleet.management.be.modules.vehicle.application.GetVehicleById
import com.fleet.management.be.modules.vehicle.application.ListVehicles
import com.fleet.management.be.modules.vehicle.infrastructure.graphql.VehicleResolverGraphQL
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.FilterType
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser

@GraphQlTest(
    controllers = [VehicleResolverGraphQL::class],
    excludeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [TestSecurityConfig::class])]
)
@ImportAutoConfiguration(exclude = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class])
class VehicleResolverGraphQLTest {

    @Autowired
    lateinit var graphQlTester: GraphQlTester

    @MockBean
    lateinit var listVehicles: ListVehicles

    @MockBean
    lateinit var getVehicleById: GetVehicleById

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun vehicles_returns_list() {
        val v1 = TestData.vehicle(id = 1L, plateNumber = "AA111AA")
        val v2 = TestData.vehicle(id = 2L, plateNumber = "BB222BB")
        Mockito.doReturn(listOf(v1, v2)).`when`(listVehicles).handle()

        graphQlTester.document(
            """
            query {
              vehicles {
                id
                plateNumber
              }
            }
            """.trimIndent()
        )
            .execute()
            .path("vehicles[0].id").entity(String::class.java).isEqualTo("1")
            .path("vehicles[0].plateNumber").entity(String::class.java).isEqualTo("AA111AA")
            .path("vehicles[1].id").entity(String::class.java).isEqualTo("2")
            .path("vehicles[1].plateNumber").entity(String::class.java).isEqualTo("BB222BB")
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun vehicle_by_id() {
        val v = TestData.vehicle(id = 7L, plateNumber = "ZZ777ZZ")
        Mockito.doReturn(v).`when`(getVehicleById).handle(7L)

        graphQlTester.document(
            """
            query {
              vehicle(id: 7) {
                id
                plateNumber
              }
            }
            """.trimIndent()
        )
            .execute()
            .path("vehicle.id").entity(String::class.java).isEqualTo("7")
            .path("vehicle.plateNumber").entity(String::class.java).isEqualTo("ZZ777ZZ")
    }
}
