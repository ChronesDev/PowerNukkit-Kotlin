package cn.nukkit.blockproperty

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
class ArrayBlockProperty<E : Serializable?> @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(name: String, exportedToItem: Boolean, universe: Array<E>, bitSize: Int, persistenceName: String, ordinal: Boolean, @Nullable persistenceNames: Array<String>?) : BlockProperty<E>(name, exportedToItem, bitSize, persistenceName) {
    @Nonnull
    private val universe: Array<E>
    private val persistenceNames: Array<String>?
    private val eClass: Class<E>
    val isOrdinal: Boolean
        @PowerNukkitOnly @Since("1.4.0.0-PN") get

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, universe: Array<E>, bitSize: Int, persistenceName: String) : this(name, exportedToItem, universe, bitSize, persistenceName, false) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, universe: Array<E>, bitSize: Int, persistenceName: String, ordinal: Boolean) : this(name, exportedToItem, universe, bitSize, persistenceName, ordinal, if (ordinal) null else Arrays.stream(universe).map(Objects::toString).map(String::toLowerCase).toArray { _Dummy_.__Array__() }) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, universe: Array<E>, bitSize: Int) : this(name, exportedToItem, universe, bitSize, name) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String, exportedToItem: Boolean, universe: Array<E>) : this(name, exportedToItem, checkUniverseLength(universe), NukkitMath.bitLength(universe.size - 1)) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(name: String?, exportedToItem: Boolean, enumClass: Class<E>) : this(name, exportedToItem, enumClass.getEnumConstants()) {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun copy(): ArrayBlockProperty<E> {
        return ArrayBlockProperty(getName(), isExportedToItem(), universe, getBitSize(), getPersistenceName(), isOrdinal, persistenceNames)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun exportingToItems(exportedToItem: Boolean): ArrayBlockProperty<E> {
        return ArrayBlockProperty(getName(), exportedToItem, universe, getBitSize(), getPersistenceName(), isOrdinal, persistenceNames)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun ordinal(ordinal: Boolean): ArrayBlockProperty<E> {
        return if (ordinal == isOrdinal) {
            this
        } else ArrayBlockProperty(getName(), isExportedToItem(), universe, getBitSize(), getPersistenceName(), ordinal)
    }

    @Override
    fun getMetaForValue(@Nullable value: E?): Int {
        if (value == null) {
            return 0
        }
        for (i in universe.indices) {
            if (universe[i]!!.equals(value)) {
                return i
            }
        }
        throw InvalidBlockPropertyValueException(this, null, value, "Element is not part of this property")
    }

    @Nonnull
    @Override
    override fun getValueForMeta(meta: Int): E {
        return universe[meta]
    }

    @Override
    override fun getIntValueForMeta(meta: Int): Int {
        try {
            validateMetaDirectly(meta)
        } catch (e: IllegalArgumentException) {
            throw InvalidBlockPropertyMetaException(this, meta, meta, e)
        }
        return meta
    }

    @Nonnull
    @Override
    override fun getPersistenceValueForMeta(meta: Int): String {
        try {
            validateMetaDirectly(meta)
        } catch (e: IllegalArgumentException) {
            throw InvalidBlockPropertyMetaException(this, meta, meta, e)
        }
        return if (isOrdinal) {
            Integer.toString(meta)
        } else persistenceNames!![meta]
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getMetaForPersistenceValue(persistenceValue: String?): Int {
        val meta: Int
        if (isOrdinal) {
            try {
                meta = Integer.parseInt(persistenceValue)
                validateMetaDirectly(meta)
            } catch (e: IndexOutOfBoundsException) {
                throw InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue,
                        "Expected a number from 0 to " + persistenceNames!!.size, e)
            } catch (e: IllegalArgumentException) {
                throw InvalidBlockPropertyPersistenceValueException(this, null, persistenceValue,
                        "Expected a number from 0 to " + persistenceNames!!.size, e)
            }
            return meta
        }
        for (index in persistenceNames.indices) {
            if (persistenceNames!![index].equals(persistenceValue)) {
                return index
            }
        }
        throw InvalidBlockPropertyPersistenceValueException(
                this, null, persistenceValue,
                "The value does not exists in this property."
        )
    }

    @Override
    protected override fun validateDirectly(@Nullable value: E) {
        for (`object` in universe) {
            if (`object` === value) {
                return
            }
        }
        throw IllegalArgumentException(value.toString() + " is not valid for this property")
    }

    @Override
    protected override fun validateMetaDirectly(meta: Int) {
        Preconditions.checkElementIndex(meta, universe.size)
    }

    override val valueClass: Class<E>
        @Nonnull @Override get() = eClass

    @Nonnull
    fun getUniverse(): Array<E> {
        return universe.clone()
    }

    override val defaultValue: T
        @Since("1.4.0.0-PN") @PowerNukkitOnly @Nonnull @Override get() = universe[0]

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun isDefaultValue(@Nullable value: E?): Boolean {
        return value == null || universe[0]!!.equals(value)
    }

    companion object {
        private const val serialVersionUID = 507174531989068430L
        private fun <E> checkUniverseLength(universe: Array<E>): Array<E> {
            Preconditions.checkNotNull(universe, "universe can't be null")
            Preconditions.checkArgument(universe.size > 0, "The universe can't be empty")
            return universe
        }
    }

    init {
        checkUniverseLength(universe)
        if (!ordinal) {
            Preconditions.checkArgument(persistenceNames != null, "persistenceNames can't be null when ordinal is false")
            Preconditions.checkArgument(persistenceNames!!.size == universe.size, "persistenceNames and universe must have the same length when ordinal is false")
            this.persistenceNames = persistenceNames.clone()
        } else {
            this.persistenceNames = null
        }
        isOrdinal = ordinal
        this.universe = universe.clone()
        eClass = universe.getClass().getComponentType() as Class<E>
        val elements: Set<E> = HashSet()
        val persistenceNamesCheck: Set<String> = HashSet()
        for (i in this.universe.indices) {
            val element = this.universe[i]
            Preconditions.checkNotNull(element, "The universe can not contain null values")
            Preconditions.checkArgument(elements.add(element), "The universe can not have duplicated elements")
            if (!ordinal) {
                val elementName = this.persistenceNames!![i]
                Preconditions.checkNotNull(elementName, "The persistenceNames can not contain null values")
                Preconditions.checkArgument(persistenceNamesCheck.add(elementName), "The persistenceNames can not have duplicated elements")
            }
        }
    }
}