/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.blockstate

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-10-03
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNonnullByDefault
class ZeroMutableBlockState @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(blockId: Int, properties: BlockProperties) : MutableBlockState(blockId, properties) {
    private val state: BlockState

    @Override
    override fun validate() {
    }

    @Nonnull
    @Override
    override fun copy(): MutableBlockState {
        return this
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setDataStorage(@Nonnegative storage: Number) {
        val c: Class<out Number?> = storage.getClass()
        val state: Int
        if (c === Integer::class.java || c === Short::class.java || c === Byte::class.java) {
            state = storage.intValue()
        } else {
            state = try {
                BigDecimal(storage.toString()).intValueExact()
            } catch (e: ArithmeticException) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e)
            } catch (e: NumberFormatException) {
                throw handleUnsupportedStorageType(getBlockId(), storage, e)
            }
        }
        if (state != 0) {
            throw handleUnsupportedStorageType(getBlockId(), storage, ArithmeticException("ZeroMutableBlockState only accepts zero"))
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setDataStorageFromInt(@Nonnegative storage: Int) {
        if (storage != 0) {
            throw handleUnsupportedStorageType(getBlockId(), storage, ArithmeticException("ZeroMutableBlockState only accepts zero"))
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun setPropertyValue(propertyName: String, @Nullable value: Serializable) {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted to set $propertyName to $value")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setBooleanValue(propertyName: String, value: Boolean) {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted to set $propertyName to $value")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun setIntValue(propertyName: String, value: Int) {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted to set $propertyName to $value")
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    fun getDataStorage(): Number {
        return 0
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun isDefaultState(): Boolean {
        return true
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getLegacyDamage(): Int {
        return 0
    }

    @Unsigned
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getBigDamage(): Int {
        return 0
    }

    @Nonnegative
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    fun getHugeDamage(): BigInteger {
        return BigInteger.ZERO
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    fun getPropertyValue(propertyName: String): Serializable {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property $propertyName")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getIntValue(propertyName: String): Int {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property $propertyName")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getBooleanValue(propertyName: String): Boolean {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property $propertyName")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    override fun getPersistenceValue(propertyName: String): String {
        throw NoSuchElementException("ZeroMutableBlockState can't have properties. Attempted get property $propertyName")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    fun getCurrentState(): BlockState {
        return state
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    fun getExactIntStorage(): Int {
        return 0
    }

    init {
        state = BlockState.of(blockId)
    }
}