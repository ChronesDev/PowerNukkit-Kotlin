package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author good777LUCKY
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockStructure @Since("1.4.0.0-PN") @PowerNukkitOnly constructor(meta: Int) : BlockSolidMeta(meta) {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STRUCTURE_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var structureBlockType: StructureBlockType
        get() = getPropertyValue(STRUCTURE_BLOCK_TYPE)
        set(type) {
            setPropertyValue(STRUCTURE_BLOCK_TYPE, type)
        }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                // TODO: Add UI
            }
        }
        return true
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false
        }
        this.getLevel().setBlock(block, this, true)
        // TODO: Add Block Entity
        return true
    }

    @get:Override
    override val name: String
        get() = structureBlockType.getEnglishName()

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val resistance: Double
        get() = 18000000

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.LIGHT_GRAY_BLOCK_COLOR

    companion object {
        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val STRUCTURE_BLOCK_TYPE: BlockProperty<StructureBlockType> = ArrayBlockProperty(
                "structure_block_type", true, StructureBlockType::class.java
        )

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val PROPERTIES: BlockProperties = BlockProperties(STRUCTURE_BLOCK_TYPE)
    }
}