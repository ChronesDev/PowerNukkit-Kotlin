package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class StoneSlab1Type {
    SMOOTH_STONE("Smooth Stone"), SANDSTONE(BlockColor.SAND_BLOCK_COLOR), WOOD(BlockColor.WOOD_BLOCK_COLOR), COBBLESTONE, BRICK, STONE_BRICK("Stone Brick"), QUARTZ(BlockColor.QUARTZ_BLOCK_COLOR), NETHER_BRICK(BlockColor.NETHERRACK_BLOCK_COLOR, "Nether Brick");

    private val color: BlockColor

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String

    constructor() : this(BlockColor.STONE_BLOCK_COLOR) {}
    constructor(name: String) {
        color = BlockColor.STONE_BLOCK_COLOR
        englishName = name
    }

    constructor(color: BlockColor) {
        this.color = color
        englishName = name().substring(0, 1) + name().substring(1).toLowerCase()
    }

    constructor(color: BlockColor, name: String) {
        this.color = color
        englishName = name
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getColor(): BlockColor {
        return color
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTY: ArrayBlockProperty<StoneSlab1Type> = ArrayBlockProperty("stone_slab_type", true, values())
    }
}