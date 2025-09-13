package com.fleet.management.be.modules.fleet.domain.infrastructure.jpa

import com.fleet.management.be.modules.fleet.domain.Fleet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaFleetRepository : JpaRepository<Fleet, Long>
