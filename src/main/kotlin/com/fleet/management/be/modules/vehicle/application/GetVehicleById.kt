package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.errors.NotFound
import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.stereotype.Service

@Service
class GetVehicleById(private val queries: VehicleQueryPort) {
    fun handle(id: Long): Vehicle =
        queries.findById(id).orElseThrow { NotFound("Vehicle $id not found") }
}
