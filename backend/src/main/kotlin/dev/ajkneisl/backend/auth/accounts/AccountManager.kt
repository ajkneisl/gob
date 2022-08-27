package dev.ajkneisl.backend.auth.accounts

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.ajkneisl.backend.*
import dev.ajkneisl.backend.auth.perms.GlobalRoles
import dev.ajkneisl.backend.db.MONGO
import org.litote.kmongo.eq
import java.util.*
import org.mindrot.jbcrypt.BCrypt

object AccountManager {
    /** Get all [Account]s. */
    suspend fun getAccounts(): List<Account> {
        return MONGO.getDatabase("gobspeak").getCollection<Account>().find().toList()
    }

    /**
     * Get an [Account] by it's [id].
     */
    @Throws(NotFound::class)
    suspend fun getAccountById(id: String): Account {
        return MONGO.getDatabase("gobspeak")
            .getCollection<Account>()
            .find(Account::id eq id)
            .first()
            ?: throw NotFound("account")
    }

    /**
     * TODO require reCAPTCHA
     */
    suspend fun authenticate(username: String, password: String): String {
        val acc = getAccounts().firstOrNull { acc -> acc.username.equals(username, true) }
            ?: throw InvalidArguments("account", "There was an issue with your credentials!")

        if (BCrypt.checkpw(password, acc.password))
            return generateJwt(acc.id)
        else throw InvalidArguments("account", "There was an issue with your credentials!")
    }

    /** If [name] is taken */
    private suspend fun isUsernameTaken(name: String): Boolean =
        getAccounts().any { acc -> acc.username.equals(name, true) }

    /**
     * Create an account with [username] and [password]
     *
     * TODO add checks to make sure password contains proper characters
     * TODO ensure UUID hasn't been used before
     */
    suspend fun createAccount(username: String, password: String): String {
        when {
            !(3..16).contains(username.length) ->
                throw InvalidArguments(
                    "username",
                    "Make sure your username is within 3 and 16 characters!"
                )
            password.length > 8 ->
                throw InvalidArguments(
                    "password",
                    "Make sure your password is more than 8 characters!"
                )
            isUsernameTaken(username) ->
                throw InvalidArguments("username", "That username is already taken!")
        }

        val account =
            Account(
                UUID.randomUUID().toString(),
                username,
                username,
                BCrypt.hashpw(password, BCrypt.gensalt()),
                System.currentTimeMillis(),
                GlobalRoles.DEFAULT
            )

        MONGO.getDatabase("gobspeak").getCollection<Account>().insertOne(account)

        return generateJwt(account.id)
    }

    /** Generate a JWT for [id] */
    fun generateJwt(id: String, expireTime: Long = 60000): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("id", id)
            .withExpiresAt(Date(System.currentTimeMillis() + expireTime))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}
