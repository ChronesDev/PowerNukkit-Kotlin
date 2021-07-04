package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
class BlockSand : BlockFallableMeta {
    constructor() {
        // Does nothing
    }

    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = SAND

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHOVEL

    @get:Override
    override val name: String
        get() = if (this.getDamage() === 0x01) {
            "Red Sand"
        } else "Sand"

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SAND_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val SAND_TYPE: ArrayBlockProperty<SandType> = ArrayBlockProperty("sand_type", true, SandType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SAND_TYPE)
        const val DEFAULT = 0
        const val RED = 1
    }
}