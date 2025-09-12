package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.errors.NotFound
import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AssignVehicleToUser(
    private val commands: VehicleCommandPort
) {
    @Transactional
    fun handle(vehicleId: Long, userId: Long): Vehicle {
        val v = commands.findById(vehicleId).orElseThrow { NotFound("Vehicle $vehicleId not found") }
        val assigned = v.copy(assignedUserId = userId)
        return commands.save(assigned)
    }
}
