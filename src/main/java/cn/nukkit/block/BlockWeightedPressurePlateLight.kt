package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author CreeperFace
 */
class BlockWeightedPressurePlateLight @JvmOverloads constructor(meta: Int = 0) : BlockPressurePlateBase(meta) {
    @get:Override
    override val id: Int
        get() = LIGHT_WEIGHTED_PRESSURE_PLATE

    @get:Override
    override val name: String
        get() = "Weighted Pressure Plate (Light)"

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GOLD_BLOCK_COLOR

    @Override
    protected override fun computeRedstoneStrength(): Int {
        val count: Int = Math.min(this.level.getCollidingEntities(getCollisionBoundingBox()).length, maxWeight)
        return if (count > 0) {
            val f = Math.min(maxWeight, count) as Float / maxWeight.toFloat()
            NukkitMath.ceilFloat(f * 15.0f)
        } else {
            0
        }
    }

    val maxWeight: Int
        get() = 15

    init {
        this.onPitch = 0.90000004f
        this.offPitch = 0.75f
    }
}