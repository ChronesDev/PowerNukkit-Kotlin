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
package cn.nukkit.blockproperty.exception

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-01-12
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ParametersAreNullableByDefault
class InvalidBlockPropertyPersistenceValueException : InvalidBlockPropertyException {
    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @Nullable
    val currentValue: String

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @Nullable
    val invalidValue: String

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: String, invalidValue: String) : super(property, buildMessage(currentValue, invalidValue)) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: String, invalidValue: String, message: String) : super(property, buildMessage(currentValue, invalidValue) + ". " + message) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: String, invalidValue: String, message: String, cause: Throwable?) : super(property, buildMessage(currentValue, invalidValue) + ". " + message, cause) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(@Nonnull property: BlockProperty<*>, currentValue: String, invalidValue: String, cause: Throwable?) : super(property, buildMessage(currentValue, invalidValue), cause) {
        this.currentValue = currentValue
        this.invalidValue = invalidValue
    }

    companion object {
        private const val serialVersionUID = 1L
        private fun buildMessage(currentValue: Object, invalidValue: Object): String {
            return "Current Value: $currentValue, Invalid Value: $invalidValue"
        }
    }
}