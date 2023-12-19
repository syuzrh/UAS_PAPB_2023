package com.example.uas_papb_2023.Model

data class UserModel(
    val id: String?,  // Menambahkan properti id
    val email: String?,
    val password: String?,  // Menambahkan properti password
    val userRole: UserRole?  // Menambahkan properti userRole
) {
    // Menambahkan konstruktor tambahan tanpa id untuk digunakan saat registrasi
    constructor(email: String?, password: String?, userRole: UserRole?) :
            this(null, email, password, userRole)
}

// Enum class untuk UserRole
enum class UserRole {
    ADMIN,
    USER;

    companion object {
        fun fromString(role: String): UserRole {
            return when (role.toUpperCase()) {
                "ADMIN" -> ADMIN
                "USER" -> USER
                else -> USER // Default role, sesuaikan dengan kebutuhan Anda
            }
        }
    }
}
