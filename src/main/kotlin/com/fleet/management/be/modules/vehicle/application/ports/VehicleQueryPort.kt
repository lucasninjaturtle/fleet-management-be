package com.fleet.management.be.modules.vehicle.application.ports

import com.fleet.management.be.modules.vehicle.domain.Vehicle

interface VehicleQueryPort {
    fun findAll(): List<Vehicle>
}
