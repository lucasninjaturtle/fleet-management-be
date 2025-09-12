package com.fleet.management.be.modules.vehicle.domain

import jakarta.persistence.*
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus

@Entity
@Table(
    name = "vehicles",
    indexes = [Index(name = "idx_plate_unique", columnList = "plate_number", unique = true)]
)
data class Vehicle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "plate_number", nullable = false, unique = true)
    val plateNumber: String,

    @Column(nullable = false)
    val model: String,

    @Column(nullable = false)
    val year: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: VehicleStatus = VehicleStatus.ACTIVE,

    @Column(name = "assigned_user_id")
    val assignedUserId: Long? = null
)
