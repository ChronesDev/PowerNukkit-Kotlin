package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockCoralFan @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta), Faceable {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL_FAN

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Tube Coral Fan",
                    "Brain Coral Fan",
                    "Bubble Coral Fan",
                    "Fire Coral Fan",
                    "Horn Coral Fan"
            )
            return names[type]
        }

    @get:Override
    override val color: BlockColor
        get() {
            val colors: Array<BlockColor> = arrayOf<BlockColor>(
                    BlockColor.BLUE_BLOCK_COLOR,
                    BlockColor.PINK_BLOCK_COLOR,
                    BlockColor.PURPLE_BLOCK_COLOR,
                    BlockColor.RED_BLOCK_COLOR,
                    BlockColor.YELLOW_BLOCK_COLOR
            )
            return colors[type]
        }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @get:PowerNukkitOnly
    val isDead: Boolean
        get() = false

    @get:PowerNukkitOnly
    val type: Int
        get() = getDamage() and 0x7

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex((getDamage() and 0x8 shr 3) + 1)

    @get:PowerNukkitOnly
    val rootsFace: BlockFace
        get() = BlockFace.DOWN

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val side: Block = getSide(rootsFace)
            if (!side.isSolid() || side.getId() === MAGMA || side.getId() === SOUL_SAND) {
                this.getLevel().useBreakOn(this)
            } else {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40))
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            val side: Block = getSide(rootsFace)
            if (side.getId() === ICE) {
                this.getLevel().useBreakOn(this)
                return type
            }
            if (!isDead && getLevelBlockAtLayer(1) !is BlockWater && getLevelBlockAtLayer(1) !is BlockIceFrosted) {
                val event = BlockFadeEvent(this, BlockCoralFanDead(getDamage()))
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true)
                }
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getDamage() and 0x8 === 0) {
                setDamage(getDamage() or 0x8)
            } else {
                setDamage(getDamage() xor 0x8)
            }
            this.getLevel().setBlock(this, this, true, true)
            return type
        }
        return 0
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player): Boolean {
        if (face === BlockFace.DOWN) {
            return false
        }
        val layer1: Block = block.getLevelBlockAtLayer(1)
        val hasWater = layer1 is BlockWater
        if (layer1.getId() !== Block.AIR && (!hasWater || layer1.getDamage() !== 0 && layer1.getDamage() !== 8)) {
            return false
        }
        if (hasWater && layer1.getDamage() === 8) {
            this.getLevel().setBlock(this, 1, BlockWater(), true, false)
        }
        if (!target.isSolid() || target.getId() === MAGMA || target.getId() === SOUL_SAND) {
            return false
        }
        if (face === BlockFace.UP) {
            var rotation: Double = player.yaw % 360
            if (rotation < 0) {
                rotation += 360.0
            }
            val axisBit = if (rotation >= 0 && rotation < 12 || 342 <= rotation && rotation < 360) 0x0 else 0x8
            setDamage(getDamage() and 0x7 or axisBit)
            this.getLevel().setBlock(this, 0, if (hasWater) BlockCoralFan(getDamage()) else BlockCoralFanDead(getDamage()), true, true)
        } else {
            val type = type
            val typeBit = type % 2
            val deadBit = if (isDead) 0x1 else 0
            val faceBit: Int
            faceBit = when (face) {
                WEST -> 0
                EAST -> 1
                NORTH -> 2
                SOUTH -> 3
                else -> 3
            }
            val deadData = faceBit shl 2 or (deadBit shl 1) or typeBit
            val deadBlockId: Int
            when (type) {
                BlockCoral.TYPE_TUBE, BlockCoral.TYPE_BRAIN -> deadBlockId = CORAL_FAN_HANG
                BlockCoral.TYPE_BUBBLE, BlockCoral.TYPE_FIRE -> deadBlockId = CORAL_FAN_HANG2
                BlockCoral.TYPE_HORN -> deadBlockId = CORAL_FAN_HANG3
                else -> deadBlockId = CORAL_FAN_HANG
            }
            this.getLevel().setBlock(this, 0, Block.get(deadBlockId, deadData), true, true)
        }
        return true
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun toItem(): Item {
        return Item.get(getItemId(), getDamage() xor 0x8)
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
        val FAN_DIRECTION: ArrayBlockProperty<BlockFace.Axis> = ArrayBlockProperty("coral_fan_direction", false, arrayOf<BlockFace.Axis>(BlockFace.Axis.X, BlockFace.Axis.Z)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(COLOR, FAN_DIRECTION)
    }
}