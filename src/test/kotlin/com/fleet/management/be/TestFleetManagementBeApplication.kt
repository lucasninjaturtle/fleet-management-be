package com.fleet.management.be

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<FleetManagementBeApplication>().with(TestcontainersConfiguration::class).run(*args)
}
