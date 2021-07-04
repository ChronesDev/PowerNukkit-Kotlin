package cn.nukkit.blockstate

import cn.nukkit.api.*

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
class BigIntegerMutableBlockState @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(blockId: Int, properties: BlockProperties, state: BigInteger) : MutableBlockState(blockId, properties) {
    private var storage: BigInteger

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(blockId: Int, properties: BlockProperties) : this(blockId, properties, BigInteger.ZERO) {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setDataStorage(@Nonnegative storage: Number) {
        val state: BigInteger
        state = if (storage is BigInteger) {
            storage as BigInteger
        } else if (LONG_COMPATIBLE_CLASSES.contains(storage.getClass())) {
            BigInteger.valueOf(storage.longValue())
        } else {
            try {
                BigDecimal(storage.toString()).toBigIntegerExact()
            } catch (e: NumberFormatException) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e)
            } catch (e: ArithmeticException) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e)
            }
        }
        validate(state)
        this.storage = state
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setDataStorageFromInt(@Nonnegative storage: Int) {
        val state: BigInteger = BigInteger.valueOf(storage)
        validate(state)
        this.storage = state
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    @API(definition = INTERNAL, usage = INCUBATING)
    fun setDataStorageWithoutValidation(storage: Number) {
        if (storage is BigInteger) {
            this.storage = storage as BigInteger
        } else {
            this.storage = BigInteger(storage.toString())
        }
    }

    @Override
    override fun validate() {
        validate(storage)
    }

    private fun validate(state: BigInteger) {
        if (BigInteger.ZERO.equals(state)) {
            return
        }
        Validation.checkPositive("state", state)
        val properties: BlockProperties = this.properties
        val bitLength: Int = NukkitMath.bitLength(state)
        if (bitLength > properties.getBitSize()) {
            throw InvalidBlockStateException(
                    BlockState.of(getBlockId(), state),
                    "The state have more data bits than specified in the properties. Bits: " + bitLength + ", Max: " + properties.getBitSize()
            )
        }
        try {
            for (name in properties.getNames()) {
                val property: BlockProperty<*> = properties.getBlockProperty(name)
                property.validateMeta(state, properties.getOffset(name))
            }
        } catch (e: InvalidBlockPropertyException) {
            throw InvalidBlockStateException(BlockState.of(getBlockId(), state), e)
        }
    }

    @Nonnegative
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    fun getLegacyDamage(): Int {
        return storage.and(BigInteger.valueOf(Block.DATA_MASK)).intValue()
    }

    @Unsigned
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    fun getBigDamage(): Int {
        return storage.and(BigInteger.valueOf(BlockStateRegistry.BIG_META_MASK)).intValue()
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    fun getSignedBigDamage(): Int {
        return storage.and(BigInteger.valueOf(Integer.MAX_VALUE)).intValue()
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    fun getHugeDamage(): BigInteger {
        return storage
    }

    @Nonnegative
    @Nonnull
    @Override
    fun getDataStorage(): Number {
        return getHugeDamage()
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun isDefaultState(): Boolean {
        return storage.equals(BigInteger.ONE)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setPropertyValue(propertyName: String?, @Nullable value: Serializable?) {
        storage = properties.setValue(storage, propertyName, value)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setBooleanValue(propertyName: String?, value: Boolean) {
        storage = properties.setValue(storage, propertyName, value)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setIntValue(propertyName: String?, value: Int) {
        storage = properties.setValue(storage, propertyName, value)
    }

    @Nonnull
    @Override
    override fun getPropertyValue(propertyName: String?): Serializable {
        return properties.getValue(storage, propertyName)
    }

    @Override
    override fun getIntValue(propertyName: String?): Int {
        return properties.getIntValue(storage, propertyName)
    }

    @Override
    override fun getBooleanValue(propertyName: String?): Boolean {
        return properties.getBooleanValue(storage, propertyName)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @Nonnull
    @Override
    override fun getPersistenceValue(propertyName: String?): String {
        return properties.getPersistenceValue(storage, propertyName)
    }

    @Nonnull
    @Override
    fun getCurrentState(): BlockState {
        return BlockState.of(blockId, storage)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getExactIntStorage(): Int {
        return storage.intValueExact()
    }

    @Nonnull
    @Override
    override fun copy(): BigIntegerMutableBlockState {
        return BigIntegerMutableBlockState(getBlockId(), properties, storage)
    }

    companion object {
        private val LONG_COMPATIBLE_CLASSES: Set<Class<*>> = HashSet(Arrays.asList(
                Long::class.java, Integer::class.java, Short::class.java, Byte::class.java))
    }

    init {
        storage = state
    }
}