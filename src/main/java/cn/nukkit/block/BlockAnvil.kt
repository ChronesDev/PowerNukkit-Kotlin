package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable", since = "1.4.0.0-PN")
class BlockAnvil : BlockFallableMeta, Faceable {
    constructor() {
        // Does nothing
    }

    constructor(meta: Int) : super(meta) {}

    @get:Override
    override val id: Int
        get() = ANVIL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var anvilDamage: AnvilDamage
        get() = getPropertyValue(DAMAGE)
        set(anvilDamage) {
            setPropertyValue(DAMAGE, anvilDamage)
        }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val isTransparent: Boolean
        get() = true

    @get:Override
    override val hardness: Double
        get() = 5

    @get:Override
    override val resistance: Double
        get() = 6000

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val name: String
        get() = anvilDamage.getEnglishName()

    @PowerNukkitDifference(info = "Just like sand, it can now be placed anywhere and removed the sound for the player who placed, was duplicated", since = "1.3.0.0-PN")
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        blockFace = if (player != null) player.getDirection().rotateY() else BlockFace.SOUTH
        this.getLevel().setBlock(this, this, true)
        if (player == null) {
            this.getLevel().addSound(this, Sound.RANDOM_ANVIL_LAND, 1, 0.8f)
        } else {
            val players: Collection<Player> = getLevel().getChunkPlayers(getChunkX(), getChunkZ()).values()
            players.remove(player)
            if (!players.isEmpty()) {
                getLevel().addSound(this, Sound.RANDOM_ANVIL_LAND, 1, 0.8f, players)
            }
        }
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (player != null) {
            player.addWindow(AnvilInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID)
        }
        return true
    }

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the returned bounding box")
    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val face: BlockFace = blockFace.rotateY()
        val xOffset: Double = Math.abs(face.getXOffset()) * (2 / 16.0)
        val zOffset: Double = Math.abs(face.getZOffset()) * (2 / 16.0)
        return SimpleAxisAlignedBB(x + xOffset, y, z + zOffset, x + 1 - xOffset, y + 1, z + 1 - zOffset)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val DAMAGE: BlockProperty<AnvilDamage> = ArrayBlockProperty("damage", false, AnvilDamage::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                DIRECTION.exportingToItems(true), DAMAGE.exportingToItems(true)
        )
    }
}