package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
class BlockSoulSand : BlockSolid() {
    @get:Override
    override val name: String
        get() = "Soul Sand"

    @get:Override
    override val id: Int
        get() = SOUL_SAND

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
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed MaxY BB, soul sand is a normal full cube in Bedrock Edition")
    override val maxY: Double
        get() = this.y + 1

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val isSoulSpeedCompatible: Boolean
        get() = true

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.motionX *= 0.4
        entity.motionZ *= 0.4
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val up: Block = up()
            if (up is BlockWater && (up.getDamage() === 0 || up.getDamage() === 8)) {
                val event = BlockFormEvent(up, BlockBubbleColumn(0))
                if (!event.isCancelled()) {
                    if (event.getNewState().getWaterloggingLevel() > 0) {
                        this.getLevel().setBlock(up, 1, BlockWater(), true, false)
                    }
                    this.getLevel().setBlock(up, 0, event.getNewState(), true, true)
                }
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.BROWN_BLOCK_COLOR
}