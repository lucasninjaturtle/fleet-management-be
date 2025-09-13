package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.stereotype.Service

@Service
class ListVehicles(private val port: VehicleQueryPort) {
    fun handle(): List<Vehicle> = port.findAll()
}
