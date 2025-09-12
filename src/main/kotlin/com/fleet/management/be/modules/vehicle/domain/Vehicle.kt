package com.fleet.management.be.modules.vehicle.domain

import jakarta.persistence.*

@Entity
@Table(name = "vehicles", indexes = [Index(name = "idx_plate_unique", columnList = "plate_number", unique = true)])
data class Vehicle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "plate_number", nullable = false, unique = true, length = 32)
    val plateNumber: String,

    @Column(nullable = false, length = 128)
    val model: String,

    @Column(nullable = false)
    val year: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    val status: VehicleStatus = VehicleStatus.ACTIVE
)
