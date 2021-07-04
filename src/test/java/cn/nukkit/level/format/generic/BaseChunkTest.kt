package cn.nukkit.level.format.generic

import cn.nukkit.block.BlockID

@ExtendWith(PowerNukkitExtension::class)
internal class BaseChunkTest {
    @Mock
    var anvil: Anvil? = null

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    var chunk: BaseChunk? = null
    @BeforeEach
    fun setup() {
        chunk.setPaletteUpdatesDelayed(true)
        chunk.sections = arrayOfNulls<ChunkSection>(16)
        System.arraycopy(EmptyChunkSection.EMPTY, 0, chunk.sections, 0, 16)
        chunk.setProvider(anvil)
        chunk.providerClass = Anvil::class.java
    }
    /////////////////////////

    ////////////////////////////////

    ////////////////////////////////
    @get:Test
    val isBlockChangeAllowed: Unit

    ////////////////////////////////

    ////////////////////////////////

        ////////////////////////////////
        get() {
            val allow: BlockState = BlockState.of(BlockID.ALLOW)
            val deny: BlockState = BlockState.of(BlockID.DENY)
            val border: BlockState = BlockState.of(BlockID.BORDER_BLOCK)
            val x = 5
            val baseY = 6
            val z = 7

            /////////////////////////
            chunk.setBlockState(x, baseY, z, deny)
            for (y in 0 until baseY) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY..254) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }

            ////////////////////////////////
            chunk.setBlockState(x, baseY + 30, z, allow)
            for (y in 0 until baseY) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY until baseY + 30) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY + 30..255) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }

            ////////////////////////////////
            val chunkStart = 96
            chunk.setBlockState(x, chunkStart, z, deny)
            for (y in 0 until baseY) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY until baseY + 30) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY + 30 until chunkStart) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in chunkStart..255) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }

            ////////////////////////////////
            chunk.setBlockState(x, chunkStart + 15, z, allow)
            for (y in 0 until baseY) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY until baseY + 30) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY + 30 until chunkStart) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in chunkStart until chunkStart + 15) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in chunkStart + 15..255) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }

            ////////////////////////////////
            chunk.setBlockState(x, 200, z, border)
            for (y in 0..255) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }

            ////////////////////////////////
            chunk.setBlockState(x, 200, z, deny)
            for (y in 0 until baseY) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY until baseY + 30) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in baseY + 30 until chunkStart) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in chunkStart until chunkStart + 15) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in chunkStart + 15..199) {
                assertTrue(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
            for (y in 200..255) {
                assertFalse(chunk.isBlockChangeAllowed(x, y, z), "x:$x y:$y z:$z")
            }
        }

    @Test
    fun backwardCompatibilityUpdate2InvalidWalls() {
        val wallType: Int = BlockWall.WallType.DIORITE.ordinal()
        val x = 3
        val y = 4
        val z = 5
        val invalid = 48 or wallType
        chunk.setBlock(x, y, z, BlockID.COBBLE_WALL, invalid)
        chunk.setBlock(x, y + 1, z, BlockID.COBBLE_WALL, wallType)
        chunk.setBlock(x + 1, y, z, BlockID.COBBLE_WALL, wallType)
        chunk.setBlock(x + 1, y + 1, z, BlockID.COBBLE_WALL, wallType)
        val section: ChunkSection = chunk.getSection(y shr 4)
        section.setContentVersion(1)
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x, y, z))
        assertEquals(invalid, chunk.getBlockData(x, y, z))
        assertEquals(1, section.getContentVersion())
        val level: Level = mock(Level::class.java, Answers.CALLS_REAL_METHODS)
        doReturn("FakeLevel").`when`(level).getName()
        doReturn(chunk).`when`(level).getChunk(x shr 4, z shr 4)
        chunk.backwardCompatibilityUpdate(level)
        assertEquals(ChunkUpdater.getCurrentContentVersion(), section.getContentVersion())
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x, y, z))
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x, y + 1, z))
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x + 1, y, z))
        assertEquals(BlockID.COBBLE_WALL, chunk.getBlockId(x + 1, y + 1, z))
        val wall = BlockWall(wallType)
        wall.setWallPost(true)
        wall.setConnection(BlockFace.EAST, BlockWall.WallConnectionType.TALL)
        assertEquals(wall.getDamage(), chunk.getBlockData(x, y, z))
        wall.setWallPost(true)
        wall.clearConnections()
        wall.setConnection(BlockFace.EAST, BlockWall.WallConnectionType.SHORT)
        assertEquals(wall.getDamage(), chunk.getBlockData(x, y + 1, z))
        wall.setWallPost(true)
        wall.clearConnections()
        wall.setConnection(BlockFace.WEST, BlockWall.WallConnectionType.TALL)
        assertEquals(wall.getDamage(), chunk.getBlockData(x + 1, y, z))
        wall.setWallPost(true)
        wall.clearConnections()
        wall.setConnection(BlockFace.WEST, BlockWall.WallConnectionType.SHORT)
        assertEquals(wall.getDamage(), chunk.getBlockData(x + 1, y + 1, z))
    }
}