package cn.nukkit.level.format.anvil.util

import cn.nukkit.api.API

@ParametersAreNonnullByDefault
@Log4j2
class BlockStorage {
    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<BlockStorage>(0)
        private const val FLAG_HAS_ID: Byte = 1
        private const val FLAG_HAS_ID_EXTRA: Byte = 2
        private const val FLAG_HAS_DATA_EXTRA: Byte = 4
        private const val FLAG_HAS_DATA_BIG: Byte = 8
        private const val FLAG_HAS_DATA_HUGE: Byte = 16
        private const val FLAG_PALETTE_UPDATED: Byte = 32
        private val FLAG_ENABLE_ID_EXTRA: Byte = (FLAG_HAS_ID or FLAG_HAS_ID_EXTRA).toByte()
        private val FLAG_ENABLE_DATA_EXTRA: Byte = (FLAG_HAS_ID or FLAG_HAS_DATA_EXTRA).toByte()
        private val FLAG_ENABLE_DATA_BIG: Byte = (FLAG_ENABLE_DATA_EXTRA or FLAG_HAS_DATA_BIG).toByte()
        private val FLAG_ENABLE_DATA_HUGE: Byte = (FLAG_ENABLE_DATA_BIG or FLAG_HAS_DATA_HUGE).toByte()
        private val FLAG_EVERYTHING_ENABLED: Byte = (FLAG_ENABLE_DATA_HUGE or FLAG_ENABLE_ID_EXTRA or FLAG_PALETTE_UPDATED).toByte()
        private const val BLOCK_ID_MASK = 0x00FF
        private const val BLOCK_ID_EXTRA_MASK = 0xFF00
        private const val BLOCK_ID_FULL = BLOCK_ID_MASK or BLOCK_ID_EXTRA_MASK
        const val SECTION_SIZE = 4096
        private val EMPTY: Array<BlockState?> = arrayOfNulls<BlockState>(SECTION_SIZE)
        private fun getIndex(x: Int, y: Int, z: Int): Int {
            val index = (x shl 8) + (z shl 4) + y // XZY = Bedrock format
            Preconditions.checkArgument(index >= 0 && index < SECTION_SIZE, "Invalid index")
            return index
        }

        init {
            Arrays.fill(EMPTY, BlockState.AIR)
        }
    }

    private val palette: PalettedBlockStorage
    private val states: Array<BlockState>
    private var flags = FLAG_PALETTE_UPDATED

    @Nullable
    private var denyStates: BitSet? = null

    constructor() {
        states = EMPTY.clone()
        palette = PalettedBlockStorage()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @API(definition = INTERNAL, usage = BLEEDING)
    internal constructor(states: Array<BlockState>, flags: Byte, palette: PalettedBlockStorage, @Nullable denyStates: BitSet?) {
        this.states = states
        this.flags = flags
        this.palette = palette
        this.denyStates = denyStates
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @Nonnegative
    fun getBlockData(x: Int, y: Int, z: Int): Int {
        return states[getIndex(x, y, z)].getSignedBigDamage()
    }

    @Nonnegative
    fun getBlockId(x: Int, y: Int, z: Int): Int {
        return states[getIndex(x, y, z)].getBlockId()
    }

    fun setBlockId(x: Int, y: Int, z: Int, @Nonnegative id: Int) {
        val index = getIndex(x, y, z)
        setBlockState(index, states[index].withBlockId(id))
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    fun setBlockData(x: Int, y: Int, z: Int, @Nonnegative data: Int) {
        val index = getIndex(x, y, z)
        setBlockState(index, states[index].withData(data))
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.4.0.0-PN")
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun setBlock(x: Int, y: Int, z: Int, @Nonnegative id: Int, @Nonnegative data: Int) {
        val index = getIndex(x, y, z)
        val state: BlockState = BlockState.of(id, data)
        setBlockState(index, state)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun getFullBlock(x: Int, y: Int, z: Int): Int {
        return getFullBlock(getIndex(x, y, z))
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    fun setFullBlock(x: Int, y: Int, z: Int, @Nonnegative value: Int) {
        this.setFullBlock(getIndex(x, y, z), value)
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun getAndSetBlock(x: Int, y: Int, z: Int, @Nonnegative id: Int, @Nonnegative meta: Int): BlockState {
        return setBlockState(getIndex(x, y, z), BlockState.of(id, meta))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getAndSetBlockState(x: Int, y: Int, z: Int, state: BlockState): BlockState {
        return setBlockState(getIndex(x, y, z), state)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBlockState(x: Int, y: Int, z: Int, state: BlockState) {
        setBlockState(getIndex(x, y, z), state)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN", replaceWith = "getAndSetFullBlock")
    fun getAndSetFullBlock(x: Int, y: Int, z: Int, value: Int): Int {
        return getAndSetFullBlock(getIndex(x, y, z), value)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    private fun getAndSetFullBlock(index: Int, value: Int): Int {
        Preconditions.checkArgument(value < 0x1FF shl Block.DATA_BITS or Block.DATA_MASK, "Invalid full block")
        val blockId = value shr Block.DATA_BITS and BLOCK_ID_FULL
        val data = value and Block.DATA_MASK
        val newState: BlockState = BlockState.of(blockId, data)
        val oldState: BlockState = states[index]
        if (oldState.equals(newState)) {
            return value
        }
        setBlockState(index, newState)
        return oldState.getFullId()
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    private fun getFullBlock(index: Int): Int {
        return states[index].getFullId()
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    private fun setFullBlock(index: Int, value: Int) {
        Preconditions.checkArgument(value < BLOCK_ID_FULL shl Block.DATA_BITS or Block.DATA_MASK, "Invalid full block")
        val blockId = value shr Block.DATA_BITS and BLOCK_ID_FULL
        val data = value and Block.DATA_MASK
        val state: BlockState = BlockState.of(blockId, data)
        setBlockState(index, state)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun setBlockState(index: Int, state: BlockState): BlockState {
        if (log.isTraceEnabled() && !state.isCachedValidationValid()) {
            try {
                val runtimeId: Int = state.getBlock().getRuntimeId()
                if (runtimeId == BlockStateRegistry.getFallbackRuntimeId()) {
                    log.trace("Setting a state that will become info update! State: {}", state, RuntimeException())
                }
            } catch (e: InvalidBlockStateException) {
                log.trace("Setting an invalid state! State: {}", state, e)
            }
        }
        val previous: BlockState = states[index]
        if (previous.equals(state)) {
            return previous
        }
        states[index] = state
        updateFlags(index, previous, state)
        if (getFlag(FLAG_PALETTE_UPDATED)) {
            val runtimeId: Int = state.getRuntimeId()
            if (runtimeId == BlockStateRegistry.getFallbackRuntimeId() && !state.equals(BlockStateRegistry.getFallbackBlockState())) {
                delayPaletteUpdates()
            } else {
                palette.setBlock(index, runtimeId)
            }
        }
        return previous
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun delayPaletteUpdates() {
        setFlag(FLAG_PALETTE_UPDATED, false)
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isPaletteUpdateDelayed: Boolean
        get() = !getFlag(FLAG_PALETTE_UPDATED)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockState(x: Int, y: Int, z: Int): BlockState {
        return states[getIndex(x, y, z)]
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun recheckBlocks() {
        flags = computeFlags((flags and FLAG_PALETTE_UPDATED) as Byte, *states)
    }

    private fun updateFlags(index: Int, previous: BlockState, state: BlockState) {
        if (flags != FLAG_EVERYTHING_ENABLED) {
            flags = computeFlags(flags, state)
        }
        if (denyStates != null) {
            when (previous.getBlockId()) {
                BlockID.DENY -> clearDeny(index)
                BlockID.ALLOW -> clearAllow(index)
                BlockID.BORDER_BLOCK -> clearBorder(index)
                else -> {
                }
            }
        }
        when (state.getBlockId()) {
            BlockID.DENY -> deny(index)
            BlockID.BORDER_BLOCK -> border(index)
            BlockID.ALLOW -> allow(index)
            else -> {
            }
        }
    }

    private fun border(index: Int) {
        var index = index
        if (denyStates == null) {
            denyStates = BitSet()
        }
        index = index and 0xF.inv() shl 1
        for (y in 0..0xF) {
            denyStates.set(index++) // Deny
            denyStates.set(index++) // Allow
            // Both deny and allow means border
        }
    }

    private fun deny(index: Int) {
        var index = index
        if (denyStates == null) {
            denyStates = BitSet()
        }
        var y = index and 0xF
        index = index shl 1
        var first = true
        while (y <= 0xF) {
            if (denyStates.get(index + 1)) { //Allow
                if (first) { // Replacing an allow block with a deny block
                    if (denyStates.get(index)) { // If the XZ pos have a border, the border takes priority
                        return
                    }
                    denyStates.clear(index + 1)
                } else if (states[index shr 1].getBlockId() === BlockID.ALLOW) {
                    // Check if the allow state is actually from a allow block or from a previous removal
                    return
                } else {
                    denyStates.clear(index + 1)
                }
            }
            denyStates.set(index)
            y++
            index += 2
            first = false
        }
    }

    private fun allow(index: Int) {
        var index = index
        if (denyStates == null) {
            denyStates = BitSet()
        }
        var y = index and 0xF
        index = index shl 1
        var first = true
        while (y <= 0xF) {
            if (denyStates.get(index)) { //Deny
                if (first) { // Replacing a deny block with an allow block
                    if (denyStates.get(index + 1)) { // If the XZ pos have a border, the border takes priority
                        return
                    }
                    denyStates.clear(index)
                } else if (states[index shr 1].getBlockId() === BlockID.DENY) {
                    // Check if the deny state is actually from a deny block or from a previous removal
                    return
                } else {
                    denyStates.clear(index)
                }
            }
            denyStates.set(index + 1)
            y++
            index += 2
            first = false
        }
    }

    private fun clearAllow(index: Int) {
        var index = index
        assert(denyStates != null)
        var y = index and 0xF
        index = index shl 1
        while (y <= 0xF) {
            if (denyStates.get(index)) { // Deny or border
                break
            }
            denyStates.clear(index + 1) // Remove the allow
            y++
            index += 2
            index++
        }
    }

    private fun clearDeny(index: Int) {
        var index = index
        assert(denyStates != null)
        var y = index and 0xF
        index = index shl 1
        while (y <= 0xF) {
            if (denyStates.get(index + 1)) { // Allow or border
                break
            }
            denyStates.clear(index) // Remove the deny
            y++
            index += 2
            index++
        }
    }

    private fun clearBorder(index: Int) {
        assert(denyStates != null)

        // Check if there's an other border
        val bottomIndex = index and 0xF.inv()
        val topIndex = index or 0xF
        for (blockIndex in bottomIndex until topIndex) {
            if (states[blockIndex].getBlockId() === BlockID.BORDER_BLOCK) {
                return
            }
        }

        // Clear the border flags
        var removeDeny = true
        var removeAllow = true
        var blockIndex = bottomIndex
        var flagIndex = blockIndex shl 1
        while (blockIndex < topIndex) {
            when (states[blockIndex].getBlockId()) {
                BlockID.ALLOW -> {
                    removeDeny = true
                    removeAllow = false
                }
                BlockID.DENY -> {
                    removeDeny = false
                    removeAllow = true
                }
                else -> {
                }
            }
            if (removeDeny) {
                denyStates.clear(flagIndex)
            }
            if (removeAllow) {
                denyStates.clear(flagIndex + 1)
            }
            blockIndex++
            flagIndex += 2
        }
    }

    private fun computeFlags(newFlags: Byte, vararg states: BlockState): Byte {
        var newFlags = newFlags
        for (state in states) {
            val blockId: Int = state.getBlockId()
            if (blockId and BLOCK_ID_EXTRA_MASK != 0) {
                newFlags = newFlags or FLAG_ENABLE_ID_EXTRA
            } else if (blockId != 0) {
                newFlags = newFlags or FLAG_HAS_ID
            }
            val bitSize: Int = state.getBitSize()
            if (bitSize > 16) {
                newFlags = newFlags or FLAG_ENABLE_DATA_HUGE
            } else if (bitSize > 8) {
                newFlags = newFlags or FLAG_ENABLE_DATA_BIG
            } else if (bitSize > 4) {
                newFlags = newFlags or FLAG_ENABLE_DATA_EXTRA
            } else if (bitSize > 1 || blockId != 0) {
                newFlags = newFlags or FLAG_HAS_ID
            }
            if (newFlags == FLAG_EVERYTHING_ENABLED) {
                return newFlags
            }
        }
        return newFlags
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun copy(): BlockStorage {
        val deny: BitSet? = denyStates
        return BlockStorage(states.clone(), flags, palette.copy(), (if (deny != null) deny.clone() else null) as BitSet?)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun immutableCopy(): ImmutableBlockStorage {
        return ImmutableBlockStorage(states, flags, palette, denyStates)
    }

    private fun getFlag(flag: Byte): Boolean {
        return flags and flag == flag.toInt()
    }

    private fun setFlag(flag: Byte, value: Boolean) {
        if (value) {
            flags = flags or flag
        } else {
            flags = flags and flag.inv()
        }
    }

    fun hasBlockIds(): Boolean {
        return getFlag(FLAG_HAS_ID)
    }

    fun hasBlockIdExtras(): Boolean {
        return getFlag(FLAG_HAS_ID_EXTRA)
    }

    fun hasBlockDataExtras(): Boolean {
        return getFlag(FLAG_HAS_DATA_EXTRA)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasBlockDataBig(): Boolean {
        return getFlag(FLAG_HAS_DATA_BIG)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasBlockDataHuge(): Boolean {
        return getFlag(FLAG_HAS_DATA_HUGE)
    }

    private val isPaletteUpdated: Boolean
        private get() = getFlag(FLAG_PALETTE_UPDATED)

    @Since("1.4.0.0-PN")
    fun writeTo(stream: BinaryStream?) {
        if (!isPaletteUpdated) {
            for (i in states.indices) {
                palette.setBlock(i, states[i].getRuntimeId())
            }
            setFlag(FLAG_PALETTE_UPDATED, true)
        }
        palette.writeTo(stream)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun iterateStates(consumer: BlockPositionDataConsumer<BlockState?>) {
        for (i in states.indices) {
            // XZY = Bedrock format
            //int index = (x << 8) + (z << 4) + y; // XZY = Bedrock format
            val x: Int = i shr 8 and 0xF
            val z: Int = i shr 4 and 0xF
            val y: Int = i and 0xF
            consumer.accept(x, y, z, states[i])
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockChangeStateAbove(x: Int, y: Int, z: Int): Int {
        val denyFlags: BitSet = denyStates ?: return 0
        val index = getIndex(x, y, z) shl 1
        return (if (denyFlags.get(index)) 0x1 else 0x0) or if (denyFlags.get(index + 1)) 0x2 else 0x0
    }
}