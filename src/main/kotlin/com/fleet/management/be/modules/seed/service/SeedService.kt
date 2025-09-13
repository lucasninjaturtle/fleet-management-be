package com.fleet.management.be.modules.seed.service

import com.fleet.management.be.modules.contract.domain.Contract
import com.fleet.management.be.modules.contract.domain.ContractStatus
import com.fleet.management.be.modules.contract.domain.infrastructure.jpa.JpaContractRepository
import com.fleet.management.be.modules.driver.domain.Driver
import com.fleet.management.be.modules.driver.domain.DriverStatus
import com.fleet.management.be.modules.driver.domain.infrastructure.jpa.JpaDriverRepository
import com.fleet.management.be.modules.fleet.domain.Fleet
import com.fleet.management.be.modules.fleet.domain.infrastructure.jpa.JpaFleetRepository
import com.fleet.management.be.modules.maintenance.domain.MaintenanceRecord
import com.fleet.management.be.modules.maintenance.domain.MaintenanceType
import com.fleet.management.be.modules.maintenance.domain.infrastructure.jpa.JpaMaintenanceRecordRepository
import com.fleet.management.be.modules.vehicle.domain.Vehicle
import com.fleet.management.be.modules.vehicle.domain.VehicleStatus
import com.fleet.management.be.modules.vehicle.infrastructure.jpa.JpaVehicleRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class SeedService(
    private val fleetRepo: JpaFleetRepository,
    private val vehicleRepo: JpaVehicleRepository,
    private val driverRepo: JpaDriverRepository,
    private val contractRepo: JpaContractRepository,
    private val maintenanceRepo: JpaMaintenanceRecordRepository
) {

    @Transactional
    fun reset() {
        maintenanceRepo.deleteAllInBatch()
        contractRepo.deleteAllInBatch()
        driverRepo.deleteAllInBatch()
        vehicleRepo.deleteAllInBatch()
        fleetRepo.deleteAllInBatch()
    }

    @Transactional
    fun seedFull() {
        reset()

        val f1 = Fleet(name = "VW Argentina - Oeste", description = "Flota regional oeste")
        val f2 = Fleet(name = "VW Argentina - Norte", description = "Flota regional norte")
        val f3 = Fleet(name = "VW Argentina - Cuyo", description = "Flota regional cuyo")
        val f4 = Fleet(name = "VW Argentina - Patagonia", description = "Flota regional patagonia")
        fleetRepo.saveAll(listOf(f1, f2, f3, f4))

        val v1 = Vehicle(
            plateNumber = "AA123BB",
            model = "VW Golf",
            year = 2020,
            status = VehicleStatus.ACTIVE,
            fleet = f1
        )
        val v2 = Vehicle(
            plateNumber = "AC456DD",
            model = "VW Polo",
            year = 2022,
            status = VehicleStatus.ACTIVE,
            fleet = f1
        )
        val v3 = Vehicle(
            plateNumber = "AE789FF",
            model = "VW Amarok",
            year = 2021,
            status = VehicleStatus.INACTIVE,
            fleet = f2
        )
        val v4 = Vehicle(
            plateNumber = "AF321GG",
            model = "VW Virtus",
            year = 2023,
            status = VehicleStatus.ACTIVE,
            fleet = f3
        )
        vehicleRepo.saveAll(listOf(v1, v2, v3, v4))

        val d1 = Driver(
            firstName = "Sofía",
            lastName = "Pérez",
            email = "sofia.perez@example.com",
            phone = "+54 11 5555-1001",
            licenseNumber = "AR-ABC123",
            licenseCategory = "B1",
            licenseExpiry = LocalDate.of(2028, 5, 31),
            status = DriverStatus.ACTIVE,
            fleet = f1,
            vehicle = v1
        )
        val d2 = Driver(
            firstName = "Martín",
            lastName = "Gómez",
            email = "martin.gomez@example.com",
            phone = "+54 11 5555-1002",
            licenseNumber = "AR-XYZ987",
            licenseCategory = "C",
            licenseExpiry = LocalDate.of(2026, 11, 15),
            status = DriverStatus.ACTIVE,
            fleet = f1,
            vehicle = v2
        )
        val d3 = Driver(
            firstName = "Lucía",
            lastName = "Benítez",
            email = "lucia.benitez@example.com",
            phone = "+54 11 5555-1003",
            licenseNumber = "AR-JKL456",
            licenseCategory = "B2",
            licenseExpiry = LocalDate.of(2027, 2, 20),
            status = DriverStatus.SUSPENDED,
            fleet = f2,
            vehicle = v3
        )
        val d4 = Driver(
            firstName = "Diego",
            lastName = "Funes",
            email = "diego.funes@example.com",
            phone = "+54 11 5555-1004",
            licenseNumber = "AR-ZZZ111",
            licenseCategory = "B1",
            licenseExpiry = LocalDate.of(2029, 9, 1),
            status = DriverStatus.INACTIVE,
            fleet = f3,
            vehicle = v4
        )
        driverRepo.saveAll(listOf(d1, d2, d3, d4))

        val c1 = Contract(
            vehicle = v1,
            driver = d1,
            fleet = f1,
            startDate = LocalDate.of(2025, 1, 1),
            endDate = null,
            monthlyRate = BigDecimal("250000.00"),
            mileageLimitKm = 2500,
            status = ContractStatus.ACTIVE
        )
        val c2 = Contract(
            vehicle = v2,
            driver = d2,
            fleet = f1,
            startDate = LocalDate.of(2024, 7, 1),
            endDate = LocalDate.of(2025, 6, 30),
            monthlyRate = BigDecimal("220000.00"),
            mileageLimitKm = 2000,
            status = ContractStatus.TERMINATED
        )
        val c3 = Contract(
            vehicle = v3,
            driver = d3,
            fleet = f2,
            startDate = LocalDate.of(2025, 3, 10),
            endDate = null,
            monthlyRate = BigDecimal("330000.00"),
            mileageLimitKm = 3000,
            status = ContractStatus.ACTIVE
        )
        val c4 = Contract(
            vehicle = v4,
            driver = d4,
            fleet = f3,
            startDate = LocalDate.of(2025, 4, 5),
            endDate = null,
            monthlyRate = BigDecimal("280000.00"),
            mileageLimitKm = 2200,
            status = ContractStatus.PENDING
        )
        contractRepo.saveAll(listOf(c1, c2, c3, c4))

        val m1 = MaintenanceRecord(
            vehicle = v1,
            serviceDate = LocalDate.of(2025, 3, 10),
            odometerKm = 15400,
            type = MaintenanceType.SCHEDULED,
            description = "Service 15k: aceite + filtros",
            cost = BigDecimal("85000.00")
        )
        val m2 = MaintenanceRecord(
            vehicle = v2,
            serviceDate = LocalDate.of(2025, 2, 20),
            odometerKm = 30800,
            type = MaintenanceType.REPAIR,
            description = "Cambio pastillas de freno",
            cost = BigDecimal("120000.00")
        )
        val m3 = MaintenanceRecord(
            vehicle = v3,
            serviceDate = LocalDate.of(2025, 5, 2),
            odometerKm = 41000,
            type = MaintenanceType.INSPECTION,
            description = "Inspección técnica anual",
            cost = BigDecimal("15000.00")
        )
        val m4 = MaintenanceRecord(
            vehicle = v4,
            serviceDate = LocalDate.of(2025, 6, 18),
            odometerKm = 9800,
            type = MaintenanceType.SCHEDULED,
            description = "Service 10k",
            cost = BigDecimal("60000.00")
        )
        maintenanceRepo.saveAll(listOf(m1, m2, m3, m4))
    }
}
