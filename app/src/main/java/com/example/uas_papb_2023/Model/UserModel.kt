package com.example.uas_papb_2023.Model

data class UserModel(
    val id: String?,
    val email: String?,
    val password: String?,
    val userRole: UserRole?
)

enum class UserRole {
    ADMIN,
    USER;
}
