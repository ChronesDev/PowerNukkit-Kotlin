package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class LevelException : ServerException {
    constructor(message: String?) : super(message) {}

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(message: String?, cause: Throwable?) : super(message, cause) {
    }
}