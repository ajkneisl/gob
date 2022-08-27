package dev.ajkneisl.backend.auth

import dev.ajkneisl.backend.GobError

open class AuthenticationError(message: String = "There was an issue authorizing this request!") : GobError(message, 401)

class NotAuthenticated : AuthenticationError("You must be authorized for this!")