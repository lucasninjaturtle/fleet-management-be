package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

private class FakePort3 : VehicleCommandPort {
    var store: MutableMap<Long, Vehicle> = mutableMapOf()
    var deleted: Long? = null

    override fun existsByPlateNumber(plate: String) = store.values.any { it.plateNumber == plate }
    override fun findByPlateNumber(plate: String): Optional<Vehicle> {
        TODO("Not yet implemented")
    }

    override fun save(vehicle: Vehicle): Vehicle {
        val v = if (vehicle.id == null) vehicle.copy(id = 1L) else vehicle
        store[v.id!!] = v
        return v
    }

    override fun findById(id: Long): Optional<Vehicle> = Optional.ofNullable(store[id])

    override fun deleteById(id: Long) { deleted = id; store.remove(id) }
}

class DeleteVehicleTest {

    @Test
    fun `deletes when exists`() {
        val v = Vehicle(id = 10L, plateNumber = "AA111AA", model = "Taos", year = 2022, status = VehicleStatus.ACTIVE)
        val port = FakePort3().apply { store[v.id!!] = v }
        val useCase = DeleteVehicle(port)

        useCase.handle(10L)

        assertThat(port.deleted).isEqualTo(10L)
        assertThat(port.store.containsKey(10L)).isFalse()
    }

    @Test
    fun `throws when missing`() {
        val port = FakePort3()
        val useCase = DeleteVehicle(port)

        val ex = assertThrows(RuntimeException::class.java) {
            useCase.handle(999L)
        }
        // assertThat(ex.message).contains("not found")
    }
}
