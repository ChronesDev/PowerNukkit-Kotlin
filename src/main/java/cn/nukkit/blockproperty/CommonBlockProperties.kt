package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class CommonBlockProperties private constructor() {
    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_PROPERTIES: BlockProperties = BlockProperties()

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val OPEN: BooleanBlockProperty = BooleanBlockProperty("open_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val TOGGLE: BooleanBlockProperty = BooleanBlockProperty("toggle_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val REDSTONE_SIGNAL: IntBlockProperty = IntBlockProperty("redstone_signal", false, 15)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PERMANENTLY_DEAD: BooleanBlockProperty = BooleanBlockProperty("dead_bit", true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val REDSTONE_SIGNAL_BLOCK_PROPERTY: BlockProperties = BlockProperties(REDSTONE_SIGNAL)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val UPPER_BLOCK: BooleanBlockProperty = BooleanBlockProperty("upper_block_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val FACING_DIRECTION: BlockProperty<BlockFace> = ArrayBlockProperty("facing_direction", false, arrayOf<BlockFace>( // Index based
                BlockFace.DOWN, BlockFace.UP,
                BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.WEST, BlockFace.EAST)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val CHISEL_TYPE: ArrayBlockProperty<ChiselType> = ArrayBlockProperty("chisel_type", true, ChiselType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val AGE_15: IntBlockProperty = IntBlockProperty("age", false, 15)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val FACING_DIRECTION_BLOCK_PROPERTIES: BlockProperties = BlockProperties(FACING_DIRECTION)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DIRECTION: BlockProperty<BlockFace> = ArrayBlockProperty("direction", false, arrayOf<BlockFace>( // Horizontal-index based
                BlockFace.SOUTH, BlockFace.WEST,
                BlockFace.NORTH, BlockFace.EAST)).ordinal(true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PILLAR_AXIS: BlockProperty<BlockFace.Axis> = ArrayBlockProperty("pillar_axis", false, arrayOf<BlockFace.Axis>(
                BlockFace.Axis.Y, BlockFace.Axis.X, BlockFace.Axis.Z
        ))

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DEPRECATED: IntBlockProperty = IntBlockProperty("deprecated", false, 3)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val COLOR: BlockProperty<DyeColor> = ArrayBlockProperty("color", true, arrayOf<DyeColor>(
                DyeColor.WHITE, DyeColor.ORANGE, DyeColor.MAGENTA, DyeColor.LIGHT_BLUE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.PINK,
                DyeColor.GRAY, DyeColor.LIGHT_GRAY, DyeColor.CYAN, DyeColor.PURPLE, DyeColor.BLUE, DyeColor.BROWN,
                DyeColor.GREEN, DyeColor.RED, DyeColor.BLACK
        ), 4, "color", false, arrayOf(
                "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue",
                "brown", "green", "red", "black"
        ))

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val COLOR_BLOCK_PROPERTIES: BlockProperties = BlockProperties(COLOR)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val POWERED: BooleanBlockProperty = BooleanBlockProperty("powered_bit", false)
    }

    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }
}