package cn.nukkit.block

import cn.nukkit.entity.Entity

/**
 * @author Nukkit Project Team
 */
class BlockPressurePlateStone @JvmOverloads constructor(meta: Int = 0) : BlockPressurePlateBase(meta) {
    @get:Override
    override val name: String
        get() = "Stone Pressure Plate"

    @get:Override
    override val id: Int
        get() = STONE_PRESSURE_PLATE

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.STONE_BLOCK_COLOR

    @Override
    protected override fun computeRedstoneStrength(): Int {
        val bb: AxisAlignedBB = getCollisionBoundingBox()
        for (entity in this.level.getCollidingEntities(bb)) {
            if (entity is EntityLiving && entity.doesTriggerPressurePlate()) {
                return 15
            }
        }
        return 0
    }

    init {
        this.onPitch = 0.6f
        this.offPitch = 0.5f
    }
}