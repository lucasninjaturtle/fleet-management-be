package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

private class FakePort2 : VehicleCommandPort {
    var store: MutableMap<Long, Vehicle> = mutableMapOf()
    var lastSaved: Vehicle? = null

    override fun existsByPlateNumber(plate: String) = store.values.any { it.plateNumber == plate }
    override fun findByPlateNumber(plate: String): Optional<Vehicle> {
        TODO("Not yet implemented")
    }

    override fun save(vehicle: Vehicle): Vehicle {
        val v = if (vehicle.id == null) vehicle.copy(id = 1L) else vehicle
        store[v.id!!] = v
        lastSaved = v
        return v
    }

    override fun findById(id: Long): Optional<Vehicle> = Optional.ofNullable(store[id])

    override fun deleteById(id: Long) { store.remove(id) }
}

class UpdateVehicleStatusTest {

    @Test
    fun `updates status`() {
        val existing = Vehicle(id = 5L, plateNumber = "AA000AA", model = "Nivus", year = 2023, status = VehicleStatus.ACTIVE)
        val port = FakePort2().apply { store[existing.id!!] = existing }
        val useCase = UpdateVehicleStatus(port)

        val updated = useCase.handle(5L, VehicleStatus.INACTIVE)

        assertThat(updated.status).isEqualTo(VehicleStatus.INACTIVE)
        assertThat(port.lastSaved!!.status).isEqualTo(VehicleStatus.INACTIVE)
    }
}
