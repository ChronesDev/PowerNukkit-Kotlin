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
package cn.nukkit.level.format.anvil

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class LayerStorage @PowerNukkitOnly @Since("1.4.0.0-PN") protected constructor() : Cloneable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun hasBlocks(): Boolean {
        return false
    }

    @Override
    @Throws(CloneNotSupportedException::class)
    fun clone(): LayerStorage {
        return if (getClass() === cn.nukkit.level.format.anvil.LayerStorage::class.java) {
            this
        } else super.clone() as LayerStorage
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getStorageOrEmpty(layer: Int): BlockStorage {
        return ImmutableBlockStorage.EMPTY
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getOrSetStorage(setLayerStorage: Consumer<LayerStorage>, contentVersion: IntSupplier?, layer: Int): BlockStorage {
        val populatedLayerStorage: LayerStorage = if (layer == 0) SingleLayerStorage() else MultiLayerStorage()
        setLayerStorage.accept(populatedLayerStorage)
        return populatedLayerStorage.getOrSetStorage(setLayerStorage, contentVersion, layer)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getStorageOrNull(layer: Int): BlockStorage? {
        return null
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun delayPaletteUpdates() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun writeTo(stream: BinaryStream) {
        stream.putByte(ChunkSection.STREAM_STORAGE_VERSION as Byte)
        stream.putByte(0.toByte())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun size(): Int {
        return 0
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun compress(setLayerStorage: Consumer<LayerStorage>?) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun createBlockStorage(contentVersion: Int): BlockStorage {
        val storage = BlockStorage()
        if (contentVersion < ChunkUpdater.getCurrentContentVersion()) {
            storage.delayPaletteUpdates()
        }
        return storage
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY = LayerStorage()
    }
}