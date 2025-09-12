package com.fleet.management.be.modules.vehicle.infrastructure.jpa

import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.data.jpa.repository.JpaRepository

interface JpaVehicleRepository : JpaRepository<Vehicle, Long>
