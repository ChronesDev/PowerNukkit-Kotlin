package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class StoneSlab2Type(color: BlockColor) {
    RED_SANDSTONE(BlockColor.ORANGE_BLOCK_COLOR), PURPUR(BlockColor.MAGENTA_BLOCK_COLOR), PRISMARINE_ROUGH(BlockColor.CYAN_BLOCK_COLOR), PRISMARINE_DARK(BlockColor.DIAMOND_BLOCK_COLOR), PRISMARINE_BRICK(BlockColor.DIAMOND_BLOCK_COLOR), MOSSY_COBBLESTONE(BlockColor.STONE_BLOCK_COLOR), SMOOTH_SANDSTONE(BlockColor.SAND_BLOCK_COLOR), RED_NETHER_BRICK(BlockColor.NETHERRACK_BLOCK_COLOR);

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
        val PROPERTY: ArrayBlockProperty<StoneSlab2Type> = ArrayBlockProperty("stone_slab_type_2", true, values())
    }

    init {
        this.color = color
        englishName = Arrays.stream(name().split("_")).map { name -> name.substring(0, 1) + name.substring(1).toLowerCase() }.collect(Collectors.joining(" "))
    }
}