package cn.nukkit.blockstate

import cn.nukkit.api.DeprecationDetails

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
@ParametersAreNonnullByDefault
class BlockState : Serializable, IBlockState {
    @Getter
    @Nonnegative
    private override val blockId: Int

    @Nonnull
    @Nonnegative
    private val storage: Storage

    @ToString.Exclude
    @Nonnull
    private var valid: OptionalBoolean = OptionalBoolean.empty()

    private constructor(@Nonnegative blockId: Int) {
        Validation.checkPositive("blockId", blockId)
        this.blockId = blockId
        storage = ZERO_STORAGE
    }

    private constructor(@Nonnegative blockId: Int, @Nonnegative blockData: Byte) {
        Validation.checkPositive("blockId", blockId)
        this.blockId = blockId
        storage = if (blockData.toInt() == 0) ZERO_STORAGE else ByteStorage(blockData)
    }

    private constructor(@Nonnegative blockId: Int, @Nonnegative blockData: Int) {
        Validation.checkPositive("blockId", blockId)
        this.blockId = blockId
        storage = if (blockData == 0) ZERO_STORAGE else  //blockData < 0?                  new IntStorage(blockData) :
            if (blockData <= Byte.MAX_VALUE) ByteStorage(blockData.toByte()) else IntStorage(blockData)
    }

    private constructor(@Nonnegative blockId: Int, @Nonnegative blockData: Long) {
        Validation.checkPositive("blockId", blockId)
        this.blockId = blockId
        storage = if (blockData == 0L) ZERO_STORAGE else  //blockData < 0?                  new LongStorage(blockData) :
            if (blockData <= Byte.MAX_VALUE) ByteStorage(blockData.toByte()) else if (blockData <= Integer.MAX_VALUE) IntStorage(blockData.toInt()) else LongStorage(blockData)
    }

    private constructor(@Nonnegative blockId: Int, @Nonnegative blockData: BigInteger) {
        Validation.checkPositive("blockId", blockId)
        this.blockId = blockId
        val zeroCmp: Int = BigInteger.ZERO.compareTo(blockData)
        if (zeroCmp == 0) {
            storage = ZERO_STORAGE
            //} else if (zeroCmp < 0) {
            //    storage = new BigIntegerStorage(blockData);
        } else if (blockData.compareTo(BYTE_LIMIT) <= 0) {
            storage = ByteStorage(blockData.byteValue())
        } else if (blockData.compareTo(INT_LIMIT) <= 0) {
            storage = IntStorage(blockData.intValue())
        } else if (blockData.compareTo(LONG_LIMIT) <= 0) {
            storage = LongStorage(blockData.longValue())
        } else {
            storage = BigIntegerStorage(blockData)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withData(@Nonnegative data: Int): BlockState {
        return of(blockId, data)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withData(@Nonnegative data: Long): BlockState {
        return of(blockId, data)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withData(@Nonnegative data: BigInteger?): BlockState {
        return of(blockId, data)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withData(@Nonnegative data: Number?): BlockState {
        return of(blockId, data)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withBlockId(@Nonnegative blockId: Int): BlockState {
        return storage.withBlockId(blockId)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <E : Serializable?> withProperty(property: BlockProperty<E>, @Nullable value: E): BlockState {
        return withProperty(property.getName(), value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withProperty(propertyName: String?, @Nullable value: Serializable?): BlockState {
        return storage.withProperty(blockId, properties, propertyName, value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun withProperty(propertyName: String?, persistenceValue: String?): BlockState {
        return storage.withPropertyString(blockId, properties, propertyName, persistenceValue)
    }

    /**
     * @throws NoSuchElementException If any of the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onlyWithProperties(vararg properties: BlockProperty<*>): BlockState {
        val names = arrayOfNulls<String>(properties.size)
        for (i in 0 until properties.size) {
            names[i] = properties[i].getName()
        }
        return onlyWithProperties(*names)
    }

    /**
     * @throws NoSuchElementException If any of the given property names is not found
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onlyWithProperties(vararg propertyNames: String?): BlockState {
        val properties: BlockProperties = properties
        val list: List<String> = Arrays.asList(propertyNames)
        if (!properties.getNames().containsAll(list)) {
            val missing: Set<String> = LinkedHashSet(list)
            missing.removeAll(properties.getNames())
            throw NoSuchElementException("Missing properties: " + String.join(", ", missing))
        }
        return storage.onlyWithProperties(this, list)
    }

    /**
     * @throws NoSuchElementException If the property was not found
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onlyWithProperty(name: String?): BlockState {
        return onlyWithProperties(name)
    }

    /**
     * @throws NoSuchElementException If the property was not found
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onlyWithProperty(property: BlockProperty<*>?): BlockState {
        return onlyWithProperties(property)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun onlyWithProperty(name: String?, value: Serializable?): BlockState {
        return storage.onlyWithProperty(this, name, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T : Serializable?> onlyWithProperty(property: BlockProperty<T>, value: T): BlockState {
        return onlyWithProperty(property.getName(), value)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun forItem(): BlockState {
        val allProperties: BlockProperties = properties
        val allNames: Set<String> = allProperties.getNames()
        val itemProperties: BlockProperties = allProperties.getItemBlockProperties()
        val itemNames: List<String> = itemProperties.getItemPropertyNames()
        return if (allNames.size() === itemNames.size() && allNames.containsAll(itemNames)) {
            this
        } else storage.onlyWithProperties(this, itemNames)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    override fun asItemBlock(count: Int): ItemBlock {
        val allProperties: BlockProperties = properties
        val allNames: Set<String> = allProperties.getNames()
        val itemBlockMeta: Int
        val itemProperties: BlockProperties = allProperties.getItemBlockProperties()
        val itemNames: List<String> = itemProperties.getItemPropertyNames()
        val trimmedState: BlockState
        if (allNames.size() === itemNames.size() && allNames.containsAll(itemNames)) {
            itemBlockMeta = exactIntStorage
            trimmedState = this
        } else if (itemNames.isEmpty()) {
            itemBlockMeta = 0
            trimmedState = if (isDefaultState) this else of(getBlockId())
        } else {
            trimmedState = storage.onlyWithProperties(this, itemNames)
            val itemState: MutableBlockState = itemProperties.createMutableState(getBlockId())
            itemNames.forEach { property -> itemState.setPropertyValue(property, getPropertyValue(property)) }
            itemBlockMeta = itemState.getExactIntStorage()
        }
        val runtimeId: Int = trimmedState.getRuntimeId()
        if (runtimeId == BlockStateRegistry.getUpdateBlockRegistration() && !"minecraft:info_update".equals(trimmedState.getPersistenceName())) {
            throw UnknownRuntimeIdException("The current block state can't be represented as an item. State: $trimmedState, Trimmed: $trimmedState ItemBlockMeta: $itemBlockMeta")
        }
        val block: Block = trimmedState.block
        return ItemBlock(block, itemBlockMeta, count)
    }

    override val dataStorage: Number
        @Nonnegative @Nonnull @Override get() = storage.number
    override val properties: BlockProperties
        @Nonnull @Override get() = BlockStateRegistry.getProperties(blockId)
    override val legacyDamage: Int
        @Nonnegative @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()") @Override get() = storage.legacyDamage
    override val bigDamage: Int
        @Unsigned @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()") @Override get() = storage.bigDamage
    override val signedBigDamage: Int
        @Nonnegative @Since("1.4.0.0-PN") @PowerNukkitOnly @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()") @Override get() = storage.signedBigDamage
    override val hugeDamage: BigInteger
        @Nonnegative @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull @Override get() = storage.hugeDamage

    @Nonnull
    @Override
    override fun getPropertyValue(propertyName: String?): Serializable {
        return storage.getPropertyValue(properties, propertyName)
    }

    @Override
    override fun getIntValue(propertyName: String?): Int {
        return storage.getIntValue(properties, propertyName)
    }

    @Override
    override fun getBooleanValue(propertyName: String?): Boolean {
        return storage.getBooleanValue(properties, propertyName)
    }

    @Nonnull
    @Override
    override fun getPersistenceValue(propertyName: String?): String {
        return storage.getPersistenceValue(properties, propertyName)
    }

    override val currentState: BlockState
        @Nonnull @Override get() = this
    override val bitSize: Int
        @Override get() = storage.bitSize

    /**
     * @throws ArithmeticException If the storage have more than 32 bits
     */
    override val exactIntStorage: Int
        @Since("1.4.0.0-PN") @PowerNukkitOnly @Override get() {
            val storageClass: Class<out Storage> = storage.getClass()
            if (bitSize >= 32 || storageClass !== ZeroStorage::class.java && storageClass !== ByteStorage::class.java && storageClass !== IntStorage::class.java) {
                throw ArithmeticException("$dataStorage cant be stored in a signed 32 bits integer without losses. It has $bitSize bits")
            }
            return signedBigDamage
        }
    override val isDefaultState: Boolean
        @Since("1.4.0.0-PN") @PowerNukkitOnly @Override get() = storage.isDefaultState

    @Override
    override fun equals(o: Object?): Boolean {
        if (this === o) return true
        if (o == null || getClass() !== o.getClass()) return false
        val that = o as BlockState
        if (blockId != that.blockId) return false
        return if (storage.bitSize != that.storage.bitSize) false else compareDataEquality(storage.number, that.storage.number)
    }

    @Override
    override fun hashCode(): Int {
        val bitSize = storage.bitSize
        var result = blockId
        result = 31 * result + bitSize
        if (bitSize <= 32) {
            result = 31 * result + storage.bigDamage
        } else if (bitSize <= 64) {
            result = 31 * result + Long.hashCode(storage.number.longValue())
        } else {
            result = 31 * result + storage.hugeDamage.hashCode()
        }
        return result
    }

    /**
     * @throws InvalidBlockStateException If the stored state is invalid
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun validate() {
        if (valid === OptionalBoolean.TRUE) {
            return
        }
        val properties: BlockProperties = properties
        if (storage.bitSize > properties.getBitSize()) {
            throw InvalidBlockStateException(this,
                    "The stored data overflows the maximum properties bits. Stored bits: " + storage.bitSize + ", " +
                            "Properties Bits: " + properties.getBitSize() + ", Stored data: " + storage.number
            )
        }
        try {
            storage.validate(properties)
            valid = OptionalBoolean.TRUE
        } catch (e: Exception) {
            valid = OptionalBoolean.FALSE
            throw InvalidBlockStateException(this, e)
        }
    }

    val isCachedValidationValid: Boolean
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = valid.orElse(false)
    val cachedValidation: OptionalBoolean
        @PowerNukkitOnly @Since("1.4.0.0-PN") get() = valid
    override val block: Block
        @Nonnull @Override get() = try {
            val block: Block = super@IBlockState.getBlock()
            valid = OptionalBoolean.TRUE
            block
        } catch (e: InvalidBlockStateException) {
            valid = OptionalBoolean.FALSE
            throw e
        }

    @Nonnull
    @Override
    override fun getBlock(@Nullable level: Level?, x: Int, y: Int, z: Int, layer: Int, repair: Boolean, @Nullable callback: Consumer<BlockStateRepair?>?): Block {
        var callback: Consumer<BlockStateRepair?>? = callback
        if (valid === OptionalBoolean.TRUE) {
            val block: Block = super@IBlockState.getBlock()
            block.x = x
            block.y = y
            block.z = z
            block.layer = layer
            block.level = level
            return block
        }
        if (valid === OptionalBoolean.FALSE) {
            return super@IBlockState.getBlock(level, x, y, z, layer, repair, callback)
        }
        val updater: Consumer<BlockStateRepair> = Consumer<BlockStateRepair> { r -> valid = OptionalBoolean.FALSE }
        callback = if (repair && callback != null) {
            updater.andThen(callback)
        } else {
            updater.andThen { rep -> throw InvalidBlockStateException(this, "Attempted to repair when repair was false. " + rep.toString(), rep.getValidationException()) }
        }
        return try {
            val block: Block = super@IBlockState.getBlock(level, x, y, z, layer, true, callback)
            if (valid === OptionalBoolean.EMPTY) {
                valid = OptionalBoolean.TRUE
            }
            block
        } catch (e: InvalidBlockStateException) {
            valid = OptionalBoolean.FALSE
            throw e
        }
    }

    @ParametersAreNonnullByDefault
    private interface Storage : Serializable {
        val number: Number
            @Nonnull get
        val legacyDamage: Int
        val bigDamage: Int
        val signedBigDamage: Int
            get() = bigDamage

        @Nonnull
        fun getPropertyValue(properties: BlockProperties?, propertyName: String?): Serializable
        fun getIntValue(properties: BlockProperties?, propertyName: String?): Int
        fun getBooleanValue(properties: BlockProperties?, propertyName: String?): Boolean

        @Nonnull
        fun withBlockId(blockId: Int): BlockState

        @Nonnull
        fun getPersistenceValue(properties: BlockProperties?, propertyName: String?): String
        val bitSize: Int
        val hugeDamage: BigInteger
            @Nonnull get

        @Nonnull
        fun withProperty(blockId: Int, properties: BlockProperties?, propertyName: String?, @Nullable value: Serializable?): BlockState

        @Nonnull
        fun onlyWithProperties(currentState: BlockState?, propertyNames: List<String?>?): BlockState

        @Nonnull
        fun onlyWithProperty(currentState: BlockState?, name: String?, value: Serializable?): BlockState
        fun validate(properties: BlockProperties?)
        val isDefaultState: Boolean

        @Nonnull
        fun withPropertyString(blockId: Int, properties: BlockProperties?, propertyName: String?, value: String?): BlockState
    }

    @ParametersAreNonnullByDefault
    private class ZeroStorage : Storage {
        override val bitSize: Int
            @Override get() = 1
        override val number: Number
            @Nonnull @Override get() = 0
        override val legacyDamage: Int
            @Override get() = 0
        override val bigDamage: Int
            @Override get() = 0
        override val hugeDamage: BigInteger
            @Nonnull @Override get() = BigInteger.ZERO

        @Nonnull
        @Override
        override fun getPropertyValue(properties: BlockProperties, propertyName: String?): Serializable {
            return properties.getValue(0, propertyName)
        }

        @Override
        override fun getIntValue(properties: BlockProperties, propertyName: String?): Int {
            return properties.getIntValue(0, propertyName)
        }

        @Override
        override fun getBooleanValue(properties: BlockProperties, propertyName: String?): Boolean {
            return properties.getBooleanValue(0, propertyName)
        }

        @Nonnull
        @Override
        override fun withBlockId(blockId: Int): BlockState {
            return of(blockId)
        }

        @Nonnull
        @Override
        override fun withProperty(blockId: Int, properties: BlockProperties, propertyName: String?, @Nullable value: Serializable?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setValue(0, propertyName, value))
        }

        @Nonnull
        @Override
        override fun withPropertyString(blockId: Int, properties: BlockProperties, propertyName: String?, value: String?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setPersistenceValue(0, propertyName, value))
        }

        @Nonnull
        @Override
        override fun onlyWithProperties(currentState: BlockState, propertyNames: List<String?>?): BlockState {
            return currentState
        }

        @Nonnull
        @Override
        override fun onlyWithProperty(currentState: BlockState, name: String?, value: Serializable?): BlockState {
            val properties: BlockProperties = currentState.properties
            return if (!properties.contains(name)) {
                currentState
            } else of(currentState.blockId, properties.setValue(0, name, value))
        }

        @Override
        override fun validate(properties: BlockProperties?) {
            // Meta 0 is always valid
        }

        override val isDefaultState: Boolean
            @Override get() = true

        @Nonnull
        @Override
        override fun getPersistenceValue(properties: BlockProperties, propertyName: String?): String {
            return properties.getPersistenceValue(0, propertyName)
        }

        @Override
        override fun toString(): String {
            return "0"
        }

        companion object {
            private const val serialVersionUID = -4199347838375711088L
        }
    }

    private inner class ByteStorage(private val data: Byte) : Storage {
        @Getter
        private override val bitSize: Int
        override val number: Number
            @Nonnull @Override get() = data
        override val legacyDamage: Int
            @Override get() = data and Block.DATA_MASK
        override val bigDamage: Int
            @Override get() = data.toInt()
        override val hugeDamage: BigInteger
            @Nonnull @Override get() = BigInteger.valueOf(data)

        @Nonnull
        @Override
        override fun getPropertyValue(properties: BlockProperties, propertyName: String?): Serializable {
            return properties.getValue(data, propertyName)
        }

        @Override
        override fun getIntValue(properties: BlockProperties, propertyName: String?): Int {
            return properties.getIntValue(data, propertyName)
        }

        @Override
        override fun getBooleanValue(properties: BlockProperties, propertyName: String?): Boolean {
            return properties.getBooleanValue(data, propertyName)
        }

        @Nonnull
        @Override
        override fun withBlockId(blockId: Int): BlockState {
            return of(blockId, data)
        }

        @Nonnull
        @Override
        override fun withProperty(blockId: Int, properties: BlockProperties, propertyName: String?, @Nullable value: Serializable?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setValue(data, propertyName, value))
        }

        @Nonnull
        @Override
        override fun withPropertyString(blockId: Int, properties: BlockProperties, propertyName: String?, value: String?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setPersistenceValue(data, propertyName, value))
        }

        @Nonnull
        @Override
        override fun onlyWithProperties(currentState: BlockState?, propertyNames: List<String?>): BlockState {
            return of(blockId,
                    properties.reduceInt(data
                    ) { property, offset, current -> if (propertyNames.contains(property.getName())) current else property.setValue(current, offset, null) }
            )
        }

        @Nonnull
        @Override
        @SuppressWarnings(["unchecked", "java:S1905", "rawtypes"])
        override fun onlyWithProperty(currentState: BlockState?, name: String, value: Serializable?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId,
                    properties.reduceInt(data
                    ) { property, offset, current -> (property as BlockProperty).setValue(current, offset, if (name.equals(property.getName())) value else null) }
            )
        }

        @Override
        override fun validate(properties: BlockProperties) {
            properties.forEach { property, offset -> property.validateMeta(data, offset) }
        }

        override val isDefaultState: Boolean
            @Override get() = data.toInt() == 0

        @Nonnull
        @Override
        override fun getPersistenceValue(properties: BlockProperties, propertyName: String?): String {
            return properties.getPersistenceValue(data, propertyName)
        }

        @Override
        override fun toString(): String {
            return Byte.toString(data)
        }

        init {
            this.bitSize = NukkitMath.bitLength(data)
        }
    }

    @ParametersAreNonnullByDefault
    private inner class IntStorage(override val bigDamage: Int) : Storage {
        @Getter
        private override val bitSize: Int
        override val number: Number
            @Nonnull @Override get() = bigDamage
        override val legacyDamage: Int
            @Override get() = this.bigDamage and Block.DATA_MASK

        @Nonnull
        @Override
        override fun getPropertyValue(properties: BlockProperties, propertyName: String?): Serializable {
            return properties.getValue(this.bigDamage, propertyName)
        }

        @Override
        override fun getIntValue(properties: BlockProperties, propertyName: String?): Int {
            return properties.getIntValue(this.bigDamage, propertyName)
        }

        @Override
        override fun getBooleanValue(properties: BlockProperties, propertyName: String?): Boolean {
            return properties.getBooleanValue(this.bigDamage, propertyName)
        }

        @Nonnull
        @Override
        override fun withBlockId(blockId: Int): BlockState {
            return of(blockId, this.bigDamage)
        }

        @Nonnull
        @Override
        override fun withProperty(blockId: Int, properties: BlockProperties, propertyName: String?, @Nullable value: Serializable?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setValue(this.bigDamage, propertyName, value))
        }

        @Nonnull
        @Override
        override fun withPropertyString(blockId: Int, properties: BlockProperties, propertyName: String?, value: String?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setPersistenceValue(this.bigDamage, propertyName, value))
        }

        @Nonnull
        @Override
        override fun onlyWithProperties(currentState: BlockState?, propertyNames: List<String?>): BlockState {
            return of(blockId,
                    properties.reduceInt(this.bigDamage
                    ) { property, offset, current -> if (propertyNames.contains(property.getName())) current else property.setValue(current, offset, null) }
            )
        }

        @Nonnull
        @Override
        @SuppressWarnings(["unchecked", "java:S1905", "rawtypes"])
        override fun onlyWithProperty(currentState: BlockState?, name: String, value: Serializable?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId,
                    properties.reduceInt(this.bigDamage
                    ) { property, offset, current -> (property as BlockProperty).setValue(current, offset, if (name.equals(property.getName())) value else null) }
            )
        }

        @Override
        override fun validate(properties: BlockProperties) {
            properties.forEach { property, offset -> property.validateMeta(this.bigDamage, offset) }
        }

        override val isDefaultState: Boolean
            @Override get() = this.bigDamage == 0

        @Nonnull
        @Override
        override fun getPersistenceValue(properties: BlockProperties, propertyName: String?): String {
            return properties.getPersistenceValue(this.bigDamage, propertyName)
        }

        override val hugeDamage: BigInteger
            @Nonnull @Override get() = BigInteger.valueOf(this.bigDamage)

        @Override
        override fun toString(): String {
            return Integer.toString(this.bigDamage)
        }

        companion object {
            private const val serialVersionUID = 4700387399339051513L
        }

        init {
            bitSize = NukkitMath.bitLength(bigDamage)
        }
    }

    @ParametersAreNonnullByDefault
    private inner class LongStorage(private val data: Long) : Storage {
        @Getter
        private override val bitSize: Int
        override val number: Number
            @Nonnull @Override get() = data
        override val legacyDamage: Int
            @Override get() = (data and Block.DATA_MASK).toInt()
        override val bigDamage: Int
            @Override get() = (data and BlockStateRegistry.BIG_META_MASK).toInt()
        override val signedBigDamage: Int
            @Override get() = (data and Integer.MAX_VALUE).toInt()

        @Nonnull
        @Override
        override fun getPropertyValue(properties: BlockProperties, propertyName: String?): Serializable {
            return properties.getValue(data, propertyName)
        }

        @Override
        override fun getIntValue(properties: BlockProperties, propertyName: String?): Int {
            return properties.getIntValue(data, propertyName)
        }

        @Override
        override fun getBooleanValue(properties: BlockProperties, propertyName: String?): Boolean {
            return properties.getBooleanValue(data, propertyName)
        }

        @Nonnull
        @Override
        override fun withPropertyString(blockId: Int, properties: BlockProperties, propertyName: String?, value: String?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId, properties.setPersistenceValue(data, propertyName, value))
        }

        @Nonnull
        @Override
        override fun withBlockId(blockId: Int): BlockState {
            return of(blockId, data)
        }

        @Nonnull
        @Override
        override fun withProperty(blockId: Int, properties: BlockProperties, propertyName: String?, @Nullable value: Serializable?): BlockState {
            return of(blockId, properties.setValue(data, propertyName, value))
        }

        @Nonnull
        @Override
        override fun onlyWithProperties(currentState: BlockState?, propertyNames: List<String?>): BlockState {
            return of(blockId,
                    properties.reduceLong(data
                    ) { property, offset, current -> if (propertyNames.contains(property.getName())) current else property.setValue(current, offset, null) }
            )
        }

        @Nonnull
        @Override
        @SuppressWarnings(["unchecked", "java:S1905", "rawtypes"])
        override fun onlyWithProperty(currentState: BlockState?, name: String, value: Serializable?): BlockState {
            // TODO This can cause problems when setting a property that increases the bit size
            return of(blockId,
                    properties.reduceLong(data
                    ) { property, offset, current -> (property as BlockProperty).setValue(current, offset, if (name.equals(property.getName())) value else null) }
            )
        }

        @Override
        override fun validate(properties: BlockProperties) {
            properties.forEach { property, offset -> property.validateMeta(data, offset) }
        }

        override val isDefaultState: Boolean
            @Override get() = data == 0L

        @Nonnull
        @Override
        override fun getPersistenceValue(properties: BlockProperties, propertyName: String?): String {
            return properties.getPersistenceValue(data, propertyName)
        }

        override val hugeDamage: BigInteger
            @Nonnull @Override get() = BigInteger.valueOf(data)

        @Override
        override fun toString(): String {
            return Long.toString(data)
        }

        companion object {
            private const val serialVersionUID = -2633333569914851875L
        }

        init {
            bitSize = NukkitMath.bitLength(data)
        }
    }

    @ParametersAreNonnullByDefault
    private inner class BigIntegerStorage(data: BigInteger) : Storage {
        private val data: BigInteger

        @Getter
        private override val bitSize: Int
        override val number: Number
            @Nonnull @Override get() = hugeDamage
        override val legacyDamage: Int
            @Override get() = data.and(BigInteger.valueOf(Block.DATA_MASK)).intValue()
        override val bigDamage: Int
            @Override get() = data.and(BigInteger.valueOf(BlockStateRegistry.BIG_META_MASK)).intValue()
        override val signedBigDamage: Int
            @Override get() = data.and(BigInteger.valueOf(Integer.MAX_VALUE)).intValue()

        @Nonnull
        @Override
        override fun getPropertyValue(properties: BlockProperties, propertyName: String?): Serializable {
            return properties.getValue(data, propertyName)
        }

        @Override
        override fun getIntValue(properties: BlockProperties, propertyName: String?): Int {
            return properties.getIntValue(data, propertyName)
        }

        @Override
        override fun getBooleanValue(properties: BlockProperties, propertyName: String?): Boolean {
            return properties.getBooleanValue(data, propertyName)
        }

        @Nonnull
        @Override
        override fun withBlockId(blockId: Int): BlockState {
            return of(blockId, data)
        }

        @Nonnull
        @Override
        override fun withProperty(blockId: Int, properties: BlockProperties, propertyName: String?, @Nullable value: Serializable?): BlockState {
            return of(blockId, properties.setValue(data, propertyName, value))
        }

        @Nonnull
        @Override
        override fun withPropertyString(blockId: Int, properties: BlockProperties, propertyName: String?, value: String?): BlockState {
            return of(blockId, properties.setPersistenceValue(data, propertyName, value))
        }

        @Nonnull
        @Override
        override fun onlyWithProperties(currentState: BlockState?, propertyNames: List<String?>): BlockState {
            return of(blockId,
                    properties.reduce(data
                    ) { property, offset, current -> if (propertyNames.contains(property.getName())) current else property.setValue(current, offset, null) }
            )
        }

        @Nonnull
        @Override
        @SuppressWarnings(["unchecked", "java:S1905", "rawtypes"])
        override fun onlyWithProperty(currentState: BlockState?, name: String, value: Serializable?): BlockState {
            return of(blockId,
                    properties.reduce(data
                    ) { property, offset, current -> (property as BlockProperty).setValue(current, offset, if (name.equals(property.getName())) value else null) }
            )
        }

        @Override
        override fun validate(properties: BlockProperties) {
            properties.forEach { property, offset -> property.validateMeta(data, offset) }
        }

        override val isDefaultState: Boolean
            @Override get() = data.equals(BigInteger.ZERO)

        @Nonnull
        @Override
        override fun getPersistenceValue(properties: BlockProperties, propertyName: String?): String {
            return properties.getPersistenceValue(data, propertyName)
        }

        override val hugeDamage: BigInteger
            @Nonnull @Override get() = data

        @Override
        override fun toString(): String {
            return data.toString()
        }

        companion object {
            private const val serialVersionUID = 2504213066240296662L
        }

        init {
            this.data = data
            bitSize = NukkitMath.bitLength(data)
        }
    }

    companion object {
        private const val serialVersionUID = 623759888114628578L
        private val SIXTEEN: BigInteger = BigInteger.valueOf(16)
        private val BYTE_LIMIT: BigInteger = BigInteger.valueOf(Byte.MAX_VALUE)
        private val INT_LIMIT: BigInteger = BigInteger.valueOf(Integer.MAX_VALUE)
        private val LONG_LIMIT: BigInteger = BigInteger.valueOf(Long.MAX_VALUE)
        private val ZERO_STORAGE = ZeroStorage()

        @SuppressWarnings(["deprecation", "java:S1874"])
        private val STATES_COMMON = Array(16) { arrayOfNulls<BlockState>(Block.MAX_BLOCK_ID) }
        private val STATES_UNCOMMON: ConcurrentMap<String, BlockState> = ConcurrentHashMap()

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val AIR: BlockState = of(BlockID.AIR, 0)
        private fun growCommonPool(@Nonnegative blockId: Int, @Nonnegative blockData: Byte): BlockState {
            synchronized(STATES_COMMON) {
                var blockIds = STATES_COMMON[blockData.toInt()]
                val newLen = blockId + 1
                if (blockIds.size < newLen) {
                    blockIds = Arrays.copyOf(blockIds, blockId + 1)
                    STATES_COMMON[blockData.toInt()] = blockIds
                }
                val state: BlockState = BlockState(blockId, blockData)
                blockIds[blockId] = state
                return state
            }
        }

        private fun of0xF(@Nonnegative blockId: Int, @Nonnegative blockData: Byte): BlockState {
            val blockIds = STATES_COMMON[blockData.toInt()]
            if (blockIds.size <= blockId) {
                return growCommonPool(blockId, blockData)
            }
            val state = blockIds[blockId]
            if (state != null) {
                return state
            }
            val newState: BlockState = BlockState(blockId, blockData)
            blockIds[blockId] = newState
            return newState
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun of(@Nonnegative blockId: Int): BlockState {
            return of0xF(blockId, 0.toByte())
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun of(@Nonnegative blockId: Int, @Nonnegative blockData: Byte): BlockState {
            Validation.checkPositive("blockData", blockData)
            return if (blockData < 16) {
                of0xF(blockId, blockData)
            } else STATES_UNCOMMON.computeIfAbsent("$blockId:$blockData") { k -> BlockState(blockId, blockData) }
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun of(@Nonnegative blockId: Int, @Nonnegative blockData: Int): BlockState {
            Validation.checkPositive("blockData", blockData)
            return if (blockData < 16) {
                of0xF(blockId, blockData.toByte())
            } else STATES_UNCOMMON.computeIfAbsent("$blockId:$blockData") { k -> BlockState(blockId, blockData) }
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun of(@Nonnegative blockId: Int, @Nonnegative blockData: Long): BlockState {
            Validation.checkPositive("blockData", blockData)
            return if (blockData < 16) {
                of0xF(blockId, blockData.toByte())
            } else STATES_UNCOMMON.computeIfAbsent("$blockId:$blockData") { k -> BlockState(blockId, blockData) }
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun of(@Nonnegative blockId: Int, @Nonnegative blockData: BigInteger): BlockState {
            Validation.checkPositive("blockData", blockData)
            return if (blockData.compareTo(SIXTEEN) < 0) {
                of0xF(blockId, blockData.byteValue())
            } else STATES_UNCOMMON.computeIfAbsent("$blockId:$blockData") { k -> BlockState(blockId, blockData) }
        }

        /**
         * @throws InvalidBlockStateDataTypeException If the `blockData` param is not [Integer], [Long],
         * or [BigInteger].
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun of(@Nonnegative blockId: Int, @Nonnegative blockData: Number): BlockState {
            val c: Class<out Number?> = blockData.getClass()
            return if (c === Byte::class.java) {
                of(blockId, blockData.byteValue())
            } else if (c === Integer::class.java) {
                of(blockId, blockData.intValue())
            } else if (c === Long::class.java) {
                of(blockId, blockData.longValue())
            } else if (c === BigInteger::class.java) {
                of(blockId, blockData as BigInteger)
            } else {
                throw InvalidBlockStateDataTypeException(blockData)
            }
        }

        private fun compareDataEquality(a: Number, b: Number): Boolean {
            val aClass: Class<out Number?> = a.getClass()
            val bClass: Class<out Number?> = b.getClass()
            if (aClass === bClass) {
                return a.equals(b)
            }
            if (aClass !== BigInteger::class.java && bClass !== BigInteger::class.java) {
                return a.longValue() === b.longValue()
            }
            val aBig: BigInteger = if (aClass === BigInteger::class.java) a as BigInteger else BigInteger(a.toString())
            val bBig: BigInteger = if (bClass === BigInteger::class.java) b as BigInteger else BigInteger(b.toString())
            return aBig.equals(bBig)
        }
    }
}