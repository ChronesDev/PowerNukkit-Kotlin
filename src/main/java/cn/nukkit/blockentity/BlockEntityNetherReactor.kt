package cn.nukkit.blockentity

import cn.nukkit.api.PowerNukkitOnly

/**
 * This entity allows to manipulate the save state of a nether reactor core, but changing it
 * will cause no visual change. To see the changes in the world it would be necessary to
 * change the block data value to `0 1 or 3` but that is impossible in the recent versions
 * because Minecraft Bedrock Edition has moved from block data to the block property & block state
 * system and did not create a block property for the old nether reactor core block, making it
 * impossible for the server to tell the client to render the red and dark versions of the block.
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockEntityNetherReactor @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    private var reactorState: NetherReactorState? = null
    private var progress = 0

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = getLevelBlock().getId() === BlockID.NETHER_REACTOR

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getReactorState(): NetherReactorState? {
        return reactorState
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setReactorState(reactorState: NetherReactorState?) {
        this.reactorState = reactorState
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getProgress(): Int {
        return progress
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setProgress(progress: Int) {
        this.progress = MathHelper.clamp(progress, 0, 900)
    }

    @Override
    protected override fun initBlockEntity() {
        reactorState = NetherReactorState.READY
        if (namedTag.containsShort("Progress")) {
            progress = namedTag.getShort("Progress") as Short.toInt()
        }
        reactorState = if (namedTag.containsByte("HasFinished") && namedTag.getBoolean("HasFinished")) {
            NetherReactorState.FINISHED
        } else if (namedTag.containsByte("IsInitialized") && namedTag.getBoolean("IsInitialized")) {
            NetherReactorState.INITIALIZED
        } else {
            NetherReactorState.READY
        }
        super.initBlockEntity()
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        val reactorState: NetherReactorState? = getReactorState()
        namedTag.putShort("Progress", getProgress())
        namedTag.putBoolean("HasFinished", reactorState === NetherReactorState.FINISHED)
        namedTag.putBoolean("IsInitialized", reactorState === NetherReactorState.INITIALIZED)
    }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val reactorState: NetherReactorState? = getReactorState()
            return CompoundTag()
                    .putString("id", BlockEntity.NETHER_REACTOR)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
                    .putShort("Progress", getProgress())
                    .putBoolean("HasFinished", reactorState === NetherReactorState.FINISHED)
                    .putBoolean("IsInitialized", reactorState === NetherReactorState.INITIALIZED)
        }
}