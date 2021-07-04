package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 27. 11. 2016
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
abstract class BlockButton @UsedByReflection constructor(meta: Int) : BlockFlowable(meta), RedstoneComponent, Faceable {
    @UsedByReflection
    constructor() : this(0) {
    }

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @PowerNukkitDifference(info = "Allow to be placed on top of the walls", since = "1.3.0.0-PN")
    @PowerNukkitDifference(info = "Now, can be placed on solid blocks", since = "1.4.0.0-PN")
    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!BlockLever.isSupportValid(target, face)) {
            return false
        }
        blockFace = face
        this.level.setBlock(block, this, true, true)
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (isActivated) {
            return false
        }
        this.level.scheduleUpdate(this, 30)
        isActivated = true
        this.level.setBlock(this, this, true, false)
        this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_POWER_ON, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()))
        if (this.level.getServer().isRedstoneEnabled()) {
            this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 0, 15))
            updateAroundRedstone()
            RedstoneComponent.updateAroundRedstone(getSide(facing.getOpposite()), facing)
        }
        return true
    }

    @PowerNukkitDifference(info = "Now, can be placed on solid blocks", since = "1.4.0.0-PN")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val thisFace: BlockFace = facing
            val touchingFace: BlockFace = thisFace.getOpposite()
            val side: Block = this.getSide(touchingFace)
            if (!BlockLever.isSupportValid(side, thisFace)) {
                this.level.useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (isActivated) {
                isActivated = false
                this.level.setBlock(this, this, true, false)
                this.level.addLevelSoundEvent(this.add(0.5, 0.5, 0.5), LevelSoundEventPacket.SOUND_POWER_OFF, GlobalBlockPalette.getOrCreateRuntimeId(this.getId(), this.getDamage()))
                if (this.level.getServer().isRedstoneEnabled()) {
                    this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 15, 0))
                    updateAroundRedstone()
                    RedstoneComponent.updateAroundRedstone(getSide(facing.getOpposite()), facing)
                }
            }
            return Level.BLOCK_UPDATE_SCHEDULED
        }
        return 0
    }

    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isActivated: Boolean
        get() = getBooleanValue(BUTTON_PRESSED)
        set(activated) {
            setBooleanValue(BUTTON_PRESSED, activated)
        }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @Override
    override fun getWeakPower(side: BlockFace?): Int {
        return if (isActivated) 15 else 0
    }

    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (!isActivated) 0 else if (facing === side) 15 else 0
    }

    val facing: BlockFace
        get() = getPropertyValue(FACING_DIRECTION)

    @Override
    override fun onBreak(item: Item?): Boolean {
        if (isActivated) {
            this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 15, 0))
        }
        return super.onBreak(item)
    }

    @Override
    override fun toItem(): Item {
        return Item.get(this.getItemId())
    }

    @get:Override
    @get:PowerNukkitDifference(info = "Was returning the wrong face", since = "1.3.0.0-PN")
    @set:Override
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace
        get() = facing
        set(face) {
            setPropertyValue(FACING_DIRECTION, face)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        protected val BUTTON_PRESSED: BooleanBlockProperty = BooleanBlockProperty("button_pressed_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                FACING_DIRECTION,
                BUTTON_PRESSED
        )
    }
}