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
class SingleLayerStorage : LayerStorage {
    private var storage: BlockStorage

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
        storage = ImmutableBlockStorage.EMPTY
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(storage: BlockStorage) {
        this.storage = storage
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun hasBlocks(): Boolean {
        return storage.hasBlockIds()
    }

    @SneakyThrows(CloneNotSupportedException::class)
    @Override
    override fun clone(): SingleLayerStorage {
        val clone = super.clone() as SingleLayerStorage
        clone.storage = clone.storage.copy()
        return clone
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getStorageOrEmpty(layer: Int): BlockStorage {
        return when (layer) {
            0 -> storage
            1 -> ImmutableBlockStorage.EMPTY
            else -> throw IndexOutOfBoundsException("Invalid layer: $layer")
        }
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun getOrSetStorage(setLayerStorage: Consumer<LayerStorage?>, contentVersion: IntSupplier, layer: Int): BlockStorage {
        when (layer) {
            0 -> {
            }
            1 -> {
                val blockStorage: BlockStorage = createBlockStorage(contentVersion.getAsInt())
                val multiLayerStorage = MultiLayerStorage(storage, blockStorage)
                setLayerStorage.accept(multiLayerStorage)
                return blockStorage
            }
            else -> throw IndexOutOfBoundsException("Invalid layer: $layer")
        }
        var blockStorage: BlockStorage = storage
        if (blockStorage !== ImmutableBlockStorage.EMPTY) {
            return blockStorage
        }
        blockStorage = createBlockStorage(contentVersion.getAsInt())
        storage = blockStorage
        return blockStorage
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    @Override
    override fun getStorageOrNull(layer: Int): BlockStorage? {
        if (layer != 0) {
            return null
        }
        val blockStorage: BlockStorage = storage
        return if (blockStorage !== ImmutableBlockStorage.EMPTY) blockStorage else null
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun delayPaletteUpdates() {
        storage.delayPaletteUpdates()
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun writeTo(stream: BinaryStream) {
        stream.putByte(ChunkSection.STREAM_STORAGE_VERSION as Byte)
        val blockStorage: BlockStorage = storage
        if (blockStorage === ImmutableBlockStorage.EMPTY) {
            stream.putByte(0.toByte())
            return
        }
        stream.putByte(1.toByte())
        blockStorage.writeTo(stream)
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun size(): Int {
        return if (storage === ImmutableBlockStorage.EMPTY) 0 else 1
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun compress(setLayerStorage: Consumer<LayerStorage?>) {
        val blockStorage: BlockStorage = storage
        if (blockStorage === ImmutableBlockStorage.EMPTY) {
            setLayerStorage.accept(EMPTY)
            return
        }
        blockStorage.recheckBlocks()
        if (!blockStorage.hasBlockIds()) {
            setLayerStorage.accept(EMPTY)
        }
    }
}