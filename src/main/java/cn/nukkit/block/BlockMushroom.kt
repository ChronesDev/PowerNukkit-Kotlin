package cn.nukkit.block

import cn.nukkit.Player

abstract class BlockMushroom @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canStay()) {
                getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (canStay()) {
            getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isFertilizer()) {
            if (player != null && player.gamemode and 0x01 === 0) {
                item.count--
            }
            if (ThreadLocalRandom.current().nextFloat() < 0.4) {
                grow()
            }
            this.level.addParticle(BoneMealParticle(this))
            return true
        }
        return false
    }

    fun grow(): Boolean {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, false)
        val generator = BigMushroom(type)
        val chunkManager = ListChunkManager(this.level)
        return if (generator.generate(chunkManager, NukkitRandom(), this)) {
            val ev = StructureGrowEvent(this, chunkManager.getBlocks())
            this.level.getServer().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            }
            for (block in ev.getBlockList()) {
                this.level.setBlockAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getId(), block.getDamage())
            }
            true
        } else {
            this.level.setBlock(this, this, true, false)
            false
        }
    }

    fun canStay(): Boolean {
        val block: Block = this.down()
        return block.getId() === MYCELIUM || block.getId() === PODZOL || block is BlockNylium || !block.isTransparent() && this.level.getFullLight(this) < 13
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN
    protected abstract val type: Int
}