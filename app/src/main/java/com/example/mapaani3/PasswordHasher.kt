package com.example.mapaani3

import java.security.MessageDigest

/**
 * A simple SHA-256 password hasher for school project purposes.
 * Note: In a real production app, use Argon2 or BCrypt with a unique salt per user.
 */
object PasswordHasher {
    fun hash(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
