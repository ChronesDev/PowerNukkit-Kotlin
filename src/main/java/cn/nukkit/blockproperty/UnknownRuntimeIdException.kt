package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class UnknownRuntimeIdException : IllegalStateException {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(s: String?) : super(s) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(message: String?, cause: Throwable?) : super(message, cause) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(cause: Throwable?) : super(cause) {
    }
}