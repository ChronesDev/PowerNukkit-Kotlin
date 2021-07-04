package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockCoral @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isDead: Boolean
        get() = getDamage() and 0x8 === 0x8
        set(dead) {
            if (dead) {
                setDamage(getDamage() or 0x8)
            } else {
                setDamage(getDamage() xor 0x8)
            }
        }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val down: Block = down()
            if (!down.isSolid()) {
                this.getLevel().useBreakOn(this)
            } else if (!isDead) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40))
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isDead && getLevelBlockAtLayer(1) !is BlockWater && getLevelBlockAtLayer(1) !is BlockIceFrosted) {
                val event = BlockFadeEvent(this, BlockCoral(getDamage() or 0x8))
                if (!event.isCancelled()) {
                    isDead = true
                    this.getLevel().setBlock(this, event.getNewState(), true, true)
                }
            }
            return type
        }
        return 0
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = down()
        val layer1: Block = block.getLevelBlockAtLayer(1)
        val hasWater = layer1 is BlockWater
        var waterDamage: Int
        if (layer1.getId() !== Block.AIR && (!hasWater || layer1.getDamage().also { waterDamage = it } != 0 && waterDamage != 8)) {
            return false
        }
        if (hasWater && layer1.getDamage() === 8) {
            this.getLevel().setBlock(this, 1, BlockWater(), true, false)
        }
        if (down.isSolid()) {
            this.getLevel().setBlock(this, 0, this, true, true)
            return true
        }
        return false
    }

    // Invalid
    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Tube Coral",
                    "Brain Coral",
                    "Bubble Coral",
                    "Fire Coral",
                    "Horn Coral",  // Invalid
                    "Tube Coral",
                    "Tube Coral",
                    "Tube Coral"
            )
            val name = names[getDamage() and 0x7]
            return if (isDead) {
                "Dead $name"
            } else {
                name
            }
        }

    // Invalid
    @get:Override
    override val color: BlockColor
        get() {
            if (isDead) {
                return BlockColor.GRAY_BLOCK_COLOR
            }
            val colors: Array<BlockColor> = arrayOf<BlockColor>(
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.PINK_BLOCK_COLOR,
                    BlockColor.PURPLE_BLOCK_COLOR,
                    BlockColor.RED_BLOCK_COLOR,
                    BlockColor.YELLOW_BLOCK_COLOR,  // Invalid
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.BLUE_BLOCK_COLOR
            )
            return colors[getDamage() and 0x7]
        }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            super.getDrops(item)
        } else {
            Item.EMPTY_ARRAY
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val COLOR: ArrayBlockProperty<CoralType> = ArrayBlockProperty("coral_color", true, CoralType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(COLOR, PERMANENTLY_DEAD)

        @PowerNukkitOnly
        val TYPE_TUBE = 0

        @PowerNukkitOnly
        val TYPE_BRAIN = 1

        @PowerNukkitOnly
        val TYPE_BUBBLE = 2

        @PowerNukkitOnly
        val TYPE_FIRE = 3

        @PowerNukkitOnly
        val TYPE_HORN = 4
    }
}