package cn.nukkit.level

import cn.nukkit.api.DeprecationDetails

@Deprecated
@DeprecationDetails(reason = "Reimplemented using BlockState", replaceWith = "BlockStateRegistry", since = "1.4.0.0-PN")
@Log4j2
object GlobalBlockPalette {
    @Deprecated
    @DeprecationDetails(reason = "Public mutable array", replaceWith = "BlockStateRegistry.getBlockPaletteBytes() or BlockStateRegistry.copyBlockPaletteBytes()", since = "1.4.0.0-PN")
    val BLOCK_PALETTE: ByteArray = BlockStateRegistry.getBlockPaletteBytes()
    @Deprecated
    @DeprecationDetails(reason = "Limited to 32 bits meta", since = "1.4.0.0-PN", replaceWith = "BlockStateRegistry.getRuntimeId(BlockState)")
    fun getOrCreateRuntimeId(id: Int, meta: Int): Int {
        return BlockStateRegistry.getRuntimeId(id, meta)
    }

    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", replaceWith = "BlockStateRegistry.getRuntimeId(BlockState)", since = "1.3.0.0-PN")
    fun getOrCreateRuntimeId(legacyId: Int): Int {
        return getOrCreateRuntimeId(legacyId shr Block.DATA_BITS, legacyId and Block.DATA_MASK)
    }

    @Deprecated
    @DeprecationDetails(reason = "Moved to BlockStateRegistry", replaceWith = "BlockStateRegistry.getPersistenceName(int)", since = "1.3.0.0-PN")
    fun getName(blockId: Int): String {
        return BlockStateRegistry.getPersistenceName(blockId)
    }
}