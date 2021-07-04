package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class IntBlockProperty @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(name: String, exportedToItem: Boolean, maxValue: Int, minValue: Int, bitSize: Int, persistenceName: String) : BlockProperty<Integer?>(name, exportedToItem, bitSize, persistenceName) {
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val defaultIntValue: Int
        @Since("1.4.0.0-PN") @PowerNukkitOnly @Override get() = field
    val maxValue: Int
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
    override fun copy(): IntBlockProperty {
        return IntBlockProperty(getName(), isExportedToItem(), maxValue, defaultIntValue, getBitSize(), getPersistenceName())
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun exportingToItems(exportedToItem: Boolean): IntBlockProperty {
        return IntBlockProperty(getName(), exportedToItem, maxValue, defaultIntValue, getBitSize(), getPersistenceName())
    }

    @Override
    override fun getMetaForValue(@Nullable value: Integer?): Int {
        return if (value == null) {
            0
        } else getMetaForValue(value.intValue())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    override fun getMetaForValue(value: Int): Int {
        try {
            validateDirectly(value)
        } catch (e: IllegalArgumentException) {
            throw InvalidBlockPropertyValueException(this, null, value, e)
        }
        return value - defaultIntValue
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
        return defaultIntValue + meta
    }

    @Override
    override fun getPersistenceValueForMeta(meta: Int): String {
        return String.valueOf(getIntValueForMeta(meta))
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getMetaForPersistenceValue(@Nonnull persistenceValue: String?): Int {
        return try {
            getMetaForValue(Integer.parseInt(persistenceValue))
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
        validateDirectly(value.intValue())
    }

    private override fun validateDirectly(newValue: Int) {
        Preconditions.checkArgument(newValue >= defaultIntValue, "New value (%s) must be higher or equals to %s", newValue, defaultIntValue)
        Preconditions.checkArgument(maxValue >= newValue, "New value (%s) must be less or equals to %s", newValue, maxValue)
    }

    @Override
    protected override fun validateMetaDirectly(meta: Int) {
        val max = maxValue - defaultIntValue
        Preconditions.checkArgument(0 <= meta && meta <= max, "The meta %s is outside the range of 0 .. ", meta, max)
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    fun clamp(value: Int): Int {
        return NukkitMath.clamp(value, defaultIntValue, maxValue)
    }

    override val defaultValue: T
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() = defaultIntValue

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isDefaultIntValue(value: Int): Boolean {
        return defaultIntValue == value
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isDefaultValue(@Nullable value: Integer?): Boolean {
        return value == null || defaultIntValue == value
    }

    override val valueClass: Class<Integer>
        @Nonnull @Override get() = Integer::class.java

    companion object {
        private const val serialVersionUID = -2239010977496415152L
    }

    init {
        val delta = maxValue - minValue
        Preconditions.checkArgument(delta > 0, "maxValue must be higher than minValue. Got min:%s and max:%s", minValue, maxValue)
        val mask = -1 ushr 32 - bitSize
        Preconditions.checkArgument(delta <= mask, "The data range from %s to %s can't be stored in %s bits", minValue, maxValue, bitSize)
        defaultIntValue = minValue
        this.maxValue = maxValue
    }
}