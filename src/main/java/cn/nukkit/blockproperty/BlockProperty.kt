package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
abstract class BlockProperty<T : Serializable?> @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(name: String, exportedToItem: Boolean, bitSize: Int, persistenceName: String) : Serializable {
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val bitSize: Int

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val name: String

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val persistenceName: String

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isExportedToItem: Boolean
    private fun computeRightMask(bitOffset: Int): Int {
        return if (bitOffset == 0) 0 else -1 ushr 32 - bitOffset
    }

    private fun computeBigRightMask(bitOffset: Int): Long {
        return if (bitOffset.toLong() == 0L) 0L else -1L ushr 64 - bitOffset
    }

    private fun computeHugeRightMask(bitOffset: Int): BigInteger {
        return BigInteger.ONE.shiftLeft(bitOffset).subtract(BigInteger.ONE)
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    private fun computeValueMask(bitOffset: Int): Int {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset)
        val maskBits = bitSize + bitOffset
        Preconditions.checkArgument(0 < maskBits && maskBits <= 32, "The bit offset %s plus the bit size %s causes memory overflow (32 bits)", bitOffset, bitSize)
        val rightMask = computeRightMask(bitOffset)
        val leftMask = -1 shl maskBits
        return rightMask.inv() and leftMask.inv()
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    private fun computeBigValueMask(bitOffset: Int): Long {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset)
        val maskBits = bitSize + bitOffset
        Preconditions.checkArgument(0 < maskBits && maskBits <= 64, "The bit offset %s plus the bit size %s causes memory overflow (64 bits)", bitOffset, bitSize)
        val rightMask = computeBigRightMask(bitOffset)
        val leftMask = -1L shl maskBits
        return rightMask.inv() and leftMask.inv()
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    private fun computeHugeValueMask(bitOffset: Int): BigInteger {
        Preconditions.checkArgument(bitOffset >= 0, "Bit offset can not be negative. Got %s", bitOffset)
        val maskBits = bitSize + bitOffset
        Preconditions.checkArgument(0 < maskBits, "The bit offset %s plus the bit size %s causes memory overflow (huge)", bitOffset, bitSize)
        val rightMask: BigInteger = computeHugeRightMask(bitOffset)
        val leftMask: BigInteger = BigInteger.valueOf(-1).shiftLeft(maskBits)
        return rightMask.not().andNot(leftMask)
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by this property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setValue(currentMeta: Int, bitOffset: Int, @Nullable newValue: T): Int {
        val mask = computeValueMask(bitOffset)
        return try {
            val value = getMetaForValue(newValue) shl bitOffset
            if (value and mask.inv() != 0) {
                throw IllegalStateException("Attempted to set a value which overflows the size of $bitSize bits. Current:$currentMeta, offset:$bitOffset, meta:$value, value:$newValue")
            }
            currentMeta and mask.inv() or (value and mask)
        } catch (e: Exception) {
            var oldValue: T? = null
            var suppressed: InvalidBlockPropertyMetaException? = null
            try {
                oldValue = getValue(currentMeta, bitOffset)
            } catch (e2: Exception) {
                suppressed = InvalidBlockPropertyMetaException(this, currentMeta, currentMeta and mask, e2)
            }
            val toThrow = InvalidBlockPropertyValueException(this, oldValue, newValue, e)
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed)
            }
            throw toThrow
        }
    }

    /**
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by this property
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setValue(currentBigMeta: Long, bitOffset: Int, @Nullable newValue: T): Long {
        val mask = computeBigValueMask(bitOffset)
        return try {
            val value = (getMetaForValue(newValue) shl bitOffset).toLong()
            if (value and mask.inv() != 0L) {
                throw IllegalStateException("Attempted to set a value which overflows the size of $bitSize bits. Current:$currentBigMeta, offset:$bitOffset, meta:$value, value:$newValue")
            }
            currentBigMeta and mask.inv() or (value and mask)
        } catch (e: Exception) {
            var oldValue: T? = null
            var suppressed: InvalidBlockPropertyMetaException? = null
            try {
                oldValue = getValue(currentBigMeta, bitOffset)
            } catch (e2: Exception) {
                suppressed = InvalidBlockPropertyMetaException(this, currentBigMeta, currentBigMeta and mask, e2)
            }
            val toThrow = InvalidBlockPropertyValueException(this, oldValue, newValue, e)
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed)
            }
            throw toThrow
        }
    }

    /**
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by this property
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setValue(currentHugeMeta: BigInteger, bitOffset: Int, @Nullable newValue: T): BigInteger {
        val mask: BigInteger = computeHugeValueMask(bitOffset)
        return try {
            val value: BigInteger = BigInteger.valueOf(getMetaForValue(newValue)).shiftLeft(bitOffset)
            if (!value.andNot(mask).equals(BigInteger.ZERO)) {
                throw IllegalStateException("Attempted to set a value which overflows the size of $bitSize bits. Current:$currentHugeMeta, offset:$bitOffset, meta:$value, value:$newValue")
            }
            currentHugeMeta.andNot(mask).or(value.and(mask))
        } catch (e: Exception) {
            var oldValue: T? = null
            var suppressed: InvalidBlockPropertyMetaException? = null
            try {
                oldValue = getValue(currentHugeMeta, bitOffset)
            } catch (e2: Exception) {
                suppressed = InvalidBlockPropertyMetaException(this, currentHugeMeta, currentHugeMeta.and(mask), e2)
            }
            val toThrow = InvalidBlockPropertyValueException(this, oldValue, newValue, e)
            if (suppressed != null) {
                toThrow.addSuppressed(suppressed)
            }
            throw toThrow
        }
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getMetaFromInt(currentMeta: Int, bitOffset: Int): Int {
        return currentMeta and computeValueMask(bitOffset) ushr bitOffset
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getMetaFromLong(currentMeta: Long, bitOffset: Int): Int {
        return (currentMeta and computeBigValueMask(bitOffset) ushr bitOffset).toInt()
    }

    /**
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getMetaFromBigInt(currentMeta: BigInteger, bitOffset: Int): Int {
        return currentMeta.and(computeHugeValueMask(bitOffset)).shiftRight(bitOffset).intValue()
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getValue(currentMeta: Int, bitOffset: Int): T {
        val meta = getMetaFromInt(currentMeta, bitOffset)
        return try {
            getValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta and computeValueMask(bitOffset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getValue(currentBigMeta: Long, bitOffset: Int): T {
        val meta = getMetaFromLong(currentBigMeta, bitOffset)
        return try {
            getValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentBigMeta, currentBigMeta and computeBigValueMask(bitOffset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getValue(currentHugeMeta: BigInteger, bitOffset: Int): T {
        val meta = getMetaFromBigInt(currentHugeMeta, bitOffset)
        return try {
            getValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentHugeMeta, currentHugeMeta.and(computeHugeValueMask(bitOffset)), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(currentMeta: Int, bitOffset: Int): Int {
        val meta = getMetaFromInt(currentMeta, bitOffset)
        return try {
            getIntValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta and computeValueMask(bitOffset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(currentMeta: Long, bitOffset: Int): Int {
        val meta = getMetaFromLong(currentMeta, bitOffset)
        return try {
            getIntValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta and computeBigValueMask(bitOffset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(currentMeta: BigInteger, bitOffset: Int): Int {
        val meta = getMetaFromBigInt(currentMeta, bitOffset)
        return try {
            getIntValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta.and(computeHugeValueMask(bitOffset)), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPersistenceValue(currentMeta: Int, bitOffset: Int): String {
        val meta = getMetaFromInt(currentMeta, bitOffset)
        return try {
            getPersistenceValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta and computeValueMask(bitOffset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPersistenceValue(currentMeta: Long, bitOffset: Int): String {
        val meta = getMetaFromLong(currentMeta, bitOffset)
        return try {
            getPersistenceValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta and computeBigValueMask(bitOffset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getPersistenceValue(currentMeta: BigInteger, bitOffset: Int): String {
        val meta = getMetaFromBigInt(currentMeta, bitOffset)
        return try {
            getPersistenceValueForMeta(meta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, currentMeta, currentMeta.and(computeHugeValueMask(bitOffset)), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyValueException If the value is invalid for this property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun getMetaForValue(@Nullable value: T): Int

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    abstract fun getValueForMeta(meta: Int): T

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun getIntValueForMeta(meta: Int): Int

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun getPersistenceValueForMeta(meta: Int): String

    /**
     * @throws InvalidBlockPropertyPersistenceValueException IF the persistence value is not valid for this property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun getMetaForPersistenceValue(persistenceValue: String?): Int

    /**
     * @throws RuntimeException Any runtime exception to indicate an invalid value
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun validateDirectly(@Nullable value: T) {
        // Does nothing by default
    }

    /**
     * @throws RuntimeException Any runtime exception to indicate an invalid meta
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected abstract fun validateMetaDirectly(meta: Int)

    /**
     * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun validateMeta(meta: Int, offset: Int) {
        val propMeta = getMetaFromInt(meta, offset)
        try {
            validateMetaDirectly(propMeta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, meta, meta and computeValueMask(offset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun validateMeta(meta: Long, offset: Int) {
        val propMeta = getMetaFromLong(meta, offset)
        try {
            validateMetaDirectly(propMeta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, meta, meta and computeBigValueMask(offset), e)
        }
    }

    /**
     * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
     * @throws IllegalArgumentException If the offset is negative or would cause memory overflow
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun validateMeta(meta: BigInteger, offset: Int) {
        val propMeta = getMetaFromBigInt(meta, offset)
        try {
            validateMetaDirectly(propMeta)
        } catch (e: Exception) {
            throw InvalidBlockPropertyMetaException(this, meta, meta.and(computeHugeRightMask(offset)), e)
        }
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val valueClass: Class<T>?

    @Override
    override fun toString(): String {
        return getClass().getSimpleName().toString() + "{" +
                "name='" + name + '\'' +
                ", bitSize=" + bitSize +
                ", exportedToItem=" + isExportedToItem +
                ", persistenceName='" + persistenceName + '\'' +
                '}'
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun isDefaultValue(@Nullable value: T): Boolean

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val defaultValue: T
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isDefaultIntValue(value: Int): Boolean {
        return value == defaultIntValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isDefaultBooleanValue(value: Boolean): Boolean {
        return value == defaultBooleanValue
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val defaultIntValue: Int
        get() = 0

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val defaultBooleanValue: Boolean
        get() = false

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun copy(): BlockProperty<T>?
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun exportingToItems(exportedToItem: Boolean): BlockProperty<T>?

    companion object {
        private const val serialVersionUID = -2594821043880025191L
    }

    /**
     * @throws IllegalArgumentException If the bit size is negative
     */
    init {
        Preconditions.checkArgument(bitSize > 0, "Bit size (%s) must be positive", bitSize)
        this.bitSize = bitSize
        this.name = name
        this.persistenceName = persistenceName
        isExportedToItem = exportedToItem
    }
}