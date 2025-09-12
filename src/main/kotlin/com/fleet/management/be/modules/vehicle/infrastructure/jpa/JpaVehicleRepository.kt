package com.fleet.management.be.modules.vehicle.infrastructure.jpa

import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.springframework.data.jpa.repository.JpaRepository

interface JpaVehicleRepository : JpaRepository<Vehicle, Long> {
    fun existsByPlateNumber(plateNumber: String): Boolean
    fun findByPlateNumber(plateNumber: String): java.util.Optional<Vehicle>

    // NUEVOS
    fun findAllByAssignedUserId(userId: Long): List<Vehicle>
    fun countByAssignedUserIdAndStatus(userId: Long, status: VehicleStatus): Long
}
