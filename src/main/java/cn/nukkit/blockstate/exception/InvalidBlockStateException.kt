package cn.nukkit.blockstate.exception

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
class InvalidBlockStateException : IllegalStateException {
    @Nonnull
    private val state: BlockState

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull state: BlockState) : super(createMessage(state, null)) {
        this.state = state
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull state: BlockState, message: String?) : super(createMessage(state, message)) {
        this.state = state
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull state: BlockState, message: String?, cause: Throwable?) : super(createMessage(state, message), cause) {
        this.state = state
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull state: BlockState, cause: Throwable?) : super(createMessage(state, null), cause) {
        this.state = state
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getState(): BlockState {
        return state
    }

    companion object {
        private const val serialVersionUID = 643372054081065905L
        private fun createMessage(@Nonnull state: BlockState, @Nullable message: String?): String {
            val sb = StringBuilder(500)
            sb.append("The block state ").append(state).append(" is invalid")
            if (message != null && !message.isEmpty()) {
                sb.append(": ").append(message)
            }
            try {
                val properties: String = state.getProperties().toString()
                sb.append('\n').append(properties)
            } catch (e: Throwable) {
                sb.append("\nProperty.toString() failed: ").append(e)
            }
            return sb.toString()
        }
    }
}