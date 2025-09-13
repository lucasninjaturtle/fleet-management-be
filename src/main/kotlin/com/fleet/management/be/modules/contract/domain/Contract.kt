package com.fleet.management.be.modules.contract.domain

import com.fleet.management.be.modules.driver.domain.Driver
import com.fleet.management.be.modules.fleet.domain.Fleet
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(
    name = "contracts",
    indexes = [
        Index(name = "idx_contracts_fleet", columnList = "fleet_id"),
        Index(name = "idx_contracts_vehicle", columnList = "vehicle_id"),
        Index(name = "idx_contracts_driver", columnList = "driver_id")
    ]
)
class Contract(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    var vehicle: Vehicle,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    var driver: Driver,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fleet_id", nullable = false)
    var fleet: Fleet,

    @Column(nullable = false)
    var startDate: LocalDate,

    @Column
    var endDate: LocalDate? = null,

    @Column(nullable = false, precision = 15, scale = 2)
    var monthlyRate: BigDecimal,

    @Column
    var mileageLimitKm: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: ContractStatus = ContractStatus.ACTIVE
)
