package com.fleet.management.be.modules.vehicle.graphql

import com.fleet.management.be.modules.vehicle.application.AssignVehicleToUser
import com.fleet.management.be.modules.vehicle.application.CreateVehicle
import com.fleet.management.be.modules.vehicle.application.CreateVehicleCommand
import com.fleet.management.be.modules.vehicle.application.DeleteVehicle
import com.fleet.management.be.modules.vehicle.application.UpdateVehicleStatus
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import com.fleet.management.be.modules.vehicle.infrastructure.graphql.VehicleMutationResolver
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.security.test.context.support.WithMockUser

@GraphQlTest(controllers = [VehicleMutationResolver::class])
class VehicleMutationResolverGraphQLTest {

    @Autowired
    lateinit var graphQlTester: GraphQlTester

    @MockBean
    lateinit var createVehicle: CreateVehicle

    @MockBean
    lateinit var updateVehicleStatus: UpdateVehicleStatus

    @MockBean
    lateinit var deleteVehicle: DeleteVehicle

    @MockBean
    lateinit var assignVehicleToUser: AssignVehicleToUser

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun createVehicle_works_for_admin() {
        val vehicle = Mockito.mock(Vehicle::class.java)
        val cmd = CreateVehicleCommand(
            plateNumber = "AA123BB",
            model = "VW Golf",
            year = 2020,
            status = VehicleStatus.ACTIVE
        )
        Mockito.doReturn(vehicle).`when`(createVehicle).handle(cmd)

        graphQlTester.document(
            """
            mutation {
              createVehicle(input: { plateNumber:"AA123BB", model:"VW Golf", year:2020, status: ACTIVE }) {
                __typename
              }
            }
            """.trimIndent()
        )
            .execute()
            .path("createVehicle.__typename").entity(String::class.java).isEqualTo("Vehicle")
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun updateVehicleStatus() {
        val vehicle = Mockito.mock(Vehicle::class.java)
        Mockito.doReturn(vehicle).`when`(updateVehicleStatus).handle(1L, VehicleStatus.INACTIVE)

        graphQlTester.document("""mutation { updateVehicleStatus(id:1, status: INACTIVE) { __typename } }""")
            .execute()
            .path("updateVehicleStatus.__typename").entity(String::class.java).isEqualTo("Vehicle")
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun deleteVehicle() {
        Mockito.doReturn(true).`when`(deleteVehicle).handle(1L)

        graphQlTester.document("""mutation { deleteVehicle(id:1) }""")
            .execute()
            .path("deleteVehicle").entity(Boolean::class.java).isEqualTo(true)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun assignVehicleToUser() {
        val vehicle = Mockito.mock(Vehicle::class.java)
        Mockito.doReturn(vehicle).`when`(assignVehicleToUser).handle(2L, 1L)

        graphQlTester.document("""mutation { assignVehicleToUser(userId:1, vehicleId:2) { __typename } }""")
            .execute()
            .path("assignVehicleToUser.__typename").entity(String::class.java).isEqualTo("Vehicle")
    }
}
