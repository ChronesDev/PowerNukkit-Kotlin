package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 * @apiNote Implements BlockConnectable only on PowerNukkit
 */
@PowerNukkitDifference(info = "Extends BlockWallBase and implements BlockConnectable only on PowerNukkit", since = "1.4.0.0-PN")
class BlockWall @JvmOverloads constructor(meta: Int = 0) : BlockWallBase(meta) {
    @get:Override
    override val id: Int
        get() = STONE_WALL

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:PowerNukkitDifference(since = "1.3.0.0-PN", info = "Return the actual material color instead of transparent")
    override val color: BlockColor
        get() = wallType!!.color

    @get:Since("1.3.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    var wallType: WallType?
        get() = getPropertyValue(WALL_BLOCK_TYPE)
        set(type) {
            setPropertyValue(WALL_BLOCK_TYPE, type)
        }

    @get:Override
    override val name: String
        get() = wallType!!.typeName

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    enum class WallConnectionType {
        NONE, SHORT, TALL
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    enum class WallType @JvmOverloads constructor(color: BlockColor = STONE_BLOCK_COLOR) {
        COBBLESTONE, MOSSY_COBBLESTONE, GRANITE(DIRT_BLOCK_COLOR), DIORITE(QUARTZ_BLOCK_COLOR), ANDESITE, SANDSTONE(SAND_BLOCK_COLOR), BRICK(RED_BLOCK_COLOR), STONE_BRICK, MOSSY_STONE_BRICK, END_BRICK(SAND_BLOCK_COLOR), NETHER_BRICK(NETHERRACK_BLOCK_COLOR), PRISMARINE(CYAN_BLOCK_COLOR), RED_SANDSTONE(ORANGE_BLOCK_COLOR), RED_NETHER_BRICK(NETHERRACK_BLOCK_COLOR);

        val color: BlockColor

        @get:Since("1.4.0.0-PN")
        val typeName: String
        @Since("1.3.0.0-PN")
        fun getColor(): BlockColor {
            return color
        }

        init {
            this.color = color
            val name: String = Arrays.stream(name().split("_"))
                    .map { part -> part.substring(0, 1) + part.substring(1).toLowerCase() }
                    .collect(Collectors.joining(" "))

            // Concatenation separated to workaround https://bugs.openjdk.java.net/browse/JDK-8077605
            // https://www.reddit.com/r/learnprogramming/comments/32bfle/can_you_explain_this_strange_java8_error/
            typeName = "$name Wall"
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val WALL_BLOCK_TYPE: BlockProperty<WallType> = ArrayBlockProperty("wall_block_type", true, WallType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                WALL_BLOCK_TYPE,
                WALL_CONNECTION_TYPE_SOUTH,
                WALL_CONNECTION_TYPE_WEST,
                WALL_CONNECTION_TYPE_NORTH,
                WALL_CONNECTION_TYPE_EAST,
                WALL_POST_BIT
        )

        @Deprecated
        @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.COBBLESTONE", since = "1.3.0.0-PN")
        val NONE_MOSSY_WALL = 0

        @Deprecated
        @DeprecationDetails(reason = "No longer matches the meta directly", replaceWith = "WallType.MOSSY_COBBLESTONE", since = "1.3.0.0-PN")
        val MOSSY_WALL = 1
    }
}