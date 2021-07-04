package cn.nukkit.block

import cn.nukkit.blockstate.BlockState

@ExtendWith(PowerNukkitExtension::class)
@MockServer(callsRealMethods = false)
internal class BlockLeaves2Test {
    @Test
    fun issue482() {
        val current: BlockState = BlockState.of(BlockID.LEAVES2, 11)
        assertThrows(InvalidBlockStateException::class.java, current::getBlock)
    }
}