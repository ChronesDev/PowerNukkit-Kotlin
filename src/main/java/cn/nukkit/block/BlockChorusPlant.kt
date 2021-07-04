package cn.nukkit.block

import cn.nukkit.Player

class BlockChorusPlant : BlockTransparent() {
    @get:Override
    override val id: Int
        get() = CHORUS_PLANT

    @get:Override
    override val name: String
        get() = "Chorus Plant"

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val resistance: Double
        get() = 0.4

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_NONE

    // (a chorus plant with at least one other chorus plant horizontally adjacent) breaks unless (at least one of the vertically adjacent blocks is air)
    // (a chorus plant) breaks unless (the block below is (chorus plant or end stone)) or (any horizontally adjacent block is a (chorus plant above (chorus plant or end stone_))
    private val isPositionValid: Boolean
        private get() {
            // (a chorus plant with at least one other chorus plant horizontally adjacent) breaks unless (at least one of the vertically adjacent blocks is air)
            // (a chorus plant) breaks unless (the block below is (chorus plant or end stone)) or (any horizontally adjacent block is a (chorus plant above (chorus plant or end stone_))
            var horizontal = false
            var horizontalSupported = false
            val down: Block = down()
            for (face in BlockFace.Plane.HORIZONTAL) {
                val side: Block = getSide(face)
                if (side.getId() === CHORUS_PLANT) {
                    if (!horizontal) {
                        if (up().getId() !== AIR && down.getId() !== AIR) {
                            return false
                        }
                        horizontal = true
                    }
                    val sideSupport: Block = side.down()
                    if (sideSupport.getId() === CHORUS_PLANT || sideSupport.getId() === END_STONE) {
                        horizontalSupported = true
                    }
                }
            }
            return if (horizontal && horizontalSupported) {
                true
            } else down.getId() === CHORUS_PLANT || down.getId() === END_STONE
        }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid) {
                level.scheduleUpdate(this, 1)
                return type
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            level.useBreakOn(this, null, null, true)
            return type
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return if (!isPositionValid) {
            false
        } else super.place(item, block, target, face, fx, fy, fz, player)
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return if (ThreadLocalRandom.current().nextBoolean()) arrayOf<Item>(Item.get(ItemID.CHORUS_FRUIT, 0, 1)) else Item.EMPTY_ARRAY
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.PURPLE_BLOCK_COLOR
}