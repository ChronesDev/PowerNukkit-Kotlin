package cn.nukkit.utils

import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EventException : RuntimeException {
    /**
     * If applicable, returns the Exception that triggered this Exception
     *
     * @return Inner exception, or null if one does not exist
     */
    @get:Override
    val cause: Throwable?

    /**
     * Constructs a new EventException based on the given Exception
     *
     * @param throwable Exception that triggered this Exception
     */
    constructor(throwable: Throwable?) {
        cause = throwable
    }

    /**
     * Constructs a new EventException
     */
    constructor() {
        cause = null
    }

    /**
     * Constructs a new EventException with the given message
     *
     * @param cause   The exception that caused this
     * @param message The message
     */
    constructor(cause: Throwable?, message: String?) : super(message) {
        this.cause = cause
    }

    /**
     * Constructs a new EventException with the given message
     *
     * @param message The message
     */
    constructor(message: String?) : super(message) {
        cause = null
    }
}