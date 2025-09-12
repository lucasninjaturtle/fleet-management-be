package com.fleet.management.be.common

import com.fleet.management.be.modules.auth.domain.Role
import com.fleet.management.be.modules.auth.domain.User
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus

object TestData {
    fun vehicle(
        id: Long? = 1L,
        plate: String = "AA123BB",
        model: String = "VW Golf",
        year: Int = 2020,
        status: VehicleStatus = VehicleStatus.ACTIVE,
        assignedUserId: Long? = null
    ) = Vehicle(id = id, plateNumber = plate, model = model, year = year, status = status, assignedUserId = assignedUserId)

    fun user(
        id: Long? = 1L,
        username: String = "user",
        password: String = "$2a$10\$dummy_hash",
        role: Role = Role.USER
    ) = User(id = id, username = username, password = password, role = role)
}
