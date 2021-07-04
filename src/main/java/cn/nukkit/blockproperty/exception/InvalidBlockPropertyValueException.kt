package cn.nukkit.blockproperty.exception

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
class InvalidBlockPropertyValueException : InvalidBlockPropertyException {
    @Nullable
    private val currentValue: Serializable

    @Nullable
    private val invalidValue: Serializable

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: Serializable, invalidValue: Serializable) : super(property, buildMessage(currentValue, invalidValue)) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: Serializable, invalidValue: Serializable, message: String) : super(property, buildMessage(currentValue, invalidValue) + ". " + message) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: Serializable, invalidValue: Serializable, message: String, cause: Throwable?) : super(property, buildMessage(currentValue, invalidValue) + ". " + message, cause) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: Serializable, invalidValue: Serializable, cause: Throwable?) : super(property, buildMessage(currentValue, invalidValue), cause) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getCurrentValue(): Serializable {
        return currentValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getInvalidValue(): Serializable {
        return invalidValue
    }

    companion object {
        private const val serialVersionUID = -1087431932428639175L
        private fun buildMessage(currentValue: Object, invalidValue: Object): String {
            return "Current Value: $currentValue, Invalid Value: $invalidValue"
        }
    }
}