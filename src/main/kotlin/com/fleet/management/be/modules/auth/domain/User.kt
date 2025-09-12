package com.fleet.management.be.modules.auth.domain

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(
    name = "users",
    indexes = [Index(name = "idx_username_unique", columnList = "username", unique = true)]
)
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 64)
    @NotBlank val username: String,

    @Column(nullable = false)
    @NotBlank val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    val role: Role = Role.USER
)
