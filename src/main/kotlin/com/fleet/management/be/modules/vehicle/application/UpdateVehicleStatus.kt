package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.errors.NotFound
import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateVehicleStatus(
    private val commands: VehicleCommandPort
) {
    @Transactional
    fun handle(id: Long, status: VehicleStatus): Vehicle {
        val current = commands.findById(id).orElseThrow { NotFound("Vehicle $id not found") }
        val updated = current.copy(status = status)
        return commands.save(updated)
    }
}
