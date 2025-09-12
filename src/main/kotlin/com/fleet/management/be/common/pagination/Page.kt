package com.fleet.management.be.common.pagination

data class PageRequest(val limit: Int = 20, val offset: Int = 0) {
    init {
        require(limit in 1..1000) { "limit must be 1..1000" }
        require(offset >= 0) { "offset must be >= 0" }
    }
}

data class Page<T>(
    val items: List<T>,
    val total: Long,
    val limit: Int,
    val offset: Int
)
