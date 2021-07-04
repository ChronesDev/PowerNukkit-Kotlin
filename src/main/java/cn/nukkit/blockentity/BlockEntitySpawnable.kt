package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockEntitySpawnable(chunk: FullChunk?, nbt: CompoundTag) : BlockEntity(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        super.initBlockEntity()
        spawnToAll()
    }

    val spawnCompound: CompoundTag
        get() = this.namedTag

    fun spawnTo(player: Player) {
        if (this.closed) {
            return
        }
        player.dataPacket(spawnPacket)
    }

    val spawnPacket: BlockEntityDataPacket
        get() = getSpawnPacket(null)

    fun getSpawnPacket(nbt: CompoundTag?): BlockEntityDataPacket {
        var nbt: CompoundTag? = nbt
        if (nbt == null) {
            nbt = spawnCompound
        }
        val pk = BlockEntityDataPacket()
        pk.x = this.x as Int
        pk.y = this.y as Int
        pk.z = this.z as Int
        try {
            pk.namedTag = NBTIO.write(nbt, ByteOrder.LITTLE_ENDIAN, true)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return pk
    }

    fun spawnToAll() {
        if (this.closed) {
            return
        }
        for (player in this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values()) {
            if (player.spawned) {
                spawnTo(player)
            }
        }
    }

    /**
     * Called when a player updates a block entity's NBT data
     * for example when writing on a sign.
     *
     * @param nbt tag
     * @param player player
     * @return bool indication of success, will respawn the tile to the player if false.
     */
    fun updateCompoundTag(nbt: CompoundTag?, player: Player?): Boolean {
        return false
    }
}