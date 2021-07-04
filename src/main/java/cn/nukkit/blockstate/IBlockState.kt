package cn.nukkit.blockstate

import cn.nukkit.Server

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
interface IBlockState {
    val blockId: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnegative get
    val dataStorage: Number
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull @Nonnegative get
    val isDefaultState: Boolean
        @PowerNukkitOnly @Since("1.4.0.0-PN") get
    val properties: BlockProperties
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get
    val legacyDamage: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()") @Nonnegative get
    val bigDamage: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()") @Unsigned get
    val signedBigDamage: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()") @Nonnegative get() = bigDamage
    val hugeDamage: BigInteger?
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull @Nonnegative get

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPropertyValue(propertyName: String?): Serializable

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     * @throws ClassCastException If the actual property value don't match the type of the given property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <V : Serializable?> getPropertyValue(property: BlockProperty<V>): V {
        return getCheckedPropertyValue(property.getName(), property.getValueClass())
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <V : Serializable?> getUncheckedPropertyValue(property: BlockProperty<V>): V {
        return getUncheckedPropertyValue(property.getName())
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(propertyName: String?): Int

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(property: BlockProperty<*>): Int {
        return getIntValue(property.getName())
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(propertyName: String?): Boolean

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(property: BlockProperty<*>): Boolean {
        return getBooleanValue(property.getName())
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPersistenceValue(propertyName: String?): String?

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPersistenceValue(property: BlockProperty<*>): String? {
        return getPersistenceValue(property.getName())
    }

    val persistenceName: String
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() = BlockStateRegistry.getPersistenceName(blockId)

    /**
     * Gets a unique persistence identification for this state based on the block properties.
     *
     * If the state holds an invalid meta, the result of [.getLegacyStateId] is returned.
     */
    val stateId: String?
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() {
            val properties: BlockProperties = properties
            val propertyMap: Map<String, String> = TreeMap(HumanStringComparator.getInstance())
            try {
                properties.getNames().forEach { name -> propertyMap.put(properties.getBlockProperty(name).getPersistenceName(), getPersistenceValue(name)) }
            } catch (e: InvalidBlockPropertyException) {
                logIBlockState.debug("Attempted to get the stateId of an invalid state {}:{}\nProperties: {}", blockId, dataStorage, properties, e)
                return legacyStateId
            }
            val stateId = StringBuilder(persistenceName)
            propertyMap.forEach { name, value -> stateId.append(';').append(name).append('=').append(value) }
            return stateId.toString()
        }
    val legacyStateId: String?
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() = "$persistenceName;nukkit-unknown=$dataStorage"
    val currentState: cn.nukkit.blockstate.BlockState
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    val block: Block?
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() {
            val block: Block = Block.get(blockId)
            block.setState(this)
            return block
        }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(@Nullable level: Level, x: Int, y: Int, z: Int): Block? {
        return getBlock(level, x, y, z, 0, false, null)
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(@Nullable level: Level, x: Int, y: Int, z: Int, layer: Int): Block? {
        return getBlock(level, x, y, z, layer, false, null)
    }

    /**
     * @throws InvalidBlockStateException if repair is false and the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(@Nullable level: Level, x: Int, y: Int, z: Int, layer: Int, repair: Boolean): Block? {
        return getBlock(level, x, y, z, layer, repair, null)
    }

    /**
     * @throws InvalidBlockStateException if repair is false and the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(@Nullable level: Level, x: Int, y: Int, z: Int, layer: Int, repair: Boolean, @Nullable callback: Consumer<BlockStateRepair?>?): Block? {
        val block: Block = Block.get(blockId)
        block.level = level
        block.x = x
        block.y = y
        block.z = z
        block.layer = layer
        val currentState: BlockState = currentState
        try {
            if (currentState.isCachedValidationValid()) {
                block.setState(currentState)
                return block
            }
        } catch (e: Exception) {
            logIBlockState.error("Unexpected error while trying to set the cached valid state to the block. State: {}, Block: {}", currentState, block, e)
        }
        try {
            block.setDataStorage(currentState.getDataStorage(), repair, callback)
        } catch (e: InvalidBlockStateException) {
            throw InvalidBlockStateException(currentState, "Invalid block state in layer " + layer + " at: " + Position(x, y, z, level), e)
        }
        return block
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(position: Position): Block? {
        return getBlock(position, 0)
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(position: Block): Block? {
        return getBlock(position, position.layer)
    }

    /**
     * @throws InvalidBlockStateException if the state contains invalid property values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlock(position: Position, layer: Int): Block? {
        return getBlock(position.getLevel(), position.getFloorX(), position.getFloorY(), position.getFloorZ(), layer)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(pos: Block): Block? {
        return getBlockRepairing(pos, pos.layer)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(position: Position, layer: Int): Block? {
        return getBlockRepairing(position.level, position, layer)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(@Nullable level: Level, pos: BlockVector3, layer: Int): Block? {
        return getBlockRepairing(level, pos.x, pos.y, pos.z, layer)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(@Nullable level: Level?, pos: Vector3?): Block? {
        return getBlockRepairing(level, pos, 0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(@Nullable level: Level, pos: Vector3, layer: Int): Block? {
        return getBlockRepairing(level, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(@Nullable level: Level, x: Int, y: Int, z: Int): Block? {
        return getBlockRepairing(level, x, y, z, 0)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(@Nullable level: Level, x: Int, y: Int, z: Int, layer: Int): Block? {
        return getBlockRepairing(level, x, y, z, layer, null)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getBlockRepairing(@Nullable level: Level, x: Int, y: Int, z: Int, layer: Int, @Nullable callback: Consumer<BlockStateRepair?>?): Block? {
        val repairs: List<BlockStateRepair> = ArrayList(0)
        var callbackChain: Consumer<BlockStateRepair?> = repairs::add
        if (!BlockStateRepairEvent.getHandlers().isEmpty()) {
            val manager: PluginManager = Server.getInstance().getPluginManager()
            callbackChain = callbackChain.andThen { repair -> manager.callEvent(BlockStateRepairEvent(repair)) }
        }
        if (callback != null) {
            callbackChain = callbackChain.andThen(callback)
        }
        var block: Block? = getBlock(level, x, y, z, layer, true, callbackChain)
        if (!BlockStateRepairFinishEvent.getHandlers().isEmpty()) {
            val event = BlockStateRepairFinishEvent(repairs, block)
            Server.getInstance().getPluginManager().callEvent(event)
            block = event.getResult()
        }
        if (!repairs.isEmpty() && logIBlockState.isDebugEnabled()) {
            logIBlockState.debug("The block that at Level:{}, X:{}, Y:{}, Z:{}, L:{} was repaired. Result: {}, Repairs: {}",
                    level, x, y, z, layer, block, repairs,
                    Exception("Stacktrace")
            )
        }
        return block
    }

    val runtimeId: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = BlockStateRegistry.getRuntimeId(currentState)
    val fullId: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself") get() = blockId shl Block.DATA_BITS or (legacyDamage and Block.DATA_MASK)
    val bigId: Long
        @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself") get() = blockId.toLong() shl 32 or (bigDamage and BlockStateRegistry.BIG_META_MASK).toLong()

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("rawtypes")
    @Nonnull
    fun getProperty(propertyName: String?): BlockProperty? {
        return properties.getBlockProperty(propertyName)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <T : BlockProperty<*>?> getCheckedProperty(propertyName: String?, tClass: Class<T>?): T {
        return properties.getBlockProperty(propertyName, tClass)
    }

    val propertyNames: Set<String?>?
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() = properties.getNames()

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <T> getCheckedPropertyValue(propertyName: String?, tClass: Class<T>): T {
        return tClass.cast(getPropertyValue(propertyName))
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    fun <T> getUncheckedPropertyValue(propertyName: String?): T {
        return getPropertyValue(propertyName)
    }

    val bitSize: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = properties.getBitSize()
    val exactIntStorage: Int
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnegative get

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun asItemBlock(): ItemBlock? {
        return asItemBlock(1)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun asItemBlock(count: Int): ItemBlock? {
        return currentState.asItemBlock(count)
    }
}