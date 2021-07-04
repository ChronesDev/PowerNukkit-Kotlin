package cn.nukkit.blockstate

import cn.nukkit.api.DeprecationDetails

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ParametersAreNonnullByDefault
class LongMutableBlockState @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(blockId: Int, properties: BlockProperties, private var storage: Long) : MutableBlockState(blockId, properties) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(blockId: Int, properties: BlockProperties) : this(blockId, properties, 0) {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setDataStorage(@Nonnegative storage: Number) {
        val c: Class<out Number?> = storage.getClass()
        val state: Long
        if (c === Long::class.java || c === Integer::class.java || c === Short::class.java || c === Byte::class.java) {
            state = storage.longValue()
        } else {
            state = try {
                BigDecimal(storage.toString()).longValueExact()
            } catch (e: ArithmeticException) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e)
            } catch (e: NumberFormatException) {
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
        val state = storage.toLong()
        validate(state)
        this.storage = state
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setDataStorageWithoutValidation(storage: Number) {
        this.storage = storage.longValue()
    }

    @Override
    override fun validate() {
        validate(storage)
    }

    private fun validate(state: Long) {
        if (state == 0L) {
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
        return (storage and Block.DATA_MASK).toInt()
    }

    @Unsigned
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    fun getBigDamage(): Int {
        return (storage and BlockStateRegistry.BIG_META_MASK).toInt()
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Deprecated
    @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "getDataStorage()")
    @Override
    fun getSignedBigDamage(): Int {
        return (storage and Integer.MAX_VALUE).toInt()
    }

    @Nonnegative
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    fun getHugeDamage(): BigInteger {
        return BigInteger.valueOf(storage)
    }

    @Nonnegative
    @Nonnull
    @Override
    fun getDataStorage(): Number {
        return storage
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun isDefaultState(): Boolean {
        return storage == 0L
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
        storage = properties.setBooleanValue(storage, propertyName, value)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setIntValue(propertyName: String?, value: Int) {
        storage = properties.setIntValue(storage, propertyName, value)
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
        val bits: Int = getBitSize()
        if (bits > 32) {
            throw ArithmeticException("$storage can't be stored in an 32 bits integer. It has $bits bits")
        }
        return storage.toInt()
    }

    @Nonnull
    @Override
    override fun copy(): LongMutableBlockState {
        return LongMutableBlockState(getBlockId(), properties, storage)
    }
}