package cn.nukkit.blockproperty.exception

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
class InvalidBlockPropertyException : IllegalArgumentException {
    private val property: BlockProperty<*>

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>) : super(buildMessage(property)) {
        this.property = property
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, message: String) : super(buildMessage(property) + ". " + message) {
        this.property = property
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, message: String, cause: Throwable?) : super(buildMessage(property) + ". " + message, cause) {
        this.property = property
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, cause: Throwable?) : super(buildMessage(property), cause) {
        this.property = property
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getProperty(): BlockProperty<*> {
        return property
    }

    companion object {
        private const val serialVersionUID = -6934630506175381230L
        private fun buildMessage(@Nonnull property: BlockProperty<*>): String {
            return "Property: " + property.getName()
        }
    }
}