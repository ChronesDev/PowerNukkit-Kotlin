package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockEntityTarget @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag) : BlockEntity(chunk, nbt) {
    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getLevelBlock().getId() === BlockID.TARGET

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var activePower: Int
        get() = NukkitMath.clamp(namedTag.getInt("activePower"), 0, 15)
        set(power) {
            namedTag.putInt("activePower", power)
        }
}