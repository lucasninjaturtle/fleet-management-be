package com.fleet.management.be.modules.maintenance.domain.infrastructure.jpa

import com.fleet.management.be.modules.maintenance.domain.MaintenanceRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaMaintenanceRecordRepository : JpaRepository<MaintenanceRecord, Long>
