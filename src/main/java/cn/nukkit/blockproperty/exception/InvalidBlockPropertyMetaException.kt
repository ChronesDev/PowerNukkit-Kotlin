package cn.nukkit.blockproperty.exception

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
class InvalidBlockPropertyMetaException : InvalidBlockPropertyException {
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @Nonnull
    val currentMeta: Number

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @Nonnull
    val invalidMeta: Number

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(property: BlockProperty<*>, currentMeta: Number, invalidMeta: Number) : super(property, buildMessage(currentMeta, invalidMeta)) {
        this.currentMeta = currentMeta
        this.invalidMeta = invalidMeta
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(property: BlockProperty<*>, currentMeta: Number, invalidMeta: Number, message: String) : super(property, buildMessage(currentMeta, invalidMeta) + ". " + message) {
        this.currentMeta = currentMeta
        this.invalidMeta = invalidMeta
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(property: BlockProperty<*>, currentMeta: Number, invalidMeta: Number, message: String, cause: Throwable?) : super(property, buildMessage(currentMeta, invalidMeta) + ". " + message, cause) {
        this.currentMeta = currentMeta
        this.invalidMeta = invalidMeta
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(property: BlockProperty<*>, currentMeta: Number, invalidMeta: Number, cause: Throwable?) : super(property, buildMessage(currentMeta, invalidMeta), cause) {
        this.currentMeta = currentMeta
        this.invalidMeta = invalidMeta
    }

    companion object {
        private const val serialVersionUID = -8493494844859767053L
        private fun buildMessage(currentValue: Object, invalidValue: Object): String {
            return "Current Meta: $currentValue, Invalid Meta: $invalidValue"
        }
    }
}