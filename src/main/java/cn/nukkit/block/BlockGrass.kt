package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockGrass @JvmOverloads constructor(meta: Int = 0) : BlockDirt(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val id: Int
        get() = GRASS

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 0.6

    @get:Override
    override val name: String
        get() = "Grass Block"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var dirtType: Optional<DirtType>
        get() = Optional.empty()
        set(dirtType) {
            if (dirtType != null) {
                throw InvalidBlockPropertyValueException(DIRT_TYPE, null, dirtType, "$name don't support DirtType")
            }
        }

    @Override
    override fun onActivate(@Nonnull item: Item): Boolean {
        return this.onActivate(item, null)
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (!this.up().canBeReplaced()) {
            return false
        }
        if (item.isFertilizer()) {
            if (player != null && player.gamemode and 0x01 === 0) {
                item.count--
            }
            this.level.addParticle(BoneMealParticle(this))
            ObjectTallGrass.growGrass(this.getLevel(), this, NukkitRandom())
            return true
        } else if (item.isHoe()) {
            item.useOn(this)
            this.getLevel().setBlock(this, Block.get(BlockID.FARMLAND))
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        } else if (item.isShovel()) {
            item.useOn(this)
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH))
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        }
        return false
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed grass spread and decay logic to match vanilla behaviour")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            // Grass dies and changes to dirt after a random time (when a random tick lands on the block) 
            // if directly covered by any opaque block.
            // Transparent blocks can kill grass in a similar manner, 
            // but only if they cause the light level above the grass block to be four or below (like water does), 
            // and the surrounding area is not otherwise sufficiently lit up.
            if (up().getLightFilter() > 1) {
                val ev = BlockFadeEvent(this, Block.get(BlockID.DIRT))
                Server.getInstance().getPluginManager().callEvent(ev)
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this, ev.getNewState())
                    return type
                }
            }

            // Grass can spread to nearby dirt blocks. 
            // Grass spreading without player intervention depends heavily on the time of day. 
            // For a dirt block to accept grass from a nearby grass block, the following requirements must be met:

            // The source block must have a light level of 9 or brighter directly above it.
            if (getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {

                // The dirt block receiving grass must be within a 3×5×3 range of the source block 
                // where the source block is in the center of the second topmost layer of that range.
                val random: ThreadLocalRandom = ThreadLocalRandom.current()
                val x: Int = random.nextInt(this.x as Int - 1, this.x as Int + 1 + 1)
                val y: Int = random.nextInt(this.y as Int - 3, this.y as Int + 1 + 1)
                val z: Int = random.nextInt(this.z as Int - 1, this.z as Int + 1 + 1)
                val block: Block = this.getLevel().getBlock(Vector3(x, y, z))
                if (block.getId() === Block.DIRT // It cannot spread to coarse dirt        
                        && block.getPropertyValue(DIRT_TYPE) === DirtType.NORMAL // The dirt block must have a light level of at least 4 above it.
                        && getLevel().getFullLight(block) >= 4 // Any block directly above the dirt block must not reduce light by 2 levels or more.
                        && block.up().getLightFilter() < 2) {
                    val ev = BlockSpreadEvent(block, this, Block.get(BlockID.GRASS))
                    Server.getInstance().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState())
                    }
                }
            }
            return type
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GRASS_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}