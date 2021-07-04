package cn.nukkit.block

import cn.nukkit.Player

class BlockChorusFlower : BlockTransparent() {
    @get:Override
    override val id: Int
        get() = CHORUS_FLOWER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Chorus Flower"

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val resistance: Double
        get() = 0.4

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_NONE

    // Chorus flowers must be above end stone or chorus plant, or be above air and horizontally adjacent to exactly one chorus plant.
    // If these conditions are not met, the block breaks without dropping anything.
    private val isPositionValid: Boolean
        private get() {
            // Chorus flowers must be above end stone or chorus plant, or be above air and horizontally adjacent to exactly one chorus plant.
            // If these conditions are not met, the block breaks without dropping anything.
            val down: Block = down()
            if (down.getId() === CHORUS_PLANT || down.getId() === END_STONE) {
                return true
            }
            if (down.getId() !== AIR) {
                return false
            }
            var foundPlant = false
            for (face in BlockFace.Plane.HORIZONTAL) {
                val side: Block = getSide(face)
                if (side.getId() === CHORUS_PLANT) {
                    if (foundPlant) {
                        return false
                    }
                    foundPlant = true
                }
            }
            return foundPlant
        }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isPositionValid) {
                level.scheduleUpdate(this, 1)
                return type
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            val players: Map<Integer, Player> = level.getChunkPlayers(x as Int shr 4, z as Int shr 4)
            level.addParticle(DestroyBlockParticle(this, this), players.values())
            level.setBlock(this, Block.get(AIR))
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
        return arrayOf<Item>(this.toItem())
    }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val AGE: IntBlockProperty = IntBlockProperty("age", false, 5)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(AGE)
    }
}