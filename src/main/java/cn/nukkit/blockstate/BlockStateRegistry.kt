package cn.nukkit.blockstate

import cn.nukkit.Server

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@UtilityClass
@ParametersAreNonnullByDefault
@Log4j2
class BlockStateRegistry {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    val BIG_META_MASK = -0x1
    private val asyncStateRemover: ExecutorService = Executors.newSingleThreadExecutor()
    private val BLOCK_ID_NAME_PATTERN: Pattern = Pattern.compile("^blockid:(\\d+)$")
    private val updateBlockRegistration: Registration? = null
    private val blockStateRegistration: Map<BlockState, Registration> = ConcurrentHashMap()
    private val stateIdRegistration: Map<String, Registration> = ConcurrentHashMap()
    private val runtimeIdRegistration: Int2ObjectMap<Registration> = Int2ObjectOpenHashMap()
    private val blockIdToPersistenceName: Int2ObjectMap<String> = Int2ObjectOpenHashMap()
    private val persistenceNameToBlockId: Map<String, Integer> = LinkedHashMap()
    private val blockPaletteBytes: ByteArray

    companion object {
        @Nullable
        private fun findRegistrationByRuntimeId(runtimeId: Int): Registration {
            return runtimeIdRegistration.get(runtimeId)
        }

        //<editor-fold desc="static initialization" defaultstate="collapsed">
        init {

            //<editor-fold desc="Loading block_ids.csv" defaultstate="collapsed">
            try {
                Server::class.java.getClassLoader().getResourceAsStream("block_ids.csv").use { stream ->
                    if (stream == null) {
                        throw AssertionError("Unable to locate block_ids.csv")
                    }
                    val count = 0
                    try {
                        BufferedReader(InputStreamReader(stream)).use { reader ->
                            var line: String
                            while (reader.readLine().also { cn.nukkit.blockstate.line = it } != null) {
                                cn.nukkit.blockstate.count++
                                cn.nukkit.blockstate.line = cn.nukkit.blockstate.line.trim()
                                if (cn.nukkit.blockstate.line.isEmpty()) {
                                    continue
                                }
                                val parts: Array<String> = cn.nukkit.blockstate.line.split(",")
                                Preconditions.checkArgument(cn.nukkit.blockstate.parts.size == 2 || cn.nukkit.blockstate.parts.get(0).matches("^[0-9]+$"))
                                if (cn.nukkit.blockstate.parts.size > 1 && cn.nukkit.blockstate.parts.get(1).startsWith("minecraft:")) {
                                    val id: Int = Integer.parseInt(cn.nukkit.blockstate.parts.get(0))
                                    blockIdToPersistenceName.put(cn.nukkit.blockstate.id, cn.nukkit.blockstate.parts.get(1))
                                    persistenceNameToBlockId.put(cn.nukkit.blockstate.parts.get(1), cn.nukkit.blockstate.id)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        throw IOException("Error reading the line " + cn.nukkit.blockstate.count + " of the block_ids.csv", e)
                    }
                }
            } catch (e: IOException) {
                throw AssertionError(e)
            }
            //</editor-fold>

            //<editor-fold desc="Loading canonical_block_states.nbt" defaultstate="collapsed">
            val tags: List<CompoundTag> = ArrayList()
            try {
                Server::class.java.getClassLoader().getResourceAsStream("canonical_block_states.nbt").use { stream ->
                    if (stream == null) {
                        throw AssertionError("Unable to locate block state nbt")
                    }
                    BufferedInputStream(stream).use { bis ->
                        val runtimeId = 0
                        while (bis.available() > 0) {
                            val tag: CompoundTag = NBTIO.read(bis, ByteOrder.BIG_ENDIAN, true)
                            cn.nukkit.blockstate.tag.putInt("runtimeId", cn.nukkit.blockstate.runtimeId++)
                            cn.nukkit.blockstate.tag.putInt("blockId", persistenceNameToBlockId.getOrDefault(cn.nukkit.blockstate.tag.getString("name").toLowerCase(), -1))
                            cn.nukkit.blockstate.tags.add(cn.nukkit.blockstate.tag)
                        }
                    }
                }
            } catch (e: IOException) {
                throw AssertionError(e)
            }
            //</editor-fold>
            val infoUpdateRuntimeId: Integer? = null
            for (state in cn.nukkit.blockstate.tags) {
                val blockId: Int = cn.nukkit.blockstate.state.getInt("blockId")
                val runtimeId: Int = cn.nukkit.blockstate.state.getInt("runtimeId")
                val name: String = cn.nukkit.blockstate.state.getString("name").toLowerCase()
                if (cn.nukkit.blockstate.name.equals("minecraft:unknown")) {
                    cn.nukkit.blockstate.infoUpdateRuntimeId = cn.nukkit.blockstate.runtimeId
                }

                // Special condition: minecraft:wood maps 3 blocks, minecraft:wood, minecraft:log and minecraft:log2
                // All other cases, register the name normally
                if (isNameOwnerOfId(cn.nukkit.blockstate.name, cn.nukkit.blockstate.blockId)) {
                    registerPersistenceName(cn.nukkit.blockstate.blockId, cn.nukkit.blockstate.name)
                    registerStateId(cn.nukkit.blockstate.state, cn.nukkit.blockstate.runtimeId)
                }
            }
            if (cn.nukkit.blockstate.infoUpdateRuntimeId == null) {
                throw IllegalStateException("Could not find the minecraft:info_update runtime id!")
            }
            updateBlockRegistration = findRegistrationByRuntimeId(cn.nukkit.blockstate.infoUpdateRuntimeId)
            try {
                blockPaletteBytes = NBTIO.write(cn.nukkit.blockstate.tags, ByteOrder.LITTLE_ENDIAN, true)
            } catch (e: IOException) {
                throw ExceptionInInitializerError(e)
            }
        }
    }

    //</editor-fold>
    private fun isNameOwnerOfId(name: String, blockId: Int): Boolean {
        return !name.equals("minecraft:wood") || blockId == BlockID.WOOD_BARK
    }

    @Nonnull
    private fun getStateId(block: CompoundTag): String {
        val propertyMap: Map<String, String> = TreeMap(HumanStringComparator.getInstance())
        for (tag in block.getCompound("states").getAllTags()) {
            propertyMap.put(tag.getName(), tag.parseValue().toString())
        }
        val blockName: String = block.getString("name").toLowerCase(Locale.ENGLISH)
        Preconditions.checkArgument(!blockName.isEmpty(), "Couldn't find the block name!")
        val stateId = StringBuilder(blockName)
        propertyMap.forEach { name, value -> stateId.append(';').append(name).append('=').append(value) }
        return stateId.toString()
    }

    /**
     * @return `null` if the runtime id does not matches any known block state.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getBlockStateByRuntimeId(runtimeId: Int): BlockState? {
        val registration = findRegistrationByRuntimeId(runtimeId)
                ?: return null
        var state: BlockState? = registration.state
        if (state != null) {
            return state
        }
        val originalBlock: CompoundTag? = registration.originalBlock
        if (originalBlock != null) {
            state = buildStateFromCompound(originalBlock)
            if (state != null) {
                registration.state = state
                registration.originalBlock = null
            }
        }
        return state
    }

    @Nullable
    private fun buildStateFromCompound(block: CompoundTag?): BlockState? {
        val name: String = block.getString("name").toLowerCase(Locale.ENGLISH)
        val id: Integer = getBlockId(name) ?: return null
        var state: BlockState = BlockState.of(id)
        val properties: CompoundTag = block.getCompound("states")
        for (tag in properties.getAllTags()) {
            state = state.withProperty(tag.getName(), tag.parseValue().toString())
        }
        return state
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getRuntimeId(state: BlockState): Int {
        return getRegistration(state).runtimeId
    }

    private fun getRegistration(state: BlockState): Registration {
        return blockStateRegistration.computeIfAbsent(state) { obj: BlockStateRegistry, state: BlockState -> obj.findRegistration(state) }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getRuntimeId(blockId: Int): Int {
        return getRuntimeId(BlockState.of(blockId))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "getRuntimeId(BlockState state)", since = "1.3.0.0-PN")
    fun getRuntimeId(blockId: Int, meta: Int): Int {
        return getRuntimeId(BlockState.of(blockId, meta))
    }

    private fun findRegistration(state: BlockState): Registration? {
        // Special case for PN-96 PowerNukkit#210 where the world contains blocks like 0:13, 0:7, etc
        if (state.getBlockId() === BlockID.AIR) {
            val airRegistration = blockStateRegistration[BlockState.AIR]
            if (airRegistration != null) {
                return Registration(state, airRegistration.runtimeId, null)
            }
        }
        val registration = findRegistrationByStateId(state)
        removeStateIdsAsync(registration)
        return registration
    }

    private fun findRegistrationByStateId(state: BlockState): Registration? {
        var registration: Registration?
        try {
            registration = stateIdRegistration.remove(state.getStateId())
            if (registration != null) {
                registration.state = state
                registration.originalBlock = null
                return registration
            }
        } catch (e: Exception) {
            try {
                log.fatal("An error has occurred while trying to get the stateId of state: "
                        + "{}:{}"
                        + " - {}"
                        + " - {}",
                        state.getBlockId(),
                        state.getDataStorage(),
                        state.getProperties(),
                        blockIdToPersistenceName.get(state.getBlockId()),
                        e)
            } catch (e2: Exception) {
                e.addSuppressed(e2)
                log.fatal("An error has occurred while trying to get the stateId of state: {}:{}",
                        state.getBlockId(), state.getDataStorage(), e)
            }
        }
        try {
            registration = stateIdRegistration.remove(state.getLegacyStateId())
            if (registration != null) {
                registration.state = state
                registration.originalBlock = null
                return registration
            }
        } catch (e: Exception) {
            log.fatal("An error has occurred while trying to parse the legacyStateId of {}:{}", state.getBlockId(), state.getDataStorage(), e)
        }
        return logDiscoveryError(state)
    }

    private fun removeStateIdsAsync(@Nullable registration: Registration?) {
        if (registration != null && registration !== updateBlockRegistration) {
            asyncStateRemover.submit { stateIdRegistration.values().removeIf { r -> r.runtimeId === registration.runtimeId } }
        }
    }

    private fun logDiscoveryError(state: BlockState): Registration? {
        log.error("Found an unknown BlockId:Meta combination: {}:{}"
                + " - {}"
                + " - {}"
                + " - {}"
                + ", trying to repair or replacing with an \"UPDATE!\" block.",
                state.getBlockId(), state.getDataStorage(), state.getStateId(), state.getProperties(),
                blockIdToPersistenceName.get(state.getBlockId())
        )
        return updateBlockRegistration
    }

    val persistenceNames: List<String>
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = ArrayList(persistenceNameToBlockId.keySet())

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPersistenceName(blockId: Int): String {
        val persistenceName: String = blockIdToPersistenceName.get(blockId)
        if (persistenceName == null) {
            val fallback = "blockid:$blockId"
            log.warn("The persistence name of the block id {} is unknown! Using {} as an alternative!", blockId, fallback)
            registerPersistenceName(blockId, fallback)
            return fallback
        }
        return persistenceName
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun registerPersistenceName(blockId: Int, persistenceName: String) {
        synchronized(blockIdToPersistenceName) {
            val newName: String = persistenceName.toLowerCase()
            val oldName: String = blockIdToPersistenceName.putIfAbsent(blockId, newName)
            if (oldName != null && !persistenceName.equalsIgnoreCase(oldName)) {
                throw UnsupportedOperationException("The persistence name registration tried to replaced a name. Name:$persistenceName, Old:$oldName, Id:$blockId")
            }
            val oldId: Integer = persistenceNameToBlockId.putIfAbsent(newName, blockId)
            if (oldId != null && blockId != oldId) {
                blockIdToPersistenceName.remove(blockId)
                throw UnsupportedOperationException("The persistence name registration tried to replaced an id. Name:$persistenceName, OldId:$oldId, Id:$blockId")
            }
        }
    }

    private fun registerStateId(block: CompoundTag, runtimeId: Int) {
        val stateId = getStateId(block)
        val registration = Registration(null, runtimeId, block)
        val old: Registration = stateIdRegistration.putIfAbsent(stateId, registration)
        if (old != null && !old.equals(registration)) {
            throw UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:$old, New:$runtimeId, State:$stateId")
        }
        runtimeIdRegistration.put(runtimeId, registration)
    }

    private fun registerState(blockId: Int, meta: Int, originalState: CompoundTag, runtimeId: Int) {
        val state: BlockState = BlockState.of(blockId, meta)
        val registration = Registration(state, runtimeId, null)
        val old: Registration = blockStateRegistration.putIfAbsent(state, registration)
        if (old != null && !registration.equals(old)) {
            throw UnsupportedOperationException("The persistence NBT registration tried to replaced a runtime id. Old:$old, New:$runtimeId, State:$state")
        }
        runtimeIdRegistration.put(runtimeId, registration)
        stateIdRegistration.remove(getStateId(originalState))
        stateIdRegistration.remove(state.getLegacyStateId())
    }

    val blockPaletteDataVersion: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() {
            @SuppressWarnings("UnnecessaryLocalVariable") val obj: Object = blockPaletteBytes
            return obj.hashCode()
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockPaletteBytes(): ByteArray {
        return blockPaletteBytes.clone()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun putBlockPaletteBytes(stream: BinaryStream) {
        stream.put(blockPaletteBytes)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBlockPaletteLength(): Int {
        return blockPaletteBytes.size
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun copyBlockPaletteBytes(target: ByteArray?, targetIndex: Int) {
        System.arraycopy(blockPaletteBytes, 0, target, targetIndex, blockPaletteBytes.size)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings(["deprecation", "squid:CallToDepreca"])
    @Nonnull
    fun getProperties(blockId: Int): BlockProperties {
        val fullId = blockId shl Block.DATA_BITS
        var block: Block
        return if (fullId >= Block.fullList.length || Block.fullList.get(fullId).also { block = it } == null) {
            BlockUnknown.PROPERTIES
        } else block.getProperties()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun createMutableState(blockId: Int): MutableBlockState {
        return getProperties(blockId).createMutableState(blockId)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun createMutableState(blockId: Int, bigMeta: Int): MutableBlockState {
        val blockState: MutableBlockState = createMutableState(blockId)
        blockState.setDataStorageFromInt(bigMeta)
        return blockState
    }

    /**
     * @throws InvalidBlockStateException
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun createMutableState(blockId: Int, storage: Number?): MutableBlockState {
        val blockState: MutableBlockState = createMutableState(blockId)
        blockState.setDataStorage(storage)
        return blockState
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getUpdateBlockRegistration(): Int {
        return updateBlockRegistration!!.runtimeId
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getBlockId(persistenceName: String): Integer? {
        val blockId: Integer? = persistenceNameToBlockId[persistenceName]
        if (blockId != null) {
            return blockId
        }
        val matcher: Matcher = BLOCK_ID_NAME_PATTERN.matcher(persistenceName)
        if (matcher.matches()) {
            try {
                return Integer.parseInt(matcher.group(1))
            } catch (ignored: NumberFormatException) {
            }
        }
        return null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getFallbackRuntimeId(): Int {
        return updateBlockRegistration!!.runtimeId
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getFallbackBlockState(): BlockState? {
        return updateBlockRegistration!!.state
    }

    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    private class Registration {
        @Nullable
        val state: BlockState? = null
        val runtimeId = 0

        @Nullable
        val originalBlock: CompoundTag? = null
    }
}