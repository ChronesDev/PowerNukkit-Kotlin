package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class OptionalBoolean(@field:Nullable @param:Nullable private val value: Boolean) {
    TRUE(Boolean.TRUE), FALSE(Boolean.FALSE), EMPTY(null);

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val asBoolean: Boolean
        get() {
            if (value == null) {
                throw NoSuchElementException("No value present")
            }
            return value
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isPresent: Boolean
        get() = value != null

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun ifPresent(consumer: BooleanConsumer) {
        if (value != null) {
            consumer.accept(value)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun orElse(other: Boolean): Boolean {
        return value ?: other
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun orElseGet(other: BooleanSupplier): Boolean {
        return value ?: other.getAsBoolean()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(X::class)
    fun <X : Throwable?> orElseThrow(exceptionSupplier: Supplier<X>): Boolean {
        return value ?: throw exceptionSupplier.get()
    }

    @Override
    override fun toString(): String {
        return if (value == null) "OptionalBoolean.empty" else if (value) "OptionalBoolean[true]" else "OptionalBoolean[false]"
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun of(value: Boolean?): OptionalBoolean {
            return of(Objects.requireNonNull(value).booleanValue())
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun of(value: Boolean): OptionalBoolean {
            return if (value) TRUE else FALSE
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun ofNullable(value: Boolean?): OptionalBoolean {
            return if (value == null) EMPTY else of(value)
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun empty(): OptionalBoolean {
            return EMPTY
        }
    }
}