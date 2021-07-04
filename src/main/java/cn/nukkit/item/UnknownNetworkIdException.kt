/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2021  José Roberto de Araújo Júnior
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
package cn.nukkit.item

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2021-03-23
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class UnknownNetworkIdException : IllegalStateException {
    @Nullable
    @Transient
    private val item: Item?

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        item = null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(s: String?) : super(s) {
        item = null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(message: String?, cause: Throwable?) : super(message, cause) {
        item = null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(cause: Throwable?) : super(cause) {
        item = null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(item: Item?) {
        this.item = copy(item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(item: Item?, s: String?) : super(s) {
        this.item = copy(item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(item: Item?, message: String?, cause: Throwable?) : super(message, cause) {
        this.item = copy(item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(item: Item?, cause: Throwable?) : super(cause) {
        this.item = copy(item)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getItem(): Item? {
        return if (item == null) null else item.clone()
    }

    companion object {
        private fun copy(item: Item?): Item? {
            return if (item == null) null else item.clone()
        }
    }
}