package com.fleet.management.be.modules.vehicle.infrastructure.adapters

import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.infrastructure.jpa.JpaVehicleRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class VehicleCommandAdapter(
    private val repo: JpaVehicleRepository
) : VehicleCommandPort {
    override fun save(vehicle: Vehicle): Vehicle = repo.save(vehicle)
    override fun existsByPlateNumber(plate: String): Boolean = repo.existsByPlateNumber(plate)
    override fun findByPlateNumber(plate: String): Optional<Vehicle> = repo.findByPlateNumber(plate)
}
