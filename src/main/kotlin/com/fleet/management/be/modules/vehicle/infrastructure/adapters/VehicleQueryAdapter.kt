package com.fleet.management.be.modules.vehicle.infrastructure.adapters

import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.infrastructure.jpa.JpaVehicleRepository
import org.springframework.stereotype.Component

@Component
class VehicleQueryAdapter(
    private val repo: JpaVehicleRepository
) : VehicleQueryPort {
    override fun findAll(): List<Vehicle> = repo.findAll()
}
