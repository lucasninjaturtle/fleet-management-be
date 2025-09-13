package com.fleet.management.be.modules.vehicle.application.ports

import com.fleet.management.be.modules.vehicle.domain.Vehicle
import java.util.*

interface VehicleCommandPort {
    fun save(vehicle: Vehicle): Vehicle
    fun existsByPlateNumber(plate: String): Boolean
    fun findByPlateNumber(plate: String): Optional<Vehicle>
    fun findById(id: Long): Optional<Vehicle>
    fun deleteById(id: Long)
}