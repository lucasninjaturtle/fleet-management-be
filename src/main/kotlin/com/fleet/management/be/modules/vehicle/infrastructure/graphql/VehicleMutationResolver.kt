package com.fleet.management.be.modules.vehicle.infrastructure.graphql

import com.fleet.management.be.modules.vehicle.application.CreateVehicle
import com.fleet.management.be.modules.vehicle.application.CreateVehicleCommand
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

data class CreateVehicleInput(
    val plateNumber: String,
    val model: String,
    val year: Int,
    val status: VehicleStatus = VehicleStatus.ACTIVE
)

@Controller
class VehicleMutationResolver(
    private val createVehicle: CreateVehicle
) {
    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    fun createVehicle(@Argument input: CreateVehicleInput): Vehicle =
        createVehicle.handle(
            CreateVehicleCommand(
                plateNumber = input.plateNumber,
                model = input.model,
                year = input.year,
                status = input.status
            )
        )
}
