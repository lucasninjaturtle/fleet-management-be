package com.fleet.management.be.modules.vehicle.infrastructure.graphql

import com.fleet.management.be.modules.vehicle.application.GetVehicleById
import com.fleet.management.be.modules.vehicle.application.ListVehicles
import com.fleet.management.be.modules.vehicle.application.VehicleSearch
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser

@GraphQlTest(controllers = [VehicleResolver::class])
class VehicleResolverGraphQLTest {

    @Autowired
    lateinit var graphQlTester: GraphQlTester

    @MockBean
    lateinit var listVehicles: ListVehicles

    @MockBean
    lateinit var vehicleSearch: VehicleSearch

    @MockBean
    lateinit var getVehicleById: GetVehicleById

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun vehicles_returns_empty_list() {
        Mockito.`when`(listVehicles.handle()).thenReturn(emptyList())

        graphQlTester
            .document(
                """
                query {
                  vehicles { id }
                }
                """.trimIndent()
            )
            .execute()
            .path("vehicles")
            .entityList(Any::class.java)
            .hasSize(0)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun vehicle_by_id_returns_item_typename() {
        val v = Mockito.mock(Vehicle::class.java)
        Mockito.`when`(getVehicleById.handle(42L)).thenReturn(v)

        graphQlTester
            .document(
                """
                query {
                  vehicle(id: 42) { __typename }
                }
                """.trimIndent()
            )
            .execute()
            .path("vehicle.__typename")
            .entity(String::class.java)
            .isEqualTo("Vehicle")
    }
}
