package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 2015/12/26
 */
class BlockNetherrack : BlockSolid() {
    @get:Override
    override val id: Int
        get() = NETHERRACK

    @get:Override
    override val resistance: Double
        get() = 2

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = "Netherrack"

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        if (item.isNull() || !item.isFertilizer() || up().getId() !== AIR) {
            return false
        }
        val options: IntList = IntArrayList(2)
        for (face in BlockFace.Plane.HORIZONTAL) {
            val id: Int = getSide(face).getId()
            if ((id == CRIMSON_NYLIUM || id == WARPED_NYLIUM) && !options.contains(id)) {
                options.add(id)
            }
        }
        val nylium: Int
        val size: Int = options.size()
        nylium = if (size == 0) {
            return false
        } else if (size == 1) {
            options.getInt(0)
        } else {
            options.getInt(ThreadLocalRandom.current().nextInt(size))
        }
        if (level.setBlock(this, Block.get(nylium), true)) {
            if (player == null || !player.isCreative()) {
                item.count--
            }
            return true
        }
        return false
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }
}