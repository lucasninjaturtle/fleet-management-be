package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

private class FakeVehicleCommandPort : VehicleCommandPort {
    var exists = false
    var saved: Vehicle? = null
    var findByIdMap: MutableMap<Long, Vehicle> = mutableMapOf()
    var deletedId: Long? = null

    override fun existsByPlateNumber(plate: String): Boolean = exists
    override fun findByPlateNumber(plate: String): Optional<Vehicle> {
        TODO("Not yet implemented")
    }

    override fun save(vehicle: Vehicle): Vehicle {
        val withId = if (vehicle.id == null) vehicle.copy(id = 1L) else vehicle
        saved = withId
        findByIdMap[withId.id!!] = withId
        return withId
    }

    override fun findById(id: Long): Optional<Vehicle> =
        Optional.ofNullable(findByIdMap[id])

    override fun deleteById(id: Long) {
        deletedId = id
        findByIdMap.remove(id)
    }
}

class CreateVehicleTest {

    @Test
    fun `creates vehicle when plate is unique`() {
        val port = FakeVehicleCommandPort().apply { exists = false }
        val useCase = CreateVehicle(port)

        val cmd = CreateVehicleCommand(
            plateNumber = "aa123bb",
            model = "VW Golf",
            year = 2020,
            status = VehicleStatus.ACTIVE
        )

        val out = useCase.handle(cmd)

        assertThat(out.id).isNotNull()
        assertThat(out.plateNumber).isEqualTo("AA123BB")
        assertThat(out.model).isEqualTo("VW Golf")
        assertThat(port.saved!!.plateNumber).isEqualTo("AA123BB")
    }
}
