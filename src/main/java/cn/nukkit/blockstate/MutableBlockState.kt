package cn.nukkit.blockstate

import cn.nukkit.api.API

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
@EqualsAndHashCode
@ParametersAreNonnullByDefault
abstract class MutableBlockState internal constructor(@field:Since("1.4.0.0-PN") @field:PowerNukkitOnly override val blockId: Int, properties: BlockProperties) : IMutableBlockState {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected override val properties: BlockProperties
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @API(definition = INTERNAL, usage = INCUBATING)
    fun setDataStorageWithoutValidation(storage: Number?) {
        setDataStorage(storage)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    @Throws(InvalidBlockStateException::class)
    override fun setState(state: IBlockState) {
        if (state.getBlockId() === blockId) {
            if (BlockState::class.java === state.getClass() && (state as BlockState).isCachedValidationValid()) {
                setDataStorageWithoutValidation(state.getDataStorage())
            } else {
                setDataStorage(state.getDataStorage())
            }
        } else {
            super@IMutableBlockState.setState(state)
        }
    }

    @Nonnull
    @Override
    fun getProperties(): BlockProperties {
        return properties
    }

    override val fullId: Int
        @Override @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself") get() = super@IMutableBlockState.getFullId()
    override val bigId: Long
        @Override @Deprecated @DeprecationDetails(reason = "Can't store all data, exists for backward compatibility reasons", since = "1.4.0.0-PN", replaceWith = "the BlockState itself") get() = super@IMutableBlockState.getBigId()
    override val bitSize: Int
        @Override get() = getProperties().getBitSize()

    /**
     * @throws cn.nukkit.blockstate.exception.InvalidBlockStateException if the state is invalid
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    abstract fun validate()
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    abstract fun copy(): MutableBlockState?

    init {
        this.properties = properties
    }
}