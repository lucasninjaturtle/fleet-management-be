package com.fleet.management.be.modules.auth.application

interface PasswordHasher {
    fun encode(raw: String): String
    fun matches(raw: String, encoded: String): Boolean
}