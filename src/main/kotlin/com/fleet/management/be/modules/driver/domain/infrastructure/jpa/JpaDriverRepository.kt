package com.fleet.management.be.modules.driver.domain.infrastructure.jpa

import com.fleet.management.be.modules.driver.domain.Driver
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaDriverRepository : JpaRepository<Driver, Long>
