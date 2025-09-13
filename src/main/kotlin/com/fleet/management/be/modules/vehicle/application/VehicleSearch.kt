package com.fleet.management.be.modules.vehicle.application

import com.fleet.management.be.common.pagination.Page
import com.fleet.management.be.common.pagination.PageRequest
import com.fleet.management.be.modules.vehicle.application.dto.VehicleFilter
import com.fleet.management.be.modules.vehicle.application.dto.VehicleSort
import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import org.springframework.stereotype.Service

@Service
class VehicleSearch(private val port: VehicleQueryPort) {
    fun handle(filter: VehicleFilter, page: PageRequest, sort: VehicleSort): Page<Vehicle> =
        port.findPage(filter, page, sort)
}
