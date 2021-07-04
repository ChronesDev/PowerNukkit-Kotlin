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
class MultiLayerStorage : LayerStorage {
    private var storages: Array<BlockStorage>

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        storages = BlockStorage.EMPTY_ARRAY
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(vararg storages: BlockStorage) {
        this.storages = storages
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun compress(setLayerStorage: Consumer<LayerStorage?>) {
        // Remove unused storage layers
        var newSize = storages.size
        for (i in storages.indices.reversed()) {
            val storage: BlockStorage = storages[i]
            if (storage == null || storage === ImmutableBlockStorage.EMPTY) {
                newSize--
            } else if (storage.hasBlockIds()) {
                storage.recheckBlocks()
                if (storage.hasBlockIds()) {
                    break
                } else {
                    newSize--
                }
            } else {
                newSize--
            }
        }
        if (newSize == 0) {
            setLayerStorage.accept(EMPTY)
        } else if (newSize == 1) {
            setLayerStorage.accept(SingleLayerStorage(storages[0]))
        } else if (newSize != storages.size) {
            storages = Arrays.copyOf(storages, newSize)
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun size(): Int {
        return storages.size
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun writeTo(stream: BinaryStream) {
        stream.putByte(ChunkSection.STREAM_STORAGE_VERSION as Byte)
        stream.putByte(storages.size.toByte())
        for (blockStorage in storages) {
            blockStorage.writeTo(stream)
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun delayPaletteUpdates() {
        for (storage in storages) {
            storage.delayPaletteUpdates()
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getOrSetStorage(@Nullable setLayerStorage: Consumer<LayerStorage?>?, contentVersion: IntSupplier, layer: Int): BlockStorage {
        val oldLen = storages.size
        if (layer >= oldLen) {
            if (layer > 1) {
                throw IndexOutOfBoundsException("Only layer 0 and 1 are supported. Attempted: $layer")
            }
            storages = Arrays.copyOf(storages, layer + 1)
            Arrays.fill(storages, oldLen, layer, ImmutableBlockStorage.EMPTY)
            val storage: BlockStorage = createBlockStorage(contentVersion.getAsInt())
            storages[layer] = storage
            return storage
        } else if (layer < 0) {
            throw IndexOutOfBoundsException("Only layer 0 and 1 are supported. Attempted: $layer")
        }
        var storage: BlockStorage = storages[layer]
        if (storage !== ImmutableBlockStorage.EMPTY) {
            return storage
        }
        storage = createBlockStorage(contentVersion.getAsInt())
        storages[layer] = storage
        return storage
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getStorageOrEmpty(layer: Int): BlockStorage {
        return if (layer < storages.size) storages[layer] else ImmutableBlockStorage.EMPTY
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Override
    override fun getStorageOrNull(layer: Int): BlockStorage? {
        if (layer >= storages.size) {
            return null
        }
        val storage: BlockStorage = storages[layer]
        return if (storage === ImmutableBlockStorage.EMPTY) null else storage
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun hasBlocks(): Boolean {
        for (storage in storages) {
            if (storage.hasBlockIds()) {
                return true
            }
        }
        return false
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @SneakyThrows(CloneNotSupportedException::class)
    @Override
    override fun clone(): MultiLayerStorage {
        val clone = super.clone() as MultiLayerStorage
        clone.storages = clone.storages.clone()
        for (i in clone.storages.indices) {
            clone.storages[i] = clone.storages[i].copy()
        }
        return clone
    }
}