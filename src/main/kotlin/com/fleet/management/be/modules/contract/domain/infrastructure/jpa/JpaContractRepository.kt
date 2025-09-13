package com.fleet.management.be.modules.contract.domain.infrastructure.jpa

import com.fleet.management.be.modules.contract.domain.Contract
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaContractRepository : JpaRepository<Contract, Long>
