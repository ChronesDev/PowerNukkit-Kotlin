package cn.nukkit.blockproperty.exception

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.5.0.0-PN")
class BlockPropertyNotFoundException : NoSuchElementException {
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val propertyName: String

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    constructor(propertyName: String) : super("The property \"$propertyName\" was not found.") {
        this.propertyName = propertyName
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    constructor(propertyName: String, details: String) : super("$propertyName: $details") {
        this.propertyName = propertyName
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    constructor(propertyName: String, properties: BlockProperties) : super("The property \"" + propertyName + "\" was not found. Valid properties: " + properties.getNames()) {
        this.propertyName = propertyName
    }
}