package ru.dikoresearch.domain.security.token

interface TokenService {
    fun generate(tokenConfig: TokenConfig, vararg tokenClaims: TokenClaim): String
}