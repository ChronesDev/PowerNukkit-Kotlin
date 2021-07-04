package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockStructureVoid @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockSolid() {
    @get:Override
    override val id: Int
        get() = STRUCTURE_VOID

    @get:Override
    override val name: String
        get() = "Structure Void"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var type: StructureVoidType
        get() = getPropertyValue(STRUCTURE_VOID_TYPE)
        set(type) {
            setPropertyValue(STRUCTURE_VOID_TYPE, type)
        }

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return true
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
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
        get() = BlockColor.TRANSPARENT_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val STRUCTURE_VOID_TYPE: ArrayBlockProperty<StructureVoidType> = ArrayBlockProperty("structure_void_type", false, StructureVoidType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(STRUCTURE_VOID_TYPE)
    }
}