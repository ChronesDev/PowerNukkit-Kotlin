package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class UnsignedIntBlockProperty @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(name: String, exportedToItem: Boolean, maxValue: Int, minValue: Int, bitSize: Int, persistenceName: String) : BlockProperty<Integer?>(name, exportedToItem, bitSize, persistenceName) {
    val minValue: Long
        @PowerNukkitOnly @Since("1.4.0.0-PN") get
    val maxValue: Long
        @PowerNukkitOnly @Since("1.4.0.0-PN") get

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, maxValue: Int, minValue: Int, bitSize: Int) : this(name, exportedToItem, maxValue, minValue, bitSize, name) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, maxValue: Int, minValue: Int) : this(name, exportedToItem, maxValue, minValue, NukkitMath.bitLength(maxValue - minValue)) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, maxValue: Int) : this(name, exportedToItem, maxValue, 0) {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun copy(): UnsignedIntBlockProperty {
        return UnsignedIntBlockProperty(getName(), isExportedToItem(), maxValue.toInt(), minValue.toInt(), getBitSize(), getPersistenceName())
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun exportingToItems(exportedToItem: Boolean): UnsignedIntBlockProperty {
        return UnsignedIntBlockProperty(getName(), exportedToItem, maxValue.toInt(), minValue.toInt(), getBitSize(), getPersistenceName())
    }

    @Override
    override fun getMetaForValue(@Nullable value: Integer?): Int {
        if (value == null) {
            return 0
        }
        val unsigned = removeSign(value)
        try {
            validateDirectly(unsigned)
        } catch (e: IllegalArgumentException) {
            throw InvalidBlockPropertyValueException(this, null, value, e)
        }
        return (unsigned - minValue).toInt()
    }

    @Nonnull
    @Override
    override fun getValueForMeta(meta: Int): Integer {
        return getIntValueForMeta(meta)
    }

    @Override
    override fun getIntValueForMeta(meta: Int): Int {
        try {
            validateMetaDirectly(meta)
        } catch (e: IllegalArgumentException) {
            throw InvalidBlockPropertyMetaException(this, meta, meta, e)
        }
        return (minValue + meta).toInt()
    }

    @Override
    override fun getPersistenceValueForMeta(meta: Int): String {
        return String.valueOf(removeSign(getIntValueForMeta(meta)))
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getMetaForPersistenceValue(@Nonnull persistenceValue: String?): Int {
        return try {
            getMetaForValue(addSign(Long.parseLong(persistenceValue)))
        } catch (e: NumberFormatException) {
            throw InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, e)
        } catch (e: InvalidBlockPropertyValueException) {
            throw InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, e)
        }
    }

    @Override
    protected override fun validateDirectly(@Nullable value: Integer?) {
        if (value == null) {
            return
        }
        validateDirectly(removeSign(value))
    }

    /**
     * @throws RuntimeException Any runtime exception to indicate an invalid value
     */
    private override fun validateDirectly(unsigned: Long) {
        Preconditions.checkArgument(unsigned >= minValue, "New value (%s) must be higher or equals to %s", unsigned, minValue)
        Preconditions.checkArgument(maxValue >= unsigned, "New value (%s) must be less or equals to %s", unsigned, maxValue)
    }

    @Override
    protected override fun validateMetaDirectly(meta: Int) {
        val max = maxValue - minValue
        Preconditions.checkArgument(0 <= meta && meta <= max, "The meta %s is outside the range of 0 .. ", meta, max)
    }

    override val valueClass: Class<Integer>
        @Nonnull @Override get() = Integer::class.java
    override val defaultValue: T
        @Nonnull @PowerNukkitOnly @Since("1.4.0.0-PN") get() = minValue.toInt()

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isDefaultValue(@Nullable value: Integer?): Boolean {
        return value == null || removeSign(value) == minValue
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isDefaultIntValue(value: Int): Boolean {
        return removeSign(value) == minValue
    }

    override val defaultIntValue: Int
        @Since("1.4.0.0-PN") @PowerNukkitOnly @Override get() = minValue.toInt()

    companion object {
        private const val serialVersionUID = 7896101036099245755L
        private fun removeSign(value: Int): Long {
            return value.toLong() and 0xFFFFFFFFL
        }

        private fun addSign(value: Long): Int {
            return (value and 0xFFFFFFFFL).toInt()
        }
    }

    init {
        val unsignedMinValue = removeSign(minValue)
        val unsignedMaxValue = removeSign(maxValue)
        val delta = unsignedMaxValue - unsignedMinValue
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", unsignedMinValue, unsignedMaxValue)
        val mask = removeSign(-1 ushr 32 - bitSize)
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", unsignedMinValue, unsignedMaxValue, bitSize)
        this.minValue = unsignedMinValue
        this.maxValue = unsignedMaxValue
    }
}