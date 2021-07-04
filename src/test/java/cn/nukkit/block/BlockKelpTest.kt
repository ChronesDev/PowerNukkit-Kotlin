package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
@MockServer(callsRealMethods = false)
internal class BlockKelpTest {
    @Mock
    var level: Level? = null

    @Mock
    var player: Player? = null
    @Test
    fun onActivate() {
        val kelp = BlockKelp()
        kelp.position(Position(2, 3, 4, level))
        val boneMeal: Item = MinecraftItemID.BONE_MEAL.get(5)
        `when`(level.getBlock(eq(2), eq(3), eq(4))).thenReturn(kelp)
        `when`(level.getBlockStateAt(eq(2), eq(4), eq(4))).thenReturn(BlockState.of(BlockID.WATER))
        `when`(level.getBlock(eq(2), eq(4), eq(4), eq(0)))
                .thenReturn(BlockState.of(BlockID.WATER).getBlock(level, 2, 4, 4))
        assertTrue(kelp.onActivate(boneMeal, player))
        assertEquals(4, boneMeal.getCount())
    }
}