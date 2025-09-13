package com.fleet.management.be.modules.fleet.domain

import com.fleet.management.be.modules.vehicle.domain.Vehicle
import jakarta.persistence.*

@Entity
@Table(name = "fleets")
class Fleet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 120)
    var name: String,

    @Column(length = 500)
    var description: String? = null,

    @OneToMany(mappedBy = "fleet", cascade = [CascadeType.PERSIST, CascadeType.MERGE], orphanRemoval = false)
    var vehicles: MutableList<Vehicle> = mutableListOf()
)
