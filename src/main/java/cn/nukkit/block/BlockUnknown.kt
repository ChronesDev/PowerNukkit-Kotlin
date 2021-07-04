package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockUnknown @JvmOverloads constructor(@get:Override override val id: Int, meta: Integer? = 0) : BlockMeta(0) {

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Unknown"

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val UNKNOWN: UnsignedIntBlockProperty = UnsignedIntBlockProperty("unknown", true, -0x1)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(UNKNOWN)
    }

    init {
        if (meta != null && meta != 0) {
            getMutableState().setDataStorageFromInt(meta, true)
        }
    }
}