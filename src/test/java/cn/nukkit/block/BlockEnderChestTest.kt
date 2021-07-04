package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
internal class BlockEnderChestTest {
    @MockLevel
    var level: Level? = null

    @MockPlayer
    var player: Player? = null
    val pos: Vector3 = Vector3(3, 4, 5)
    @Test
    fun place() {
        val playerPos = Position(pos.x, pos.y, pos.z, level)
        `when`(player.getPosition()).thenReturn(playerPos)
        `when`(player.getNextPosition()).thenReturn(playerPos.clone())
        `when`(player.getDirection()).thenReturn(BlockFace.NORTH)
        val item: Item = Item.getBlock(BlockID.ENDER_CHEST)
        assertTrue(level.setBlock(pos.down(), Block.get(BlockID.STONE)))
        val placed: Item = level.useItemOn(pos.down(), item, BlockFace.UP, .5f, .5f, .5f, player)
        assertNotEquals(item, placed)
        assertNotNull(placed)
        assertTrue(placed.isNull())
        assertEquals(BlockID.ENDER_CHEST, level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ(), 0))
        assertThat(level.getBlockEntity(pos)).isInstanceOf(BlockEntityEnderChest::class.java)
    }

    @BeforeEach
    fun setUp() {
        lenient().`when`(player.getLevel()).thenReturn(level)
    }
}