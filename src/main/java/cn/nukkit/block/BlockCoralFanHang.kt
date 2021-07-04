package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockCoralFanHang @PowerNukkitOnly constructor(meta: Int) : BlockCoralFan(meta), Faceable {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = CORAL_FAN_HANG

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() {
            var name: String = super.getName()
            name = name.substring(0, name.length() - 4)
            return if (isDead) {
                "Dead $name Wall Fan"
            } else {
                "$name Wall Fan"
            }
        }

    @get:Override
    override val isDead: Boolean
        get() = getDamage() and 2 === 2

    @Override
    override fun onUpdate(type: Int): Int {
        return if (type == Level.BLOCK_UPDATE_RANDOM) {
            type
        } else {
            super.onUpdate(type)
        }
    }

    @get:Override
    override val type: Int
        get() = if (getDamage() and 1 === 0) {
            BlockCoral.TYPE_TUBE
        } else {
            BlockCoral.TYPE_BRAIN
        }

    @get:Override
    override val blockFace: BlockFace
        get() {
            val face: Int = getDamage() shr 2 and 0x3
            return when (face) {
                0 -> BlockFace.WEST
                1 -> BlockFace.EAST
                2 -> BlockFace.NORTH
                3 -> BlockFace.SOUTH
                else -> BlockFace.SOUTH
            }
        }

    @get:Override
    override val rootsFace: BlockFace
        get() = blockFace.getOpposite()

    @Override
    override fun toItem(): Item {
        return ItemBlock(if (isDead) BlockCoralFanDead() else BlockCoralFan(), type)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HANG1_TYPE: ArrayBlockProperty<CoralType> = ArrayBlockProperty("coral_hang_type_bit", true, arrayOf<CoralType>(CoralType.BLUE, CoralType.PINK)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HANG_DIRECTION: ArrayBlockProperty<BlockFace> = ArrayBlockProperty("coral_direction", false, arrayOf<BlockFace>(BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(HANG1_TYPE, PERMANENTLY_DEAD, HANG_DIRECTION)
    }
}