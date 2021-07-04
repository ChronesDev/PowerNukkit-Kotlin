package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/17
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockNoteblock : BlockSolid(), RedstoneComponent, BlockEntityHolder<BlockEntityMusic?> {
    @get:Override
    override val name: String
        get() = "Note Block"

    @get:Override
    override val id: Int
        get() = NOTEBLOCK

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityMusic?>
        get() = BlockEntityMusic::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.MUSIC

    @get:Override
    override val hardness: Double
        get() = 0.8

    @get:Override
    override val resistance: Double
        get() = 4.0

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onTouch(@Nullable player: Player?, action: Action): Int {
        if (action === Action.LEFT_CLICK_BLOCK && (player == null || !player.isCreative() && !player.isSpectator())) {
            emitSound()
        }
        return super.onTouch(player, action)
    }

    val strength: Int
        get() {
            val blockEntity: BlockEntityMusic = this.getBlockEntity()
            return if (blockEntity != null) blockEntity.getPitch() else 0
        }

    fun increaseStrength() {
        getOrCreateBlockEntity().changePitch()
    }

    val instrument: Instrument
        get() = when (this.down().getId()) {
            GOLD_BLOCK -> Instrument.GLOCKENSPIEL
            CLAY_BLOCK, HONEYCOMB_BLOCK -> Instrument.FLUTE
            PACKED_ICE -> Instrument.CHIME
            WOOL -> Instrument.GUITAR
            BONE_BLOCK -> Instrument.XYLOPHONE
            IRON_BLOCK -> Instrument.VIBRAPHONE
            SOUL_SAND -> Instrument.COW_BELL
            PUMPKIN -> Instrument.DIDGERIDOO
            EMERALD_BLOCK -> Instrument.SQUARE_WAVE
            HAY_BALE -> Instrument.BANJO
            GLOWSTONE_BLOCK -> Instrument.ELECTRIC_PIANO
            LOG, LOG2, PLANKS, DOUBLE_WOODEN_SLAB, WOODEN_SLAB, WOOD_STAIRS, SPRUCE_WOOD_STAIRS, BIRCH_WOOD_STAIRS, JUNGLE_WOOD_STAIRS, ACACIA_WOOD_STAIRS, DARK_OAK_WOOD_STAIRS, CRIMSON_STAIRS, WARPED_STAIRS, FENCE, FENCE_GATE, FENCE_GATE_SPRUCE, FENCE_GATE_BIRCH, FENCE_GATE_JUNGLE, FENCE_GATE_DARK_OAK, FENCE_GATE_ACACIA, CRIMSON_FENCE_GATE, WARPED_FENCE_GATE, DOOR_BLOCK, SPRUCE_DOOR_BLOCK, BIRCH_DOOR_BLOCK, JUNGLE_DOOR_BLOCK, ACACIA_DOOR_BLOCK, DARK_OAK_DOOR_BLOCK, CRIMSON_DOOR_BLOCK, WARPED_DOOR_BLOCK, WOODEN_PRESSURE_PLATE, TRAPDOOR, SIGN_POST, WALL_SIGN, NOTEBLOCK, BOOKSHELF, CHEST, TRAPPED_CHEST, CRAFTING_TABLE, JUKEBOX, BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, DAYLIGHT_DETECTOR, DAYLIGHT_DETECTOR_INVERTED, STANDING_BANNER, WALL_BANNER -> Instrument.BASS
            SAND, GRAVEL, CONCRETE_POWDER -> Instrument.DRUM
            GLASS, GLASS_PANEL, STAINED_GLASS_PANE, STAINED_GLASS, BEACON, SEA_LANTERN -> Instrument.STICKS
            STONE, SANDSTONE, RED_SANDSTONE, COBBLESTONE, MOSSY_STONE, BRICKS, STONE_BRICKS, NETHER_BRICK_BLOCK, RED_NETHER_BRICK, QUARTZ_BLOCK, DOUBLE_SLAB, SLAB, DOUBLE_RED_SANDSTONE_SLAB, RED_SANDSTONE_SLAB, COBBLE_STAIRS, BRICK_STAIRS, STONE_BRICK_STAIRS, NETHER_BRICKS_STAIRS, SANDSTONE_STAIRS, QUARTZ_STAIRS, RED_SANDSTONE_STAIRS, PURPUR_STAIRS, COBBLE_WALL, NETHER_BRICK_FENCE, BEDROCK, GOLD_ORE, IRON_ORE, COAL_ORE, LAPIS_ORE, DIAMOND_ORE, REDSTONE_ORE, GLOWING_REDSTONE_ORE, EMERALD_ORE, DROPPER, DISPENSER, FURNACE, BURNING_FURNACE, OBSIDIAN, GLOWING_OBSIDIAN, MONSTER_SPAWNER, STONE_PRESSURE_PLATE, NETHERRACK, QUARTZ_ORE, ENCHANTING_TABLE, END_PORTAL_FRAME, END_STONE, END_BRICKS, ENDER_CHEST, STAINED_TERRACOTTA, TERRACOTTA, PRISMARINE, COAL_BLOCK, PURPUR_BLOCK, MAGMA, CONCRETE, STONECUTTER, OBSERVER, CRIMSON_NYLIUM, WARPED_NYLIUM -> Instrument.BASS_DRUM
            else -> Instrument.PIANO
        }

    fun emitSound() {
        if (this.up().getId() !== AIR) return
        val instrument = instrument
        this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_NOTE, instrument.ordinal() shl 8 or strength)
        val pk = BlockEventPacket()
        pk.x = this.getFloorX()
        pk.y = this.getFloorY()
        pk.z = this.getFloorZ()
        pk.case1 = instrument.ordinal()
        pk.case2 = strength
        this.getLevel().addChunkPacket(this.getFloorX() shr 4, this.getFloorZ() shr 4, pk)
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        increaseStrength()
        emitSound()
        return true
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
            // before the "real" BlockEntity is set. That means, if we'd use the other method here,
            // it would create two BlockEntities.
            val music: BlockEntityMusic = getBlockEntity() ?: return 0
            if (this.isGettingPower()) {
                if (!music.isPowered()) {
                    emitSound()
                }
                music.setPowered(true)
            } else {
                music.setPowered(false)
            }
        }
        return super.onUpdate(type)
    }

    enum class Instrument(sound: Sound) {
        PIANO(Sound.NOTE_HARP), BASS_DRUM(Sound.NOTE_BD), DRUM(Sound.NOTE_SNARE), STICKS(Sound.NOTE_HAT), BASS(Sound.NOTE_BASS), GLOCKENSPIEL(Sound.NOTE_BELL), FLUTE(Sound.NOTE_FLUTE), CHIME(Sound.NOTE_CHIME), GUITAR(Sound.NOTE_GUITAR), XYLOPHONE(Sound.NOTE_XYLOPHONE), VIBRAPHONE(Sound.NOTE_IRON_XYLOPHONE), COW_BELL(Sound.NOTE_COW_BELL), DIDGERIDOO(Sound.NOTE_DIDGERIDOO), SQUARE_WAVE(Sound.NOTE_BIT), BANJO(Sound.NOTE_BANJO), ELECTRIC_PIANO(Sound.NOTE_PLING);

        private val sound: Sound
        fun getSound(): Sound {
            return sound
        }

        init {
            this.sound = sound
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR
}