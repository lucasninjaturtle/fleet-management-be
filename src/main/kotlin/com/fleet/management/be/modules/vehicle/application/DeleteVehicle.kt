package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.errors.NotFound
import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteVehicle(
    private val commands: VehicleCommandPort
) {
    @Transactional
    fun handle(id: Long): Boolean {
        commands.findById(id).orElseThrow { NotFound("Vehicle $id not found") }
        commands.deleteById(id)
        return true
    }
}
