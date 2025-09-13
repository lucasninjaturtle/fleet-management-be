package com.fleet.management.be.modules.vehicle.application.dto

import com.fleet.management.be.modules.vehicle.domain.VehicleStatus

data class VehicleFilter(
    val status: VehicleStatus? = null,
    val modelContains: String? = null,
    val plateContains: String? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null
)

enum class VehicleSortField { MODEL, YEAR, PLATE_NUMBER }
enum class SortDirection { ASC, DESC }

data class VehicleSort(
    val field: VehicleSortField = VehicleSortField.MODEL,
    val direction: SortDirection = SortDirection.ASC
)
