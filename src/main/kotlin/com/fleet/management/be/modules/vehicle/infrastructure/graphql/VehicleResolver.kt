package com.fleet.management.be.modules.vehicle.infrastructure.graphql

import com.fleet.management.be.modules.vehicle.application.ListVehicles
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class VehicleResolver(
    private val listVehicles: ListVehicles
) {
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @QueryMapping
    fun vehicles(): List<Vehicle> = listVehicles.handle()
}
