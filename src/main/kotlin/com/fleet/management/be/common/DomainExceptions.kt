package com.fleet.management.be.common.errors

class NotFound(message: String) : RuntimeException(message)
class Conflict(message: String) : RuntimeException(message)
