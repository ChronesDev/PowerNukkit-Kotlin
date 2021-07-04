package cn.nukkit.blockstate.exception

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class InvalidBlockStateDataTypeException : IllegalArgumentException {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull blockData: Number) : super("The block data " + blockData + " has an unsupported type " + blockData.getClass()) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull blockData: Number, @Nullable cause: Throwable?) : super("The block data " + blockData + " has an unsupported type " + blockData.getClass(), cause) {
    }

    companion object {
        private const val serialVersionUID = 6883758182474914542L
    }
}