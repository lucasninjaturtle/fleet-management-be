package com.fleet.management.be.modules.auth.infrastructure.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.fleet.management.be.modules.auth.application.TokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Service
class JwtService(
    private val mapper: ObjectMapper,
    @Value("\${app.jwt.secret:changeit}") private val secret: String,
    @Value("\${app.jwt.expSeconds:3600}") private val expSeconds: Long
) : TokenService {

    override fun generate(username: String, roles: List<String>): String {
        val header = mapOf("alg" to "HS256", "typ" to "JWT")
        val now = Instant.now().epochSecond
        val payload = mapOf("sub" to username, "roles" to roles, "iat" to now, "exp" to (now + expSeconds))
        val h = b64(mapper.writeValueAsBytes(header))
        val p = b64(mapper.writeValueAsBytes(payload))
        val s = sign("$h.$p")
        return "$h.$p.$s"
    }

    override fun validate(token: String): Boolean {
        val parts = token.split("."); if (parts.size != 3) return false
        val (h, p, s) = parts
        if (s != sign("$h.$p")) return false
        val node = mapper.readTree(Base64.getUrlDecoder().decode(p))
        val exp = node.get("exp")?.asLong() ?: return false
        return Instant.now().epochSecond < exp
    }

    override fun username(token: String): String? =
        runCatching {
            mapper.readTree(Base64.getUrlDecoder().decode(token.split(".")[1])).get("sub")?.asText()
        }.getOrNull()

    override fun roles(token: String): List<String> =
        runCatching {
            mapper.readTree(Base64.getUrlDecoder().decode(token.split(".")[1])).get("roles")?.map { it.asText() } ?: emptyList()
        }.getOrDefault(emptyList())

    private fun sign(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        return b64(mac.doFinal(data.toByteArray(StandardCharsets.UTF_8)))
    }
    private fun b64(b: ByteArray) = Base64.getUrlEncoder().withoutPadding().encodeToString(b)
}
