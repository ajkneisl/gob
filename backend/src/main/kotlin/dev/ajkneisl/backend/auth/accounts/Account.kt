package dev.ajkneisl.backend.auth.accounts

import kotlinx.serialization.Transient

@kotlinx.serialization.Serializable
data class Account(
    val id: String,
    val username: String,
    val nickname: String,
    @Transient val password: String = "",
    val createdAt: Long,
    val role: Int
)