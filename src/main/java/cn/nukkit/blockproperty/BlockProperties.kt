package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0")
@ParametersAreNonnullByDefault
class BlockProperties @PowerNukkitOnly @Since("1.4.0.0") constructor(@Nullable itemBlockProperties: BlockProperties?, vararg properties: BlockProperty<*>?) {
    private val byName: Map<String, RegisteredBlockProperty>

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val bitSize: Int

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val itemBlockProperties: BlockProperties? = null

    /**
     * @throws IllegalArgumentException If there are validation failures
     */
    @PowerNukkitOnly
    @Since("1.4.0.0")
    constructor(vararg properties: BlockProperty<*>?) : this(null, *properties) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun createMutableState(blockId: Int): MutableBlockState {
        return if (bitSize == 0) {
            ZeroMutableBlockState(blockId, this)
        } else if (bitSize < 8) {
            ByteMutableBlockState(blockId, this)
        } else if (bitSize < 32) {
            IntMutableBlockState(blockId, this)
        } else if (bitSize < 64) {
            LongMutableBlockState(blockId, this)
        } else {
            BigIntegerMutableBlockState(blockId, this)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    operator fun contains(propertyName: String): Boolean {
        return byName.containsKey(propertyName)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    operator fun contains(property: BlockProperty<*>): Boolean {
        val registry = byName[property.getName()] ?: return false
        return registry.getProperty().getValueClass().equals(property.getValueClass())
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("java:S1452")
    fun getBlockProperty(propertyName: String): BlockProperty<*>? {
        return requireRegisteredProperty(propertyName).property
    }

    /**
     *
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <T : BlockProperty<*>?> getBlockProperty(propertyName: String, tClass: Class<T>): T {
        return tClass.cast(requireRegisteredProperty(propertyName).property)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getOffset(propertyName: String): Int {
        return requireRegisteredProperty(propertyName).offset
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val names: Set<String>
        get() = byName.keySet()

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val allProperties: Collection<RegisteredBlockProperty>
        get() = byName.values()

    /**
     * @throws NoSuchElementException If the property is not registered
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun requireRegisteredProperty(propertyName: String): RegisteredBlockProperty {
        return byName[propertyName] ?: throw BlockPropertyNotFoundException(propertyName, this)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setValue(currentMeta: Int, propertyName: String, @Nullable value: Serializable?): Int {
        val registry = requireRegisteredProperty(propertyName)
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setValue(currentMeta: Long, propertyName: String, @Nullable value: Serializable?): Long {
        val registry = requireRegisteredProperty(propertyName)
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setBooleanValue(currentMeta: Int, propertyName: String, value: Boolean): Int {
        val registry = requireRegisteredProperty(propertyName)
        val property: BlockProperty<*>? = registry.property
        if (BooleanBlockProperty::class.java === property.getClass()) {
            return (property as BooleanBlockProperty?)!!.setValue(currentMeta, registry.offset, value)
        }
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setBooleanValue(currentMeta: Long, propertyName: String, value: Boolean): Long {
        val registry = requireRegisteredProperty(propertyName)
        val property: BlockProperty<*>? = registry.property
        if (BooleanBlockProperty::class.java === property.getClass()) {
            return (property as BooleanBlockProperty?)!!.setValue(currentMeta, registry.offset, value)
        }
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setBooleanValue(currentMeta: BigInteger, propertyName: String, value: Boolean): BigInteger {
        val registry = requireRegisteredProperty(propertyName)
        val property: BlockProperty<*>? = registry.property
        if (BooleanBlockProperty::class.java === property.getClass()) {
            return (property as BooleanBlockProperty?)!!.setValue(currentMeta, registry.offset, value)
        }
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setIntValue(currentMeta: Int, propertyName: String, value: Int): Int {
        val registry = requireRegisteredProperty(propertyName)
        val property: BlockProperty<*>? = registry.property
        if (IntBlockProperty::class.java === property.getClass()) {
            return (property as IntBlockProperty?).setValue(currentMeta, registry.offset, value)
        } else if (UnsignedIntBlockProperty::class.java === property.getClass()) {
            return (property as UnsignedIntBlockProperty?).setValue(currentMeta, registry.offset, value)
        }
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setIntValue(currentMeta: Long, propertyName: String, value: Int): Long {
        val registry = requireRegisteredProperty(propertyName)
        val property: BlockProperty<*>? = registry.property
        if (IntBlockProperty::class.java === property.getClass()) {
            return (property as IntBlockProperty?).setValue(currentMeta, registry.offset, value)
        } else if (UnsignedIntBlockProperty::class.java === property.getClass()) {
            return (property as UnsignedIntBlockProperty?).setValue(currentMeta, registry.offset, value)
        }
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    fun setIntValue(currentMeta: BigInteger?, propertyName: String, value: Int): BigInteger {
        val registry = requireRegisteredProperty(propertyName)
        val property: BlockProperty<*>? = registry.property
        if (IntBlockProperty::class.java === property.getClass()) {
            return (property as IntBlockProperty?).setValue(currentMeta, registry.offset, value)
        } else if (UnsignedIntBlockProperty::class.java === property.getClass()) {
            return (property as UnsignedIntBlockProperty?).setValue(currentMeta, registry.offset, value)
        }
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    @SuppressWarnings("unchecked")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setPersistenceValue(currentMeta: Int, propertyName: String, persistenceValue: String?): Int {
        val registry = requireRegisteredProperty(propertyName)
        @SuppressWarnings("rawtypes") val property: BlockProperty? = registry.property
        val meta: Int = property.getMetaForPersistenceValue(persistenceValue)
        val value: Serializable = property.getValueForMeta(meta)
        return property.setValue(currentMeta, registry.offset, value)
    }

    @SuppressWarnings("unchecked")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setPersistenceValue(currentMeta: Long, propertyName: String, persistenceValue: String?): Long {
        val registry = requireRegisteredProperty(propertyName)
        @SuppressWarnings("rawtypes") val property: BlockProperty? = registry.property
        val meta: Int = property.getMetaForPersistenceValue(persistenceValue)
        val value: Serializable = property.getValueForMeta(meta)
        return property.setValue(currentMeta, registry.offset, value)
    }

    @SuppressWarnings("unchecked")
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setPersistenceValue(currentMeta: BigInteger?, propertyName: String, persistenceValue: String?): BigInteger {
        val registry = requireRegisteredProperty(propertyName)
        @SuppressWarnings("rawtypes") val property: BlockProperty? = registry.property
        val meta: Int = property.getMetaForPersistenceValue(persistenceValue)
        val value: Serializable = property.getValueForMeta(meta)
        return property.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyValueException If the new value is not accepted by the property
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings("unchecked")
    @Nonnull
    fun setValue(currentMeta: BigInteger?, propertyName: String, @Nullable value: Serializable?): BigInteger {
        val registry = requireRegisteredProperty(propertyName)
        @SuppressWarnings(["rawtypes", "java:S3740"]) val unchecked: BlockProperty? = registry.property
        return unchecked.setValue(currentMeta, registry.offset, value)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getValue(currentMeta: Int, propertyName: String): Serializable {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getValue(currentMeta: Long, propertyName: String): Serializable {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getValue(currentMeta: BigInteger?, propertyName: String): Serializable {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property value is not assignable to the given class
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <T> getCheckedValue(currentMeta: Int, propertyName: String, tClass: Class<T>): T {
        val registry = requireRegisteredProperty(propertyName)
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset))
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property value is not assignable to the given class
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <T> getCheckedValue(currentMeta: Long, propertyName: String, tClass: Class<T>): T {
        val registry = requireRegisteredProperty(propertyName)
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset))
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property value is not assignable to the given class
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun <T> getCheckedValue(currentMeta: BigInteger?, propertyName: String, tClass: Class<T>): T {
        val registry = requireRegisteredProperty(propertyName)
        return tClass.cast(registry.property.getValue(currentMeta, registry.offset))
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    fun <T> getUncheckedValue(currentMeta: Int, propertyName: String): T {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getValue(currentMeta, registry.offset) as T
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    fun <T> getUncheckedValue(currentMeta: Long, propertyName: String): T {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getValue(currentMeta, registry.offset) as T
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @SuppressWarnings("unchecked")
    fun <T> getUncheckedValue(currentMeta: BigInteger?, propertyName: String): T {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getValue(currentMeta, registry.offset) as T
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(currentMeta: Int, propertyName: String): Int {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getIntValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(currentMeta: Long, propertyName: String): Int {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getIntValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getIntValue(currentMeta: BigInteger?, propertyName: String): Int {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getIntValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    fun getPersistenceValue(currentMeta: Int, propertyName: String): String {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getPersistenceValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    fun getPersistenceValue(currentMeta: Long, propertyName: String): String {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getPersistenceValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     */
    fun getPersistenceValue(currentMeta: BigInteger?, propertyName: String): String {
        val registry = requireRegisteredProperty(propertyName)
        return registry.property.getPersistenceValue(currentMeta, registry.offset)
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(currentMeta: Int, propertyName: String): Boolean {
        val registry = requireRegisteredProperty(propertyName)
        return if (registry.property is BooleanBlockProperty) {
            (registry.property as BooleanBlockProperty?).getBooleanValue(currentMeta, registry.offset)
        } else registry.property.getValue(currentMeta, registry.offset) as Boolean
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(currentMeta: Long, propertyName: String): Boolean {
        val registry = requireRegisteredProperty(propertyName)
        return if (registry.property is BooleanBlockProperty) {
            (registry.property as BooleanBlockProperty?).getBooleanValue(currentMeta, registry.offset)
        } else registry.property.getValue(currentMeta, registry.offset) as Boolean
    }

    /**
     * @throws NoSuchElementException If the property is not registered
     * @throws InvalidBlockPropertyMetaException If the meta contains invalid data
     * @throws ClassCastException If the property don't hold boolean values
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getBooleanValue(currentMeta: BigInteger?, propertyName: String): Boolean {
        val registry = requireRegisteredProperty(propertyName)
        return if (registry.property is BooleanBlockProperty) {
            (registry.property as BooleanBlockProperty?).getBooleanValue(currentMeta, registry.offset)
        } else registry.property.getValue(currentMeta, registry.offset) as Boolean
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun forEach(consumer: ObjIntConsumer<BlockProperty<*>?>) {
        for (registry in byName.values()) {
            consumer.accept(registry.property, registry.offset)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun forEach(consumer: Consumer<BlockProperty<*>?>) {
        for (registry in byName.values()) {
            consumer.accept(registry.property)
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <R> reduce(identity: R, accumulator: TriFunction<BlockProperty<*>?, Integer?, R, R>): R {
        var result = identity
        for (registry in byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result)
        }
        return result
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun reduceInt(identity: Int, accumulator: ToIntTriFunctionTwoInts<BlockProperty<*>?>): Int {
        var result = identity
        for (registry in byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result)
        }
        return result
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun reduceLong(identity: Long, accumulator: ToLongTriFunctionOneIntOneLong<BlockProperty<*>?>): Long {
        var result = identity
        for (registry in byName.values()) {
            result = accumulator.apply(registry.property, registry.offset, result)
        }
        return result
    }

    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val itemPropertyNames: List<String>
        get() {
            val itemProperties: List<String> = ArrayList(byName.size())
            for (registry in byName.values()) {
                if (registry.property!!.isExportedToItem()) {
                    itemProperties.add(registry.property.getName())
                } else {
                    break
                }
            }
            return itemProperties
        }

    @Override
    override fun toString(): String {
        return "BlockProperties{" +
                "bitSize=" + bitSize +
                ", properties=" + byName.values() +
                '}'
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings(["rawtypes", "java:S3740", "unchecked"])
    fun isDefaultValue(propertyName: String, @Nullable value: Serializable?): Boolean {
        val blockProperty: BlockProperty? = getBlockProperty(propertyName)
        return blockProperty.isDefaultValue(value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T : Serializable?> isDefaultValue(property: BlockProperty<T>, @Nullable value: T): Boolean {
        return isDefaultValue(property.getName(), value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings(["rawtypes", "java:S3740"])
    fun isDefaultIntValue(propertyName: String, value: Int): Boolean {
        val blockProperty: BlockProperty? = getBlockProperty(propertyName)
        return blockProperty.isDefaultIntValue(value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T : Serializable?> isDefaultIntValue(property: BlockProperty<T>, value: Int): Boolean {
        return isDefaultIntValue(property.getName(), value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @SuppressWarnings(["rawtypes", "java:S3740"])
    fun isDefaultBooleanValue(propertyName: String, value: Boolean): Boolean {
        val blockProperty: BlockProperty? = getBlockProperty(propertyName)
        return blockProperty.isDefaultBooleanValue(value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T : Serializable?> isDefaultBooleanValue(property: BlockProperty<T>, value: Boolean): Boolean {
        return isDefaultBooleanValue(property.getName(), value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Value
    class RegisteredBlockProperty {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        var property: BlockProperty<*>? = null

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        var offset = 0

        /**
         * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun validateMeta(meta: Int) {
            property.validateMeta(meta, offset)
        }

        /**
         * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun validateMeta(meta: Long) {
            property.validateMeta(meta, offset)
        }

        /**
         * @throws InvalidBlockPropertyMetaException if the value in the meta at the given offset is not valid
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun validateMeta(meta: BigInteger?) {
            property.validateMeta(meta, offset)
        }

        @Override
        override fun toString(): String {
            return offset.toString() + "-" + (offset + property.getBitSize()) + ":" + property.getName()
        }
    }

    /**
     * @throws IllegalArgumentException If there are validation failures
     */
    init {
        if (itemBlockProperties == null) {
            this.itemBlockProperties = this
        } else {
            this.itemBlockProperties = itemBlockProperties
        }
        val registry: Map<String, RegisteredBlockProperty> = LinkedHashMap(properties.size)
        val byPersistenceName: Map<String, RegisteredBlockProperty> = LinkedHashMap(properties.size)
        var offset = 0
        var allowItemExport = true
        for (property in properties) {
            Preconditions.checkArgument(property != null, "The properties can not contains null values")
            if (property!!.isExportedToItem()) {
                Preconditions.checkArgument(allowItemExport, "Cannot export a property to item if the previous property does not export")
                Preconditions.checkArgument(offset <= 6) // Only 6 bits of data can be stored in item blocks, client side limitation.
            } else {
                allowItemExport = false
            }
            val register = RegisteredBlockProperty(property, offset)
            offset += property.getBitSize()
            Preconditions.checkArgument(registry.put(property.getName(), register) == null, "The property %s is duplicated by it's normal name", property.getName())
            Preconditions.checkArgument(byPersistenceName.put(property.getPersistenceName(), register) == null, "The property %s is duplicated by it's persistence name", property.getPersistenceName())
        }
        byName = Collections.unmodifiableMap(registry)
        bitSize = offset
    }
}