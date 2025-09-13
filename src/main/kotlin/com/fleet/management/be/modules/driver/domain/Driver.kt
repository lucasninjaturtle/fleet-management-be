package com.fleet.management.be.modules.driver.domain

import com.fleet.management.be.modules.fleet.domain.Fleet
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "drivers",
    indexes = [Index(name = "idx_drivers_email", columnList = "email", unique = true)]
)
class Driver(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 80)
    var firstName: String,

    @Column(nullable = false, length = 80)
    var lastName: String,

    @Column(length = 160)
    var email: String? = null,

    @Column(length = 40)
    var phone: String? = null,

    @Column(length = 60)
    var licenseNumber: String? = null,

    @Column(length = 20)
    var licenseCategory: String? = null,

    var licenseExpiry: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: DriverStatus = DriverStatus.ACTIVE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fleet_id")
    var fleet: Fleet? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    var vehicle: Vehicle? = null
)
