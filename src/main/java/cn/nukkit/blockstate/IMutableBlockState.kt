package cn.nukkit.blockstate

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
interface IMutableBlockState : IBlockState {
    /**
     * Replace all matching states of this block state with the same states of the given block state.
     *
     * States that doesn't exists in the other state are ignored.
     *
     * Only properties that matches each other will be copied, for example, if this state have an age property
     * going from 0 to 7 and the other have an age from 0 to 15, the age property won't change.
     * @throws UnsupportedOperationException If the state is from a different block id and property copying isn't supported by the implementation
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @param state The states that will have the properties copied.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(InvalidBlockStateException::class)
    fun setState(state: IBlockState) {
        if (state.getBlockId() === getBlockId()) {
            setDataStorage(state.getDataStorage())
        } else {
            //TODO Implement property value copying
            throw UnsupportedOperationException()
        }
    }

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @throws InvalidBlockStateDataTypeException If the storage class type is not supported
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorage(@Nonnegative storage: Number?)

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorageFromInt(@Nonnegative storage: Int)

    /**
     * @throws InvalidBlockStateException If the given storage has invalid data properties
     * @throws InvalidBlockStateDataTypeException If the storage class type is not supported
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorage(@Nonnegative storage: Number, repair: Boolean): Boolean {
        return setDataStorage(storage, repair, null)
    }

    /**
     * @return if the storage was repaired
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorageFromInt(@Nonnegative storage: Int, repair: Boolean): Boolean {
        return setDataStorageFromInt(storage, repair, null)
    }

    /**
     * @return if the storage was repaired
     * @throws InvalidBlockStateException If repair is false and the storage has an invalid property state
     * @throws InvalidBlockStateDataTypeException If the storage has an unsupported number type
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorage(@Nonnegative storage: Number, repair: Boolean, @Nullable callback: Consumer<BlockStateRepair?>?): Boolean {
        return try {
            setDataStorage(storage)
            false
        } catch (e: InvalidBlockStateException) {
            if (repair) {
                val bigInteger: BigInteger
                bigInteger = try {
                    BigDecimal(storage.toString()).toBigIntegerExact()
                } catch (e2: NumberFormatException) {
                    val ex = InvalidBlockStateDataTypeException(storage, e2)
                    ex.addSuppressed(e)
                    throw ex
                } catch (e2: ArithmeticException) {
                    val ex = InvalidBlockStateDataTypeException(storage, e2)
                    ex.addSuppressed(e)
                    throw ex
                }
                try {
                    setDataStorage(repairStorage(getBlockId(), bigInteger, getProperties(), callback))
                } catch (e2: InvalidBlockPropertyException) {
                    val ex = InvalidBlockStateException(e.getState(), "The state is invalid and could not be repaired", e)
                    ex.addSuppressed(e2)
                    throw ex
                } catch (e2: InvalidBlockStateException) {
                    val ex = InvalidBlockStateException(e.getState(), "The state is invalid and could not be repaired", e)
                    ex.addSuppressed(e2)
                    throw ex
                }
                return true
            }
            throw e
        }
    }

    /**
     * @return if the storage was repaired
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorageFromInt(@Nonnegative storage: Int, repair: Boolean, @Nullable callback: Consumer<BlockStateRepair?>?): Boolean {
        return try {
            setDataStorageFromInt(storage)
            false
        } catch (e: IllegalStateException) {
            if (repair) {
                setDataStorage(repairStorage(getBlockId(), BigInteger.valueOf(storage), getProperties(), callback))
                return true
            }
            throw e
        } catch (e: InvalidBlockPropertyException) {
            if (repair) {
                setDataStorage(repairStorage(getBlockId(), BigInteger.valueOf(storage), getProperties(), callback))
                return true
            }
            throw e
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setDataStorageFromItemBlockMeta(itemBlockMeta: Int) {
        val allProperties: BlockProperties = getProperties()
        val itemBlockProperties: BlockProperties = allProperties.getItemBlockProperties()
        if (allProperties.equals(itemBlockProperties)) {
            setDataStorageFromInt(itemBlockMeta)
            return
        }
        val item: MutableBlockState = itemBlockProperties.createMutableState(getBlockId())
        item.setDataStorageFromInt(itemBlockMeta)
        val converted: MutableBlockState = allProperties.createMutableState(getBlockId())
        itemBlockProperties.getItemPropertyNames().forEach { property -> converted.setPropertyValue(property, item.getPropertyValue(property)) }
        setDataStorage(converted.getDataStorage())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setPropertyValue(propertyName: String?, @Nullable value: Serializable?)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBooleanValue(propertyName: String?, value: Boolean)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setIntValue(propertyName: String?, value: Int)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setBooleanValue(property: BlockProperty<Boolean?>, value: Boolean) {
        setBooleanValue(property.getName(), value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setIntValue(property: BlockProperty<Integer?>, value: Int) {
        setIntValue(property.getName(), value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T : Serializable?> setPropertyValue(property: BlockProperty<T>, @Nullable value: T) {
        setPropertyValue(property.getName(), value)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun toggleBooleanProperty(propertyName: String?): Boolean {
        val newValue: Boolean = !getBooleanValue(propertyName)
        setBooleanValue(propertyName, newValue)
        return newValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun toggleBooleanProperty(property: BlockProperty<Boolean?>): Boolean {
        return toggleBooleanProperty(property.getName())
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @SuppressWarnings("unchecked")
        @Nonnull
        fun repairStorage(
                @Nonnegative blockId: Int, @Nonnull storage: BigInteger, @Nonnull properties: BlockProperties,
                @Nullable callback: Consumer<BlockStateRepair?>?): BigInteger? {
            Validation.checkPositive("blockId", blockId)
            var checkedBits = 0
            var repairs = 0
            var current: BigInteger = storage
            for (reg in properties.getAllProperties()) {
                checkedBits += reg.getProperty().getBitSize()
                try {
                    reg.validateMeta(current)
                } catch (e: InvalidBlockPropertyException) {
                    val property: BlockProperty<*> = reg.getProperty()
                    val offset: Int = reg.getOffset()
                    var next: BigInteger = property.setValue(current, offset, null)
                    if (callback != null) {
                        val fixed: Serializable = property.getValue(next, offset)
                        val stateRepair = BlockStateRepair(
                                blockId, properties,
                                storage, current, next, repairs++, property, offset,
                                property.getMetaFromBigInt(current, offset),
                                fixed, fixed, e
                        )
                        callback.accept(stateRepair)
                        val proposed: Serializable = stateRepair.getProposedPropertyValue()
                        if (!fixed.equals(proposed)) {
                            try {
                                next = (property as BlockProperty<Serializable?>).setValue(current, offset, proposed)
                            } catch (proposedFailed: InvalidBlockPropertyException) {
                                logIMutableBlockState.warn("Could not apply the proposed repair, using the default proposal. $stateRepair", proposedFailed)
                            }
                        }
                    }
                    current = next
                }
            }
            if (NukkitMath.bitLength(current) > checkedBits) {
                val validMask: BigInteger = BigInteger.ONE.shiftLeft(checkedBits).subtract(BigInteger.ONE)
                val next: BigInteger = current.and(validMask)
                if (callback != null) {
                    val stateRepair = BlockStateRepair(
                            blockId, properties,
                            storage, current, next, repairs, null,
                            checkedBits, current.shiftRight(checkedBits).intValue(), 0, 0,
                            null)
                    callback.accept(stateRepair)
                    if (!Integer.valueOf(0).equals(stateRepair.getProposedPropertyValue())) {
                        logIMutableBlockState.warn("Could not apply the proposed repair, using the default proposal. $stateRepair",
                                IllegalStateException("Attempted to propose a value outside the properties boundary"))
                    }
                }
                current = next
            }
            return current
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Nonnull
        fun handleUnsupportedStorageType(@Nonnegative blockId: Int, @Nonnegative storage: Number?, e: RuntimeException?): RuntimeException? {
            val ex: InvalidBlockStateException
            try {
                ex = InvalidBlockStateException(BlockState.of(blockId, storage), e)
            } catch (e2: InvalidBlockStateDataTypeException) {
                e2.addSuppressed(e)
                return e2
            }
            return ex
        }
    }
}