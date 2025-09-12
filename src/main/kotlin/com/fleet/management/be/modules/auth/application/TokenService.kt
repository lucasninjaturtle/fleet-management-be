package com.fleet.management.be.modules.auth.application

interface TokenService {
    fun generate(username: String, roles: List<String>): String
    fun validate(token: String): Boolean
    fun username(token: String): String?
    fun roles(token: String): List<String>
}