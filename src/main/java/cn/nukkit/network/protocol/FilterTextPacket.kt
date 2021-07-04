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
package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2021-02-14
 */
@Since("1.4.0.0-PN")
@ToString
class FilterTextPacket : DataPacket {
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var text: String? = null

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var isFromServer = false

    @Since("1.4.0.0-PN")
    constructor() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(text: String?, fromServer: Boolean) {
        this.text = text
        isFromServer = fromServer
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun encode() {
        reset()
        putString(text)
        putBoolean(isFromServer)
    }

    @Override
    override fun decode() {
        text = getString()
        isFromServer = getBoolean()
    }

    companion object {
        @Since("1.4.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.FILTER_TEXT_PACKET
    }
}