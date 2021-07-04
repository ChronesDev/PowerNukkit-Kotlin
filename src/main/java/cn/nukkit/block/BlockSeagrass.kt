package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockSeagrass @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SEAGRASS

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Seagrass"

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = down()
        val layer1Block: Block = block.getLevelBlockAtLayer(1)
        var waterDamage: Int
        if (down.isSolid() && down.getId() !== MAGMA && down.getId() !== SOUL_SAND &&
                layer1Block is BlockWater && (block.getDamage().also { waterDamage = it } == 0 || waterDamage == 8)) {
            if (waterDamage == 8) {
                this.getLevel().setBlock(this, 1, BlockWater(), true, false)
            }
            this.getLevel().setBlock(this, 0, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val blockLayer1: Block = getLevelBlockAtLayer(1)
            var damage: Int
            if (blockLayer1 !is BlockIceFrosted
                    && (blockLayer1 !is BlockWater || blockLayer1.getDamage().also { damage = it } != 0 && damage != 8)) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
            val down: Block = down()
            damage = getDamage()
            if (damage == 0 || damage == 2) {
                if (!down.isSolid() || down.getId() === MAGMA || down.getId() === SOUL_SAND) {
                    this.getLevel().useBreakOn(this)
                    return Level.BLOCK_UPDATE_NORMAL
                }
                if (damage == 2) {
                    val up: Block = up()
                    if (up.getId() !== id || up.getDamage() !== 1) {
                        this.getLevel().useBreakOn(this)
                    }
                }
            } else if (down.getId() !== id || down.getDamage() !== 2) {
                this.getLevel().useBreakOn(this)
            }
            return Level.BLOCK_UPDATE_NORMAL
        }
        return 0
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (getDamage() === 0 && item.isFertilizer()) {
            val up: Block = this.up()
            var damage: Int
            if (up is BlockWater && (up.getDamage().also { damage = it } == 0 || damage == 8)) {
                if (player != null && player.gamemode and 0x01 === 0) {
                    item.count--
                }
                this.level.addParticle(BoneMealParticle(this))
                this.level.setBlock(this, BlockSeagrass(2), true, false)
                this.level.setBlock(up, 1, up, true, false)
                this.level.setBlock(up, 0, BlockSeagrass(1), true)
                return true
            }
        }
        return false
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isShears()) {
            arrayOf<Item>(toItem())
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WATER_BLOCK_COLOR

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SHEARS

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockSeagrass(), 0)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val SEA_GRASS_TYPE: ArrayBlockProperty<SeaGrassType> = ArrayBlockProperty("sea_grass_type", false, SeaGrassType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SEA_GRASS_TYPE)
    }
}