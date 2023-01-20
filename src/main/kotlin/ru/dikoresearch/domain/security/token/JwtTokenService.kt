package ru.dikoresearch.domain.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService: TokenService {
    override fun generate(tokenConfig: TokenConfig, vararg tokenClaims: TokenClaim): String {
        var token = JWT.create()
            .withAudience(tokenConfig.audience)
            .withIssuer(tokenConfig.issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + tokenConfig.expiresIn))

        tokenClaims.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }

        return token.sign(Algorithm.HMAC256(tokenConfig.secret))
    }
}