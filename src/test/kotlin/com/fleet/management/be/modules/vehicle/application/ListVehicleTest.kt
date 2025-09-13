package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.pagination.Page
import com.fleet.management.be.common.pagination.PageRequest
import com.fleet.management.be.modules.vehicle.application.dto.VehicleFilter
import com.fleet.management.be.modules.vehicle.application.dto.VehicleSort
import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

private class FakeQueryPort : VehicleQueryPort {
    var items: List<Vehicle> = emptyList()

    override fun findAll(): List<Vehicle> = items

    override fun findById(id: Long): Optional<Vehicle> =
        Optional.ofNullable(items.firstOrNull { it.id == id })

    override fun findPage(
        filter: VehicleFilter,
        page: PageRequest,
        sort: VehicleSort
    ): Page<Vehicle> {
        TODO("Not yet implemented")
    }

    override fun findByAssignedUser(userId: Long): List<Vehicle> = items.filter { it.assignedUserId == userId }
    override fun countByAssignedUserAndStatus(userId: Long, status: VehicleStatus): Long =
        items.count { it.assignedUserId == userId && it.status == status }.toLong()
}

class ListVehiclesTest {

    @Test
    fun `returns all vehicles from port`() {
        val port = FakeQueryPort().apply {
            items = listOf(
                Vehicle(id = 1L, plateNumber = "AA000AA", model = "Polo", year = 2021, status = VehicleStatus.ACTIVE),
                Vehicle(id = 2L, plateNumber = "AB123CD", model = "T-Cross", year = 2020, status = VehicleStatus.INACTIVE)
            )
        }
        val useCase = ListVehicles(port)

        val result = useCase.handle()

        assertThat(result).hasSize(2)
        assertThat(result.map { it.plateNumber }).containsExactly("AA000AA", "AB123CD")
    }
}
