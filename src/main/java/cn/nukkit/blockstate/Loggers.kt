package cn.nukkit.blockstate

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly("Only for internal use")
@Since("1.4.0.0-PN")
internal class Loggers private constructor() {
    companion object {
        val logIBlockState: Logger = LogManager.getLogger(IBlockState::class.java)
        val logIMutableBlockState: Logger = LogManager.getLogger(IMutableBlockState::class.java)
    }

    init {
        throw UnsupportedOperationException()
    }
}