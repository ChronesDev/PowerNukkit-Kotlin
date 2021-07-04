package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author CreeperFace
 */
class BlockEntityJukebox(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    private var recordItem: Item? = null
    @Override
    protected override fun initBlockEntity() {
        if (namedTag.contains("RecordItem")) {
            recordItem = NBTIO.getItemHelper(namedTag.getCompound("RecordItem"))
        } else {
            recordItem = Item.get(0)
        }
        super.initBlockEntity()
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.getLevel().getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) === Block.JUKEBOX

    fun setRecordItem(recordItem: Item?) {
        Objects.requireNonNull(recordItem, "Record item cannot be null")
        this.recordItem = recordItem
    }

    fun getRecordItem(): Item? {
        return recordItem
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    fun play() {
        if (recordItem is ItemRecord) {
            when (recordItem.getId()) {
                Item.RECORD_13 -> this.getLevel().addSound(this, Sound.RECORD_13)
                Item.RECORD_CAT -> this.getLevel().addSound(this, Sound.RECORD_CAT)
                Item.RECORD_BLOCKS -> this.getLevel().addSound(this, Sound.RECORD_BLOCKS)
                Item.RECORD_CHIRP -> this.getLevel().addSound(this, Sound.RECORD_CHIRP)
                Item.RECORD_FAR -> this.getLevel().addSound(this, Sound.RECORD_FAR)
                Item.RECORD_MALL -> this.getLevel().addSound(this, Sound.RECORD_MALL)
                Item.RECORD_MELLOHI -> this.getLevel().addSound(this, Sound.RECORD_MELLOHI)
                Item.RECORD_STAL -> this.getLevel().addSound(this, Sound.RECORD_STAL)
                Item.RECORD_STRAD -> this.getLevel().addSound(this, Sound.RECORD_STRAD)
                Item.RECORD_WARD -> this.getLevel().addSound(this, Sound.RECORD_WARD)
                Item.RECORD_11 -> this.getLevel().addSound(this, Sound.RECORD_11)
                Item.RECORD_WAIT -> this.getLevel().addSound(this, Sound.RECORD_WAIT)
                Item.RECORD_PIGSTEP -> this.getLevel().addSound(this, Sound.RECORD_PIGSTEP)
            }
        }
    }

    //TODO: Transfer the stop sound to the new sound method
    fun stop() {
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_STOP_RECORD)
    }

    fun dropItem() {
        if (recordItem.getId() !== 0) {
            stop()
            this.level.dropItem(this.up(), recordItem)
            recordItem = Item.get(0)
        }
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putCompound("RecordItem", NBTIO.putItemHelper(recordItem))
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() = getDefaultCompound(this, JUKEBOX)
                .putCompound("RecordItem", NBTIO.putItemHelper(recordItem))

    @Override
    override fun onBreak() {
        dropItem()
    }
}