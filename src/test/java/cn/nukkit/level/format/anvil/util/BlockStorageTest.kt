package cn.nukkit.level.format.anvil.util

import cn.nukkit.block.BlockID

@ExtendWith(PowerNukkitExtension::class)
internal class BlockStorageTest {
    var blockStorage: BlockStorage? = null
    var x = 0
    var y = 0
    var z = 0
    @BeforeEach
    fun setUp() {
        blockStorage = BlockStorage()
        x = 3
        y = 4
        z = 5
    }

    @Deprecated
    @Test
    fun blockData() {
        val data = 6
        blockStorage.setBlockData(x, y, z, data)
        assertEquals(data, blockStorage.getBlockData(x, y, z))
        assertEquals(BlockState.of(0, data), blockStorage.getBlockState(x, y, z))
        blockStorage.setBlockData(x, y, z, 0)
        assertEquals(0, blockStorage.getBlockData(x, y, z))
        assertEquals(AIR, blockStorage.getBlockState(x, y, z))
    }

    @Test
    fun blockId() {
        val id: Int = BlockID.PRISMARINE
        blockStorage.setBlockId(x, y, z, id)
        assertEquals(id, blockStorage.getBlockId(x, y, z))
        assertEquals(BlockState.of(id), blockStorage.getBlockState(x, y, z))
        blockStorage.setBlockId(x, y, z, BlockID.AIR)
        assertEquals(BlockID.AIR, blockStorage.getBlockId(x, y, z))
        assertEquals(AIR, blockStorage.getBlockState(x, y, z))
    }

    @Deprecated
    @Test
    fun block() {
        val id: Int = GRANITE.getBlockId()
        val data: Int = GRANITE.getLegacyDamage()
        blockStorage.setBlock(x, y, z, id, data)
        assertEquals(BlockState.of(id, data), blockStorage.getBlockState(x, y, z))
        blockStorage.setBlock(x, y, z, BlockID.AIR, 0)
        assertEquals(AIR, blockStorage.getBlockState(x, y, z))
    }

    @Deprecated
    @Test
    fun fullBlock() {
        val fullBlock: Int = GRANITE.getFullId()
        blockStorage.setFullBlock(x, y, z, fullBlock)
        assertEquals(fullBlock, blockStorage.getFullBlock(x, y, z))
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z))
        blockStorage.setFullBlock(x, y, z, 0)
        assertEquals(0, blockStorage.getFullBlock(x, y, z))
        assertEquals(AIR, blockStorage.getBlockState(x, y, z))
    }

    @get:Test
    @get:Deprecated
    val andSetBlock: Unit
        get() {
            var oldBlock: BlockState = blockStorage.getAndSetBlock(x, y, z, GRANITE.getBlockId(), GRANITE.getLegacyDamage())
            assertEquals(AIR, oldBlock)
            assertEquals(GRANITE, blockStorage.getBlockState(x, y, z))
            oldBlock = blockStorage.getAndSetBlock(x, y, z, DIRT.getBlockId(), DIRT.getLegacyDamage())
            assertEquals(GRANITE, oldBlock)
            assertEquals(DIRT, blockStorage.getBlockState(x, y, z))
            oldBlock = blockStorage.getAndSetBlock(x, y, z, AIR.getBlockId(), AIR.getLegacyDamage())
            assertEquals(DIRT, oldBlock)
            assertEquals(AIR, blockStorage.getBlockState(x, y, z))
        }

    @get:Test
    val andSetBlockState: Unit
        get() {
            var oldBlock: BlockState = blockStorage.getAndSetBlockState(x, y, z, GRANITE)
            assertEquals(AIR, oldBlock)
            assertEquals(GRANITE, blockStorage.getBlockState(x, y, z))
            oldBlock = blockStorage.getAndSetBlockState(x, y, z, DIRT)
            assertEquals(GRANITE, oldBlock)
            assertEquals(DIRT, blockStorage.getBlockState(x, y, z))
            oldBlock = blockStorage.getAndSetBlockState(x, y, z, AIR)
            assertEquals(DIRT, oldBlock)
            assertEquals(AIR, blockStorage.getBlockState(x, y, z))
        }

    @Test
    fun blockState() {
        assertEquals(AIR, blockStorage.getBlockState(x, y, z))
        blockStorage.setBlockState(x, y, z, GRANITE)
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z))
        blockStorage.setBlockState(x, y, z, STONE)
        assertEquals(STONE, blockStorage.getBlockState(x, y, z))
        blockStorage.setBlockState(x, y, z, GRANITE)
        assertEquals(GRANITE, blockStorage.getBlockState(x, y, z))
    }

    @get:Test
    @get:Deprecated
    val andSetFullBlock: Unit
        get() {
            var oldBlock: Int = blockStorage.getAndSetFullBlock(x, y, z, GRANITE.getFullId())
            assertEquals(AIR.getFullId(), oldBlock)
            assertEquals(GRANITE, blockStorage.getBlockState(x, y, z))
            oldBlock = blockStorage.getAndSetFullBlock(x, y, z, DIRT.getFullId())
            assertEquals(GRANITE.getFullId(), oldBlock)
            assertEquals(DIRT, blockStorage.getBlockState(x, y, z))
            oldBlock = blockStorage.getAndSetFullBlock(x, y, z, AIR.getFullId())
            assertEquals(DIRT.getFullId(), oldBlock)
            assertEquals(AIR, blockStorage.getBlockState(x, y, z))
        }

    @Test
    fun copy() {
        val copy: BlockStorage = blockStorage.copy()
        copy.setBlockState(x, y, z, GRANITE)
        assertEquals(GRANITE, copy.getBlockState(x, y, z))
        assertEquals(AIR, blockStorage.getBlockState(x, y, z))
    }

    @Test
    fun hasBlockIds() {
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, STONE)
        assertTrue(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, AIR)
        blockStorage.recheckBlocks()
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
    }

    @Test
    fun hasBlockIdExtras() {
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, BlockState.of(500))
        assertTrue(blockStorage.hasBlockIds())
        assertTrue(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, AIR)
        blockStorage.recheckBlocks()
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
    }

    @Test
    fun hasBlockDataExtras() {
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x0F))
        assertTrue(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, AIR)
        blockStorage.recheckBlocks()
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x1F))
        assertTrue(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertTrue(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, AIR)
        blockStorage.recheckBlocks()
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
    }

    @Test
    fun hasBlockDataBig() {
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x1FF))
        assertTrue(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertTrue(blockStorage.hasBlockDataExtras())
        assertTrue(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, AIR)
        blockStorage.recheckBlocks()
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
    }

    @Test
    fun hasBlockDataHuge() {
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, BlockState.of(0, 0x1FFFF))
        assertTrue(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertTrue(blockStorage.hasBlockDataExtras())
        assertTrue(blockStorage.hasBlockDataBig())
        assertTrue(blockStorage.hasBlockDataHuge())
        blockStorage.setBlockState(x, y, z, AIR)
        blockStorage.recheckBlocks()
        assertFalse(blockStorage.hasBlockIds())
        assertFalse(blockStorage.hasBlockIdExtras())
        assertFalse(blockStorage.hasBlockDataExtras())
        assertFalse(blockStorage.hasBlockDataBig())
        assertFalse(blockStorage.hasBlockDataHuge())
    }

    @Test
    fun iterateStates() {
        val hits = AtomicInteger()
        val found = AtomicBoolean(false)
        blockStorage.setBlockState(x, y, z, GRANITE)
        blockStorage.iterateStates { x1, y1, z1, data ->
            hits.getAndIncrement()
            if (x1 === x && y1 === y && z1 === z) {
                assertEquals(GRANITE, data)
                assertFalse(found.getAndSet(true))
            } else {
                assertEquals(AIR, data)
            }
        }
        assertTrue(found.get())
        assertEquals(BlockStorage.SECTION_SIZE, hits.get())
    }

    @Test
    fun denyAllowBorder() {
        blockStorage.setBlockState(x, y, z, DENY)
        assertEquals(STATUS_NEUTRAL, blockStorage.getBlockChangeStateAbove(x, y - 1, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 1, z))
        blockStorage.setBlockState(x, y, z, ALLOW)
        assertEquals(STATUS_NEUTRAL, blockStorage.getBlockChangeStateAbove(x, y - 1, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 1, z))
        blockStorage.setBlockState(x, y + 2, z, DENY)
        assertEquals(STATUS_NEUTRAL, blockStorage.getBlockChangeStateAbove(x, y - 1, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 1, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 2, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 3, z))
        blockStorage.setBlockState(x, y + 4, z, ALLOW)
        assertEquals(STATUS_NEUTRAL, blockStorage.getBlockChangeStateAbove(x, y - 1, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 1, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 2, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 3, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 4, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 5, z))
        blockStorage.setBlockState(x, y + 6, z, BORDER)
        for (y in 0..15) {
            val finalY = y
            assertEquals(STATUS_BORDER, blockStorage.getBlockChangeStateAbove(x, y, z)) { "x:" + x + " y:" + finalY + " z:" + z + " should be " + STATUS_BORDER }
        }
        blockStorage.setBlockState(x, y + 8, z, BORDER)
        for (y in 0..15) {
            val finalY = y
            assertEquals(STATUS_BORDER, blockStorage.getBlockChangeStateAbove(x, y, z)) { "x:" + x + " y:" + finalY + " z:" + z + " should be " + STATUS_BORDER }
        }
        blockStorage.setBlockState(x, y + 6, z, STONE)
        for (y in 0..15) {
            val finalY = y
            assertEquals(STATUS_BORDER, blockStorage.getBlockChangeStateAbove(x, y, z)) { "x:" + x + " y:" + finalY + " z:" + z + " should be " + STATUS_BORDER }
        }
        blockStorage.setBlockState(x, y + 8, z, DENY)
        assertEquals(STATUS_NEUTRAL, blockStorage.getBlockChangeStateAbove(x, y - 1, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 1, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 2, z))
        assertEquals(STATUS_DENY, blockStorage.getBlockChangeStateAbove(x, y + 3, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 4, z))
        assertEquals(STATUS_ALLOW, blockStorage.getBlockChangeStateAbove(x, y + 5, z))
    }

    @Test
    fun writeToWithInvalidData() {
        assertFalse(blockStorage.isPaletteUpdateDelayed())
        blockStorage.setBlockState(x, y, z, BlockState.of(BlockID.POLISHED_BLACKSTONE_BRICK_WALL, 7))
        assertTrue(blockStorage.isPaletteUpdateDelayed())
        val stream = BinaryStream()
        blockStorage.writeTo(stream)
    }

    companion object {
        private val DIRT: BlockState = BlockState.of(BlockID.DIRT)
        private val STONE: BlockState = BlockState.of(BlockID.STONE)
        private val GRANITE: BlockState = BlockState.of(BlockID.STONE, BlockStone.GRANITE)
        private val DENY: BlockState = BlockState.of(BlockID.DENY)
        private val ALLOW: BlockState = BlockState.of(BlockID.ALLOW)
        private val BORDER: BlockState = BlockState.of(BlockID.BORDER_BLOCK)
        private const val STATUS_NEUTRAL = 0
        private const val STATUS_DENY = 1
        private const val STATUS_ALLOW = 2
        private const val STATUS_BORDER = 3
    }
}