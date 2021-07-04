package cn.nukkit.block

import cn.nukkit.entity.Entity

/**
 * @author Nukkit Project Team
 */
class BlockPressurePlateWood @JvmOverloads constructor(meta: Int = 0) : BlockPressurePlateBase(meta) {
    @get:Override
    override val name: String
        get() = "Oak Pressure Plate"

    @get:Override
    override val id: Int
        get() = WOODEN_PRESSURE_PLATE

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 0.5

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    protected override fun computeRedstoneStrength(): Int {
        val bb: AxisAlignedBB = getCollisionBoundingBox()
        for (entity in this.level.getCollidingEntities(bb)) {
            if (entity.doesTriggerPressurePlate()) {
                return 15
            }
        }
        return 0
    }

    init {
        this.onPitch = 0.8f
        this.offPitch = 0.7f
    }
}