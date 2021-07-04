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
package cn.nukkit.level.format.anvil.util

import cn.nukkit.api.API

/**
 * @author joserobjr
 * @since 2020-10-02
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ImmutableBlockStorage @PowerNukkitOnly @Since("1.4.0.0-PN") @API(definition = INTERNAL, usage = BLEEDING) internal constructor(states: Array<BlockState>, flags: Byte, palette: PalettedBlockStorage, @Nullable denyStates: BitSet?) : BlockStorage(states.clone(), flags, palette.copy(), if (denyStates != null) denyStates.clone() as BitSet else null) {
    @Override
    protected override fun setBlockState(index: Int, @Nonnull state: BlockState?): BlockState {
        throw UnsupportedOperationException("This BlockStorage is immutable")
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun delayPaletteUpdates() {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun recheckBlocks() {
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    override fun immutableCopy(): ImmutableBlockStorage {
        return this
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY: ImmutableBlockStorage = BlockStorage().immutableCopy()
    }
}