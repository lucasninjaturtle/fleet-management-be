package com.fleet.management.be.modules.vehicle.application.ports

import com.fleet.management.be.common.pagination.Page
import com.fleet.management.be.common.pagination.PageRequest
import com.fleet.management.be.modules.vehicle.application.dto.VehicleFilter
import com.fleet.management.be.modules.vehicle.application.dto.VehicleSort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import java.util.*

interface VehicleQueryPort {
    fun findAll(): List<Vehicle>
    fun findById(id: Long): Optional<Vehicle>
    fun findPage(filter: VehicleFilter, page: PageRequest, sort: VehicleSort): Page<Vehicle>
}
