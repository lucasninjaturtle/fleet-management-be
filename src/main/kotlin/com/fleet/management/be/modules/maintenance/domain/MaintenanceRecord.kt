package com.fleet.management.be.modules.maintenance.domain

import com.fleet.management.be.modules.vehicle.domain.Vehicle
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(
    name = "maintenance_records",
    indexes = [Index(name = "idx_maint_vehicle", columnList = "vehicle_id")]
)
class MaintenanceRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    var vehicle: Vehicle,

    @Column(nullable = false)
    var serviceDate: LocalDate,

    @Column(nullable = true)
    var odometerKm: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: MaintenanceType,

    @Column(nullable = true, length = 500)
    var description: String? = null,

    @Column(nullable = true, precision = 15, scale = 2)
    var cost: BigDecimal? = null
)
