package dev.ajkneisl.backend

open class GobError(message: String, val code: Short = 400): Throwable(message)

class NotFound(type: String): GobError("That $type could not be found!")

class InvalidArguments(arg: String, fix: String): GobError("$arg: $fix")

class MissingArguments(vararg missing: String): GobError("Missing arguments: ${missing.joinToString(", ")}")