package com.fleet.management.be.modules.vehicle.infrastructure.jpa

import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaVehicleRepository : JpaRepository<Vehicle, Long> {
    fun existsByPlateNumber(plateNumber: String): Boolean
    fun findByPlateNumber(plateNumber: String): Optional<Vehicle>
}
