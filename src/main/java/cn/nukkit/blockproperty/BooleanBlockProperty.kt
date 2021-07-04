package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BooleanBlockProperty : BlockProperty<Boolean?> {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, persistenceName: String) : super(name, exportedToItem, 1, persistenceName) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean) : super(name, exportedToItem, 1, name) {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun copy(): BooleanBlockProperty {
        return BooleanBlockProperty(getName(), isExportedToItem(), getPersistenceName())
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun exportingToItems(exportedToItem: Boolean): BooleanBlockProperty {
        return BooleanBlockProperty(getName(), exportedToItem, getPersistenceName())
    }

    @Override
    override fun setValue(currentMeta: Int, bitOffset: Int, @Nullable newValue: Boolean?): Int {
        val value = newValue != null && newValue
        return setValue(currentMeta, bitOffset, value)
    }

    @Override
    override fun setValue(currentBigMeta: Long, bitOffset: Int, @Nullable newValue: Boolean?): Long {
        val value = newValue != null && newValue
        return setValue(currentBigMeta, bitOffset, value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setValue(currentMeta: Int, bitOffset: Int, newValue: Boolean): Int {
        val mask = 1 shl bitOffset
        return if (newValue) currentMeta or mask else currentMeta and mask.inv()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setValue(currentMeta: Long, bitOffset: Int, newValue: Boolean): Long {
        val mask = 1L shl bitOffset
        return if (newValue) currentMeta or mask else currentMeta and mask.inv()
    }

    @Nonnull
    @Override
    override fun getValue(currentMeta: Int, bitOffset: Int): Boolean {
        return getBooleanValue(currentMeta, bitOffset)
    }

    @Nonnull
    @Override
    override fun getValue(currentBigMeta: Long, bitOffset: Int): Boolean {
        return getBooleanValue(currentBigMeta, bitOffset)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(currentMeta: Int, bitOffset: Int): Boolean {
        val mask = 1 shl bitOffset
        return currentMeta and mask == mask
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(currentBigMeta: Long, bitOffset: Int): Boolean {
        val mask = 1L shl bitOffset
        return currentBigMeta and mask == mask
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(currentHugeData: BigInteger, bitOffset: Int): Boolean {
        val mask: BigInteger = BigInteger.ONE.shiftLeft(bitOffset)
        return mask.equals(currentHugeData.and(mask))
    }

    @Override
    override fun getIntValue(currentMeta: Int, bitOffset: Int): Int {
        return if (getBooleanValue(currentMeta, bitOffset)) 1 else 0
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @Override
    override fun getIntValueForMeta(meta: Int): Int {
        if (meta == 1 || meta == 0) {
            return meta
        }
        throw InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected")
    }

    @Override
    override fun getMetaForValue(@Nullable value: Boolean?): Int {
        return if (Boolean.TRUE.equals(value)) 1 else 0
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @Nonnull
    @Override
    override fun getValueForMeta(meta: Int): Boolean {
        return getBooleanValueForMeta(meta)
    }

    /**
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValueForMeta(meta: Int): Boolean {
        return if (meta == 0) {
            false
        } else if (meta == 1) {
            true
        } else {
            throw InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected")
        }
    }

    override val defaultValue: T
        @PowerNukkitOnly @Since("1.4.0.0-PN") @Nonnull get() = Boolean.FALSE

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isDefaultValue(@Nullable value: Boolean?): Boolean {
        return value == null || Boolean.FALSE.equals(value)
    }

    @Override
    protected override fun validateMetaDirectly(meta: Int) {
        Preconditions.checkArgument(meta == 1 || meta == 0, "Must be 1 or 0")
    }

    override val valueClass: Class<Boolean>
        @Nonnull @Override get() = Boolean::class.java

    @Override
    override fun getPersistenceValueForMeta(meta: Int): String {
        return if (meta == 1) {
            "1"
        } else if (meta == 0) {
            "0"
        } else {
            throw InvalidBlockPropertyMetaException(this, meta, meta, "Only 1 or 0 was expected")
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getMetaForPersistenceValue(@Nonnull persistenceValue: String?): Int {
        return if ("1".equals(persistenceValue)) {
            1
        } else if ("0".equals(persistenceValue)) {
            0
        } else {
            throw InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue, "Only 1 or 0 was expected")
        }
    }

    companion object {
        private const val serialVersionUID = 8249827149092664486L
    }
}