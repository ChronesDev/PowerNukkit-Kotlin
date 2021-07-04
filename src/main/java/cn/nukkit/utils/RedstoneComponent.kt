package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

/**
 * Interface, all redstone components implement, containing redstone related methods.
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
interface RedstoneComponent {
    //
    // DEFAULT METHODS
    //
    /**
     * Send a redstone update to all blocks around this block.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateAroundRedstone(@Nullable vararg ignoredFaces: BlockFace?) {
        var ignoredFaces: Array<out BlockFace?> = ignoredFaces
        if (ignoredFaces == null) ignoredFaces = arrayOfNulls<BlockFace>(0)
        this.updateAroundRedstone(Arrays.asList(ignoredFaces))
    }

    /**
     * Send a redstone update to all blocks around this block.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateAroundRedstone(@Nonnull ignoredFaces: List<BlockFace?>) {
        if (this is Position) Companion.updateAroundRedstone(this as Position, ignoredFaces)
    }

    /**
     * Send a redstone update to all blocks around this block.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateAllAroundRedstone(@Nullable vararg ignoredFaces: BlockFace?) {
        var ignoredFaces: Array<out BlockFace?> = ignoredFaces
        if (ignoredFaces == null) ignoredFaces = arrayOfNulls<BlockFace>(0)
        this.updateAllAroundRedstone(Arrays.asList(ignoredFaces))
    }

    /**
     * Send a redstone update to all blocks around this block and also around the blocks of those updated blocks.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun updateAllAroundRedstone(@Nonnull ignoredFaces: List<BlockFace?>) {
        if (this is Position) Companion.updateAllAroundRedstone(this as Position, ignoredFaces)
    }

    companion object {
        //
        // STATIC METHODS
        //
        /**
         * Send a redstone update to all blocks around the given position.
         * @param pos The middle of the blocks around.
         * @param ignoredFaces The faces, that shouldn't get updated.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun updateAroundRedstone(@Nonnull pos: Position, @Nullable vararg ignoredFaces: BlockFace?) {
            var ignoredFaces: Array<out BlockFace?> = ignoredFaces
            if (ignoredFaces == null) ignoredFaces = arrayOfNulls<BlockFace>(0)
            updateAroundRedstone(pos, Arrays.asList(ignoredFaces))
        }

        /**
         * Send a redstone update to all blocks around the given position.
         * @param pos The middle of the blocks around.
         * @param ignoredFaces The faces, that shouldn't get updated.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun updateAroundRedstone(@Nonnull pos: Position, @Nonnull ignoredFaces: List<BlockFace?>) {
            for (face in BlockFace.values()) {
                if (ignoredFaces.contains(face)) continue
                pos.getLevelBlock().getSide(face).onUpdate(Level.BLOCK_UPDATE_REDSTONE)
            }
        }

        /**
         * Send a redstone update to all blocks around the given position and also around the blocks of those updated blocks.
         * @param pos The middle of the blocks around.
         * @param ignoredFaces The faces, that shouldn't get updated.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun updateAllAroundRedstone(@Nonnull pos: Position, @Nullable vararg ignoredFaces: BlockFace?) {
            var ignoredFaces: Array<out BlockFace?> = ignoredFaces
            if (ignoredFaces == null) ignoredFaces = arrayOfNulls<BlockFace>(0)
            updateAllAroundRedstone(pos, Arrays.asList(ignoredFaces))
        }

        /**
         * Send a redstone update to all blocks around the given position and also around the blocks of those updated blocks.
         * @param pos The middle of the blocks around.
         * @param ignoredFaces The faces, that shouldn't get updated.
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun updateAllAroundRedstone(@Nonnull pos: Position, @Nonnull ignoredFaces: List<BlockFace?>) {
            updateAroundRedstone(pos, ignoredFaces)
            for (face in BlockFace.values()) {
                if (ignoredFaces.contains(face)) continue
                updateAroundRedstone(pos.getSide(face), face.getOpposite())
            }
        }
    }
}