package com.fleet.management.be.modules.vehicle.infrastructure.adapters

import com.fleet.management.be.common.pagination.Page
import com.fleet.management.be.common.pagination.PageRequest
import com.fleet.management.be.modules.vehicle.application.dto.SortDirection
import com.fleet.management.be.modules.vehicle.application.dto.VehicleFilter
import com.fleet.management.be.modules.vehicle.application.dto.VehicleSort
import com.fleet.management.be.modules.vehicle.application.dto.VehicleSortField
import com.fleet.management.be.modules.vehicle.application.ports.VehicleQueryPort
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import com.fleet.management.be.modules.vehicle.infrastructure.jpa.JpaVehicleRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.util.*

@Component
class VehicleQueryAdapter(
    private val repo: JpaVehicleRepository,
    private val em: EntityManager
) : VehicleQueryPort {

    override fun findAll(): List<Vehicle> = repo.findAll()
    override fun findById(id: Long): Optional<Vehicle> = repo.findById(id)

    override fun findByAssignedUser(userId: Long): List<Vehicle> =
        repo.findAllByAssignedUserId(userId)

    override fun countByAssignedUserAndStatus(userId: Long, status: VehicleStatus): Long =
        repo.countByAssignedUserIdAndStatus(userId, status)

    override fun findPage(filter: VehicleFilter, page: PageRequest, sort: VehicleSort): Page<Vehicle> {
        val where = StringBuilder(" FROM Vehicle v WHERE 1=1 ")
        val params = mutableMapOf<String, Any>()

        filter.status?.let { where.append(" AND v.status = :status"); params["status"] = it }
        filter.modelContains?.takeIf { it.isNotBlank() }?.let {
            where.append(" AND LOWER(v.model) LIKE :model"); params["model"] = "%${it.lowercase()}%"
        }
        filter.plateContains?.takeIf { it.isNotBlank() }?.let {
            where.append(" AND LOWER(v.plateNumber) LIKE :plate"); params["plate"] = "%${it.lowercase()}%"
        }
        filter.yearFrom?.let { where.append(" AND v.year >= :yf"); params["yf"] = it }
        filter.yearTo?.let { where.append(" AND v.year <= :yt"); params["yt"] = it }

        val sortField = when (sort.field) {
            VehicleSortField.MODEL -> "v.model"
            VehicleSortField.YEAR -> "v.year"
            VehicleSortField.PLATE_NUMBER -> "v.plateNumber"
        }
        val order = if (sort.direction == SortDirection.DESC) "DESC" else "ASC"

        val dataQuery = em.createQuery("SELECT v $where ORDER BY $sortField $order", Vehicle::class.java)
        params.forEach { (k, v) -> dataQuery.setParameter(k, v) }
        dataQuery.firstResult = page.offset
        dataQuery.maxResults = page.limit
        val items = dataQuery.resultList

        val countQuery = em.createQuery("SELECT COUNT(v.id) $where", java.lang.Long::class.java)
        params.forEach { (k, v) -> countQuery.setParameter(k, v) }
        val total = (countQuery.singleResult as Number).toLong()

        return Page(items, total, page.limit, page.offset)
    }
}
