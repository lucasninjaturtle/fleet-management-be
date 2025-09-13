package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.errors.Conflict
import com.fleet.management.be.modules.vehicle.application.ports.VehicleCommandPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.springframework.stereotype.Service
import java.time.Year

data class CreateVehicleCommand(
    val plateNumber: String,
    val model: String,
    val year: Int,
    val status: VehicleStatus
)

@Service
class CreateVehicle(
    private val commands: VehicleCommandPort
) {
    fun handle(cmd: CreateVehicleCommand): Vehicle {
        validate(cmd)
        if (commands.existsByPlateNumber(cmd.plateNumber)) {
            throw Conflict("Vehicle with plate '${cmd.plateNumber}' already exists")
        }
        val entity = Vehicle(
            plateNumber = cmd.plateNumber.trim().uppercase(),
            model = cmd.model.trim(),
            year = cmd.year,
            status = cmd.status
        )
        return commands.save(entity)
    }

    private fun validate(cmd: CreateVehicleCommand) {
        require(cmd.plateNumber.isNotBlank()) { "plateNumber is required" }
        require(cmd.model.isNotBlank()) { "model is required" }
        val current = Year.now().value + 1
        require(cmd.year in 1900..current) { "year must be between 1900 and $current" }
    }
}
