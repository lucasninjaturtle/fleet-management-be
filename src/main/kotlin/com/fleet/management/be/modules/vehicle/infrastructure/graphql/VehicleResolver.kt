package com.fleet.management.be.modules.vehicle.infrastructure.graphql

import com.fleet.management.be.common.pagination.Page
import com.fleet.management.be.common.pagination.PageRequest
import com.fleet.management.be.modules.vehicle.application.GetVehicleById
import com.fleet.management.be.modules.vehicle.application.ListVehicles
import com.fleet.management.be.modules.vehicle.application.VehicleSearch
import com.fleet.management.be.modules.vehicle.application.dto.*
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

data class PageInput(val limit: Int = 20, val offset: Int = 0) { fun toDomain() = PageRequest(limit, offset) }
data class VehicleFilterInput(
    val status: VehicleStatus? = null,
    val modelContains: String? = null,
    val plateContains: String? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null
) { fun toDomain() = VehicleFilter(status, modelContains, plateContains, yearFrom, yearTo) }
data class VehicleSortInput(
    val field: VehicleSortField = VehicleSortField.MODEL,
    val direction: SortDirection = SortDirection.ASC
) { fun toDomain() = VehicleSort(field, direction) }

data class VehiclePageGQL(val items: List<Vehicle>, val total: Int, val limit: Int, val offset: Int)
private fun Page<Vehicle>.toGql() = VehiclePageGQL(items, total.toInt(), limit, offset)

@Controller
class VehicleResolver(
    private val listVehicles: ListVehicles,
    private val vehicleSearch: VehicleSearch,
    private val getVehicleById: GetVehicleById
) {
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @QueryMapping
    fun vehicles(): List<Vehicle> = listVehicles.handle()

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @QueryMapping
    fun vehiclesPage(
        @Argument filter: VehicleFilterInput?,
        @Argument page: PageInput?,
        @Argument sort: VehicleSortInput?
    ): VehiclePageGQL =
        vehicleSearch.handle(
            (filter ?: VehicleFilterInput()).toDomain(),
            (page ?: PageInput()).toDomain(),
            (sort ?: VehicleSortInput()).toDomain()
        ).toGql()

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @QueryMapping
    fun vehicle(@Argument id: Long): Vehicle = getVehicleById.handle(id)
}
