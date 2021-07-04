package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class StoneSlab4Type(color: BlockColor) {
    MOSSY_STONE_BRICK(BlockColor.STONE_BLOCK_COLOR), SMOOTH_QUARTZ(BlockColor.QUARTZ_BLOCK_COLOR), STONE(BlockColor.STONE_BLOCK_COLOR), CUT_SANDSTONE(BlockColor.SAND_BLOCK_COLOR), CUT_RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR);

    private val color: BlockColor

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val englishName: String
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getColor(): BlockColor {
        return color
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTY: ArrayBlockProperty<StoneSlab4Type> = ArrayBlockProperty("stone_slab_type_4", true, values())
    }

    init {
        this.color = color
        englishName = Arrays.stream(name().split("_")).map { name -> name.substring(0, 1) + name.substring(1).toLowerCase() }.collect(Collectors.joining(" "))
    }
}